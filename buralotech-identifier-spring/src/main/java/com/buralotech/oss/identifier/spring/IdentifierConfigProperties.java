package com.buralotech.oss.identifier.spring;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("buralotech.identifier")
public record IdentifierConfigProperties(@Nullable String generator) {
}
