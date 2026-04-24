package com.example.demo.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "secret")
public record SecretProperties(Integer lunghezzaSecret, String caratteri) {

}
