package com.jakeapp.gui.swing.models;

import java.util.Random;


class RandomGenerator {
    private static final Random theRandomizer = new Random();
    private static final int theCharRange = 1 + 'Z' - 'A';

    public static String generateRandomString(int myI) {
        StringBuffer myBuffer = new StringBuffer();
        while (myI-- > 0) {
            myBuffer
                    .append((char) ((Math.abs(theRandomizer.nextInt()) % theCharRange) + 'A' - 1));
        }
        return myBuffer.toString();
    }
}