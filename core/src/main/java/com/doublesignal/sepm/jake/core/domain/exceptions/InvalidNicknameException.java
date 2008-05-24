package com.doublesignal.sepm.jake.core.domain.exceptions;

/**
 * @author domdorn
 */
public class InvalidNicknameException extends Exception {
	public InvalidNicknameException(String s) {
		super(s);
	}

	public InvalidNicknameException() {
		super();	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidNicknameException(String s, Throwable throwable) {
		super(s, throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public InvalidNicknameException(Throwable throwable) {
		super(throwable);	//To change body of overridden methods use File | Settings | File Templates.
	}
}
