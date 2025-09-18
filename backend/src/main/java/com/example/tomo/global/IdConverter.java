package com.example.tomo.global;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IdConverter {

    // 문자열 → Long
    public static long stringToLong(String str) {
        if (str == null) {
            str = ""; // null이면 빈 문자열 처리
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(str.getBytes(StandardCharsets.UTF_8));

            // 첫 8바이트만 사용해 long으로 변환
            long result = 0;
            for (int i = 0; i < 8; i++) {
                result = (result << 8) | (hash[i] & 0xff);
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
