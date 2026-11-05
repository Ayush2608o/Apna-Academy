package com.apnaacademy.controller;

import com.apnaacademy.service.AcademyDataService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class CertificateController {
    private final AcademyDataService data;
    private final String publicUrl;

    public CertificateController(AcademyDataService data,
                                 @Value("${academy.public-url:}") String publicUrl) {
        this.data = data;
        this.publicUrl = publicUrl;
    }

    @GetMapping("/certificate/verify")
    public String form(@RequestParam(required = false) String certificateNumber, Model model) {
        if (certificateNumber != null && !certificateNumber.isBlank()) {
            showVerification(certificateNumber, model);
        }
        return "certificate-lookup";
    }

    @PostMapping("/certificate/verify")
    public String verify(@RequestParam String certificateNumber, Model model) {
        showVerification(certificateNumber, model);
        return "certificate-lookup";
    }

    @GetMapping("/certificate/{certificateNumber}")
    public String printable(@PathVariable String certificateNumber, Model model) {
        model.addAttribute("certificate", findCertificate(certificateNumber));
        return "certificate-print";
    }

    @GetMapping(value = "/certificate/{certificateNumber}/qr.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> qrCode(@PathVariable String certificateNumber)
            throws WriterException, IOException {
        findCertificate(certificateNumber);
        String baseUrl = publicUrl.isBlank()
                ? ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
                : publicUrl.replaceAll("/+$", "");
        String verificationUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/certificate/verify")
                .queryParam("certificateNumber", certificateNumber)
                .build()
                .encode()
                .toUriString();
        ByteArrayOutputStream image = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(
                new QRCodeWriter().encode(verificationUrl, BarcodeFormat.QR_CODE, 320, 320),
                "PNG", image);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.noCache())
                .body(image.toByteArray());
    }

    private Map<String, Object> findCertificate(String certificateNumber) {
        try {
            return data.certificate(certificateNumber);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found", ex);
        }
    }

    private void showVerification(String certificateNumber, Model model) {
        String normalizedNumber = certificateNumber.trim();
        boolean hasCertificates = data.hasCertificates();
        model.addAttribute("certificateNumber", normalizedNumber);
        model.addAttribute("unavailable", !hasCertificates);
        model.addAttribute("matches", hasCertificates
                ? data.verifyCertificate(normalizedNumber) : List.<Map<String, Object>>of());
    }
}
