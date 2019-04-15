package com.tinysand.system.filters;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.BiConsumer;

@WebFilter(filterName = "waterMarker", urlPatterns = {"/image"},
        initParams = {
        @WebInitParam(name = "enabled", value = "true"),
        @WebInitParam(name = "waterMarkText", value = "TINYSAND"),
        @WebInitParam(name = "waterMarkImage", value =
                "")
})
public class WaterMarker extends HttpFilter {
    @Override
    public void doFilter(HttpServletRequest request,
                         HttpServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {
        if (enabled) {
            final String waterMark = (Objects.nonNull(waterMarkImage) &&
                    !waterMarkImage.isEmpty()) ?
                    getServletContext().getRealPath(waterMarkImage)
                    : waterMarkText;
            Objects.requireNonNull(waterMark);
            WaterMarkResponseWrapper responseWrapper = new
                    WaterMarkResponseWrapper
                    (response, waterMark, (Objects.nonNull(waterMarkImage) &&
                            !waterMarkImage.isEmpty()) ?
                            PrintType.IMAGE : PrintType.TEXT);
            filterChain.doFilter(request, responseWrapper);
            responseWrapper.handleResponse();
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private class WaterMarkResponseWrapper extends HttpServletResponseWrapper {
        WaterMarkResponseWrapper
                (HttpServletResponse response, String waterMark,
                 PrintType printType) {
            super(response);
            this.printType = printType;
            this.response = response;
            this.waterMark = waterMark;
            this.waterMarkPrinter = new WaterMarkOutputStream();
        }

        public ServletOutputStream getOutputStream() {
            return this.waterMarkPrinter;
        }

        @Override
        public void flushBuffer() throws IOException {
            waterMarkPrinter.flush();
        }

        void handleResponse() throws IOException {
            byte[] imageData = waterMarkPrinter.getOutputStream()
                    .toByteArray();
            byte[] outputImageData = (PrintType.IMAGE == printType) ?
                    printImage(imageData, waterMark) :
                    printText(imageData, waterMark);
            response.getOutputStream().write(outputImageData);
            waterMarkPrinter.close();
        }

        private byte[] printImage(final byte[] originalImageData,
                                  final String waterMarkImagePath)
                throws IOException {
            return printer(originalImageData, (graphics, originImage) -> {
                BufferedImage waterMarkImage;
                try {
                    waterMarkImage = ImageIO.read(Paths
                            .get(waterMarkImagePath).toFile());
                    int waterMarkImageHeight = waterMarkImage.getHeight();
                    int waterMarkImageWidth = waterMarkImage.getWidth();
                    if (MARK_THRESHOLD_HEIGHT <= waterMarkImageHeight &&
                            MARK_THRESHOLD_WIDTH <= waterMarkImageWidth) {
                        return;
                    }
                    waterMarkImage = getScaledImageWithSmooth(waterMarkImage);
                    int originImageWidth = originImage.getWidth(null);
                    int originImageHeight = originImage.getHeight(null);

                    graphics.drawImage(waterMarkImage,
                            (originImageWidth - waterMarkImageWidth
                                    - OFFSET_X),
                            (originImageHeight - waterMarkImageHeight
                                    - OFFSET_Y),
                            waterMarkImageWidth, waterMarkImageHeight,
                            null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        private BufferedImage getScaledImageWithSmooth
                (final BufferedImage originalImage) {
            Image tempImage = originalImage.getScaledInstance
                    (WATER_MARK_IMAGE_WIDTH, WATER_MARK_IMAGE_HEIGHT,
                            BufferedImage.SCALE_SMOOTH);

            double scaledWidth = originalImage.getWidth() * 1.0 /
                    tempImage.getWidth(null);
            double scaledHeight = originalImage.getHeight() * 1.0 /
                    tempImage.getHeight(null);

            AffineTransformOp transformer = new AffineTransformOp
                    (AffineTransform.getScaleInstance
                            (scaledWidth, scaledHeight), null);
            return transformer.filter(originalImage, null);
        }

        private byte[] printer(final byte[] originalImageData,
                               final BiConsumer<Graphics2D, Image> processor)
                throws IOException {
            Image originalImage = new ImageIcon(originalImageData).getImage();
            originalImageHeight = originalImage.getHeight(null);
            originalImageWidth = originalImage.getWidth(null);

            BufferedImage processedImage = new BufferedImage
                    (originalImageWidth, originalImageHeight,
                            BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = processedImage.createGraphics();
            graphics.drawImage(originalImage, 0, 0,
                    originalImageWidth, originalImageHeight, null);
            processor.accept(graphics, originalImage);
            graphics.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(processedImage, IMAGE_EXTENSION, outputStream);

            byte[] imageData = outputStream.toByteArray();
            outputStream.close();
            return imageData;
        }

        private byte[] printText(final byte[] originalImageData,
                                 final String waterMarkText)
                throws IOException {
            return printer(originalImageData, (graphics, image) -> {
                Font waterMarkFont = new Font
                        (FONT_FAMILY, Font.BOLD, FONT_SIZE);
                FontRenderContext fontRenderContext = new
                        FontRenderContext(new AffineTransform(), TRUE, TRUE);
                fontRenderContext.getFractionalMetricsHint();

                Rectangle rectangle = waterMarkFont.getStringBounds
                        (waterMarkText, fontRenderContext).getBounds();
                graphics.setFont(waterMarkFont);
                graphics.setColor(Color.WHITE);
                graphics.setComposite(AlphaComposite
                        .getInstance(AlphaComposite.SRC_ATOP, ALPHA));
                graphics.drawString(waterMarkText,
                        (image.getWidth(null)
                                - rectangle.width - OFFSET_Y),
                        (image.getHeight(null)
                                - rectangle.height - OFFSET_Y));
            });
        }

        private static final String IMAGE_EXTENSION = "jpg";
        private static final String FONT_FAMILY = "Times new Roman";
        private static final int WATER_MARK_IMAGE_WIDTH = 320;
        private static final int WATER_MARK_IMAGE_HEIGHT = 180;
        private static final int MARK_THRESHOLD_WIDTH = 640;
        private static final int MARK_THRESHOLD_HEIGHT = 320;
        private static final int FONT_SIZE = 24;
        private static final int OFFSET_X = 12;
        private static final int OFFSET_Y = 12;
        private static final float ALPHA = 0.8F;
        private static final boolean TRUE = true;
        private int originalImageHeight;
        private int originalImageWidth;
        private final HttpServletResponse response;
        private final PrintType printType;
        private final String waterMark;
        private final WaterMarkOutputStream waterMarkPrinter;
    }

    private class WaterMarkOutputStream extends ServletOutputStream {
        WaterMarkOutputStream() {
            outputStream = new ByteArrayOutputStream();
        }

        @Override
        public void write(int data) {
            outputStream.write(data);
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            outputStream.write(bytes);
        }

        public void write(byte[] bytes, int offset, int length) {
            outputStream.write(bytes, offset, length);
        }

        @Override
        public void close() throws IOException {
            outputStream.close();
        }

        @Override
        public void flush() throws IOException {
            outputStream.flush();
        }

        @Override
        public boolean isReady() {
            return Objects.nonNull(outputStream);
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }

        ByteArrayOutputStream getOutputStream() {
            return this.outputStream;
        }

        private ByteArrayOutputStream outputStream;
    }

    private enum PrintType { IMAGE, TEXT }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        waterMarkImage = filterConfig.getInitParameter("waterMarkImage");
        waterMarkText = filterConfig.getInitParameter("waterMarkText");
        enabled = Objects.nonNull(filterConfig.getInitParameter("enabled")) &&
                filterConfig.getInitParameter("enabled").equals("true");
    }

    private static boolean enabled;
    private static String waterMarkImage;
    private static String waterMarkText;
}
