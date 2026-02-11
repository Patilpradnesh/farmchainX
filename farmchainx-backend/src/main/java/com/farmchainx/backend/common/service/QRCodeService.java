package com.farmchainx.backend.common.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * QR Code Generation Service for Crop Traceability
 */
@Service
public class QRCodeService {

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Generate QR code for crop traceability
     * @param cropId Crop ID
     * @param blockchainHash Blockchain transaction hash
     * @return Base64 encoded QR code image
     */
    public String generateCropQRCode(Long cropId, String blockchainHash) {
        try {
            // Create trace URL that frontend will handle
            String traceUrl = String.format("%s/trace/%s", frontendUrl, blockchainHash);

            return generateQRCodeImage(traceUrl, 300, 300);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Generate QR code for shipment tracking
     */
    public String generateShipmentQRCode(String trackingNumber) {
        try {
            String trackUrl = String.format("%s/track/%s", frontendUrl, trackingNumber);
            return generateQRCodeImage(trackUrl, 300, 300);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate shipment QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Generate QR code for order
     */
    public String generateOrderQRCode(Long orderId) {
        try {
            String orderUrl = String.format("%s/order/%d", frontendUrl, orderId);
            return generateQRCodeImage(orderUrl, 300, 300);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate order QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Core QR code generation method
     */
    private String generateQRCodeImage(String text, int width, int height)
            throws WriterException, IOException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Convert to Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();

        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Generate QR code data URL for direct embedding in HTML/PDF
     */
    public Map<String, String> generateQRCodeData(String type, String identifier) {
        Map<String, String> result = new HashMap<>();

        try {
            String qrCodeBase64;
            String url;

            switch (type.toLowerCase()) {
                case "crop":
                    url = String.format("%s/trace/%s", frontendUrl, identifier);
                    qrCodeBase64 = generateQRCodeImage(url, 300, 300);
                    break;
                case "shipment":
                    url = String.format("%s/track/%s", frontendUrl, identifier);
                    qrCodeBase64 = generateQRCodeImage(url, 300, 300);
                    break;
                case "order":
                    url = String.format("%s/order/%s", frontendUrl, identifier);
                    qrCodeBase64 = generateQRCodeImage(url, 300, 300);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid QR code type: " + type);
            }

            result.put("qrCode", qrCodeBase64);
            result.put("url", url);
            result.put("type", type);
            result.put("identifier", identifier);

            return result;
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code data: " + e.getMessage(), e);
        }
    }
}
