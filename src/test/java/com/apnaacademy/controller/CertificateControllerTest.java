package com.apnaacademy.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.apnaacademy.service.AcademyDataService;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.io.ByteArrayInputStream;
import java.util.Map;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;

class CertificateControllerTest {
    private final AcademyDataService data = org.mockito.Mockito.mock(AcademyDataService.class);
    private final CertificateController controller =
            new CertificateController(data, "https://academy.netlify.app/");

    @Test
    void scannedQrCodeOpensThePublicVerificationPageForItsCertificate() throws Exception {
        String certificateNumber = "APNA-2026-AB12CD34";
        when(data.certificate(certificateNumber)).thenReturn(Map.of("certificate_number", certificateNumber));

        ResponseEntity<byte[]> response = controller.qrCode(certificateNumber);
        var image = ImageIO.read(new ByteArrayInputStream(response.getBody()));
        String scannedText = new MultiFormatReader().decode(
                new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)))).getText();

        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
        assertEquals("https://academy.netlify.app/certificate/verify?certificateNumber=APNA-2026-AB12CD34", scannedText);
        verify(data).certificate(certificateNumber);
    }

    @Test
    void qrVerificationLinkAutomaticallyLooksUpTheCertificateNumber() {
        String certificateNumber = "APNA-2026-AB12CD34";
        when(data.hasCertificates()).thenReturn(true);
        when(data.verifyCertificate(certificateNumber)).thenReturn(java.util.List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        controller.form(certificateNumber, model);

        assertEquals(certificateNumber, model.get("certificateNumber"));
        assertEquals(java.util.List.of(), model.get("matches"));
        verify(data).verifyCertificate(certificateNumber);
    }
}
