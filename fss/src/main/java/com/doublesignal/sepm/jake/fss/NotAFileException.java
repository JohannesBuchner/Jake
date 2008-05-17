package com.doublesignal.sepm.jake.fss;

/**
 * Thrown if tried to read a file which is in fact a directory or something else
 * @author johannes
 *
 */
@SuppressWarnings("serial")
public class NotAFileException extends NotAReadableFileException {

}
