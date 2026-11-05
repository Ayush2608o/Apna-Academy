package com.apnaacademy.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class AcademyDataServiceCertificateTest {
    @Mock
    private JdbcTemplate jdbc;

    @InjectMocks
    private AcademyDataService data;

    @Test
    void verifiesByTheCertificateNumberInTheApplicationDatabase() {
        when(jdbc.queryForList(anyString(), eq("APNA-2026-AB12CD34"))).thenReturn(List.of());

        data.verifyCertificate("APNA-2026-AB12CD34");

        ArgumentCaptor<String> query = ArgumentCaptor.forClass(String.class);
        verify(jdbc).queryForList(query.capture(), eq("APNA-2026-AB12CD34"));
        assertTrue(query.getValue().contains("SELECT cert.certificate_number"));
        assertTrue(query.getValue().contains("cert.certificate_number = ?"));
        assertTrue(query.getValue().contains("JOIN students"));
        assertTrue(query.getValue().contains("LEFT JOIN courses"));
        assertFalse(query.getValue().contains("certificate_system"));
    }

    @Test
    void issuesCertificatesUsingTheExistingSchemaColumns() {
        when(jdbc.queryForObject(anyString(), eq(Long.class), eq(42L))).thenReturn(7L);
        when(jdbc.update(anyString(), any(Object[].class))).thenReturn(1);

        String certificateNumber = data.issueCertificate(42L);

        ArgumentCaptor<String> query = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> parameters = ArgumentCaptor.forClass(Object[].class);
        verify(jdbc).update(query.capture(), parameters.capture());
        assertTrue(query.getValue().contains("certificate_number"));
        assertTrue(query.getValue().contains("CURRENT_DATE()"));
        assertTrue(certificateNumber.matches("APNA-\\d{4}-[A-F0-9]{8}"));
        assertTrue(parameters.getValue()[2].equals(certificateNumber));
    }
}
