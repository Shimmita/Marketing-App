package com.shimitadouglas.marketcm.utilities;

import java.util.Random;

public class JavaProductIDGenerator {
    public static final String upperCaseCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lowerCaseCharacters = "abcdefghijklmnopqrstuvwxyz";
    public static final String numberCharacters = "1234567890";

    public static String generateProductIDNow(
            int passwordLength,
            boolean useUpperCaseCharacters,
            boolean useLowerCaseCharacters,
            boolean useNumbersCharacters
    ) {
        char[] password = new char[passwordLength];
        String charSet = null;
        Random random = new Random();

        if (useUpperCaseCharacters) charSet += upperCaseCharacters;
        if (useLowerCaseCharacters) charSet += lowerCaseCharacters;
        if (useNumbersCharacters) charSet += numberCharacters;

        for (int i = 0; i < passwordLength; i++) {
            password[i] = charSet.toCharArray()[random.nextInt(charSet.length() - 1)];
        }
        return String.valueOf(password);
    }
}
