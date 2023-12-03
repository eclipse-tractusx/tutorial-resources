package com.eclipse.mxd.util;

import java.security.SecureRandom;

public class RandomWordUtil {

    public static String generateRandomWord() {
        String characters = "abcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        int length = random.nextInt(8) + 1;
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

}
