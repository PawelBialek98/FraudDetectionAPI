package com.example;

import io.smallrye.jwt.build.Jwt;

import java.time.Duration;
import java.time.Instant;

public class GenerateToken {

    public static void main(String[] args) {
        System.out.println(generateToken());
        System.exit(0);
    }

    public static String generateToken() {
        return Jwt.issuer("some-issuer")
                .subject("testUser")
                .groups("User")
                .expiresAt(Instant.now().plus(Duration.ofHours(2)))
                .sign();
    }
}