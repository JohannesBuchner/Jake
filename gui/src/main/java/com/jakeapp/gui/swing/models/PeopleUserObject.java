package com.jakeapp.gui.swing.models;

import java.util.Random;

/**
 * User: studpete
 * Date: Dec 9, 2008
 * Time: 1:53:13 AM
 */
public final class PeopleUserObject implements PeopleTreeTableNodeInterface {
    String myString1 = RandomGenerator.generateRandomString(10);
    String myString2 = RandomGenerator.generateRandomString(10);
    String myString3 = RandomGenerator.generateRandomString(10);

    public Object getColumn1() {
        return myString1;
    }

    public Object getColumn2() {
        return myString2;
    }

    public Object getColumn3() {
        return myString3;
    }


    @Override
    public void setColumn1(Object myValue) {
        myString1 = myValue.toString();
    }

    @Override
    public void setColumn2(Object myValue) {
        myString2 = myValue.toString();
    }

    @Override
    public void setColumn3(Object myValue) {
        myString3 = myValue.toString();
    }
}


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