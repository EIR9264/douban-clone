package com.douban.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final int SALT_LENGTH = 16;

    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String storedHash) {
        try {
            byte[] combined = Base64.getDecoder().decode(storedHash);

            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            for (int i = 0; i < hashedPassword.length; i++) {
                if (combined[SALT_LENGTH + i] != hashedPassword[i]) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
