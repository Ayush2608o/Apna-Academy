package com.apnaacademy.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

class SecurityConfigCorsTest {
    @Test
    void allowsOnlyTheConfiguredNetlifyOrigin() {
        CorsConfigurationSource source =
                new SecurityConfig().corsConfigurationSource("https://academy.netlify.app");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/certificate/verify");

        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertEquals("https://academy.netlify.app", configuration.checkOrigin("https://academy.netlify.app"));
        assertFalse(configuration.getAllowedOrigins().contains("*"));
        assertTrue(configuration.getAllowCredentials());
    }

    @Test
    void doesNotAllowOriginsWhenTheCorsVariableIsEmpty() {
        CorsConfigurationSource source = new SecurityConfig().corsConfigurationSource("");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/");

        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertNull(configuration.checkOrigin("https://academy.netlify.app"));
    }
}
