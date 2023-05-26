package com.example.kan_it.core;

import android.os.Build;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class StringCore {
    public static String strToBase64(String str) {
        byte[] data = str.getBytes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(data);
        } else {
            throw new RuntimeException("Current android version not support");
        }
    }

    public static String base64ToStr(String base64String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            byte[] data = Base64.getDecoder().decode(base64String);
            return new String(data, StandardCharsets.UTF_8);
        } else {
            throw new RuntimeException("Current android version not support");
        }
    }
}
