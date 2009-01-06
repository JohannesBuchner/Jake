package com.jakeapp.gui.swing.exceptions;

/**
 * The number of arguments to fill in was not correct.
 *
 * @author: studpete
 */
@SuppressWarnings("serial")
public class IllegalNumberOfArgumentsException extends RuntimeException {
    public IllegalNumberOfArgumentsException(String message) {
        super(message);
    }
}
