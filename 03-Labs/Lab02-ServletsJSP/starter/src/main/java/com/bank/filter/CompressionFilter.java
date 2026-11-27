package com.bank.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

@WebFilter(filterName = "CompressionFilter", urlPatterns = "/*")
public class CompressionFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(CompressionFilter.class.getName());
    private static final int MIN_COMPRESSION_SIZE = 1024;
    private static final String[] COMPRESSIBLE_TYPES = {
        "text/html", "text/css", "text/javascript", "application/javascript",
        "application/json", "application/xml", "text/xml", "text/plain"
    };

    @Override
    public void init(FilterConfig filterConfig) {
        LOGGER.info("CompressionFilter initialized; minimum size: " + MIN_COMPRESSION_SIZE);
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (!acceptsGzip(httpRequest.getHeader("Accept-Encoding"))) {
            chain.doFilter(request, response);
            return;
        }

        CompressionResponseWrapper wrappedResponse = new CompressionResponseWrapper(httpResponse);
        try {
            chain.doFilter(request, wrappedResponse);
        } finally {
            wrappedResponse.finishResponse();
        }
        if (wrappedResponse.isPassthrough()) {
            return;
        }

        byte[] content = wrappedResponse.getCapturedContent();
        String contentType = wrappedResponse.getContentType();
        if (shouldCompress(content, contentType)) {
            byte[] compressedContent = compress(content);
            if (!httpResponse.isCommitted()) {
                httpResponse.setHeader("Content-Encoding", "gzip");
                httpResponse.setIntHeader("Content-Length", compressedContent.length);
                httpResponse.addHeader("Vary", "Accept-Encoding");
            }
            httpResponse.getOutputStream().write(compressedContent);
            LOGGER.info("Response compressed: " + content.length + " bytes -> "
                    + compressedContent.length + " bytes");
        } else {
            if (!httpResponse.isCommitted()) {
                httpResponse.setIntHeader("Content-Length", content.length);
                httpResponse.addHeader("Vary", "Accept-Encoding");
            }
            httpResponse.getOutputStream().write(content);
        }
    }

    private boolean shouldCompress(byte[] content, String contentType) {
        if (content.length < MIN_COMPRESSION_SIZE || contentType == null) {
            return false;
        }
        String normalizedType = contentType.toLowerCase(Locale.ROOT);
        for (String type : COMPRESSIBLE_TYPES) {
            if (normalizedType.startsWith(type)) {
                return true;
            }
        }
        return false;
    }

    private byte[] compress(byte[] content) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(content);
        }
        return byteStream.toByteArray();
    }

    private boolean acceptsGzip(String acceptEncoding) {
        if (acceptEncoding == null) {
            return false;
        }
        for (String encoding : acceptEncoding.split(",")) {
            String[] parts = encoding.trim().split(";");
            if ("gzip".equalsIgnoreCase(parts[0].trim())) {
                for (int i = 1; i < parts.length; i++) {
                    if ("q=0".equalsIgnoreCase(parts[i].trim())
                            || "q=0.0".equalsIgnoreCase(parts[i].trim())) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static class CompressionResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        private PrintWriter writer;
        private ServletOutputStream servletOutputStream;
        private boolean passthrough;

        CompressionResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called");
            }
            if (servletOutputStream == null) {
                servletOutputStream = new ServletOutputStream() {
                    @Override
                    public void write(int value) {
                        outputStream.write(value);
                    }

                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(WriteListener listener) {
                        if (listener == null) {
                            throw new IllegalArgumentException("WriteListener must not be null");
                        }
                        try {
                            listener.onWritePossible();
                        } catch (IOException e) {
                            listener.onError(e);
                        }
                    }
                };
            }
            return servletOutputStream;
        }

        @Override
        public PrintWriter getWriter() {
            if (servletOutputStream != null) {
                throw new IllegalStateException("getOutputStream() has already been called");
            }
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(
                        outputStream, Charset.forName(getCharacterEncoding())), true);
            }
            return writer;
        }

        byte[] getCapturedContent() {
            if (writer != null) {
                writer.flush();
            }
            return outputStream.toByteArray();
        }

        void finishResponse() {
            if (writer != null) {
                writer.flush();
            }
        }

        @Override
        public void flushBuffer() throws IOException {
            if (writer != null) {
                writer.flush();
            }
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void resetBuffer() {
            outputStream.reset();
        }

        @Override
        public void sendError(int status) throws IOException {
            passthrough = true;
            super.sendError(status);
        }

        @Override
        public void sendError(int status, String message) throws IOException {
            passthrough = true;
            super.sendError(status, message);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            passthrough = true;
            super.sendRedirect(location);
        }

        boolean isPassthrough() {
            return passthrough;
        }
    }
}
