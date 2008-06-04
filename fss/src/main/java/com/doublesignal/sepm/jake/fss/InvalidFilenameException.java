package com.doublesignal.sepm.jake.fss;

/**
 * If a filename is not acceptable for all operating systems or filesystem 
 * (check with isValidRelpath).
 * @author johannes
 *
 */
@SuppressWarnings("serial")
public class InvalidFilenameException extends Exception {

    public InvalidFilenameException(String s) {
        super(s);
    }
}
