package com.framelibrary.exception;

import com.framelibrary.exception.*;

public class MaigetRuntimeException extends RuntimeException implements BasePinganExcetion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MaigetRuntimeException() {
		super();
	}

	public MaigetRuntimeException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MaigetRuntimeException(String detailMessage) {
		super(detailMessage);
	}

	public MaigetRuntimeException(Throwable throwable) {
		super(throwable);
	}
	
}
