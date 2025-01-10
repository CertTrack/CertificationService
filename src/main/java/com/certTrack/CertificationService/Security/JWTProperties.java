package com.certTrack.CertificationService.Security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;


@Data
@Configuration
@ConfigurationProperties("security.jwt")
public class JWTProperties {
    private String secretKey;
    private Duration tokenDuration;
}