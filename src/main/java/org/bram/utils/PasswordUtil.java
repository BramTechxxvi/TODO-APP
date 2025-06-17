package org.bram.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verifyPassword(String passwordToVerify, String hashedPassword) {
        return BCrypt.verifyer().verify(passwordToVerify.toCharArray(), hashedPassword).verified;
    }
}
