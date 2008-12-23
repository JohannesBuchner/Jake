package com.doublesignal.sepm.jake.fss.exceptions;

import com.doublesignal.sepm.jake.fss.exceptions.NotAReadableFileException;

/**
 * Filesize exceeds Integer.MAXVALUE (2 Gigabyte)
 * @author johannes
 *
 */
@SuppressWarnings("serial")
public class FileTooLargeException extends NotAReadableFileException {

}
