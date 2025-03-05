package com.example;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;

import io.smallrye.jwt.build.Jwt;

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