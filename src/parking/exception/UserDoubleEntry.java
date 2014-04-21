package parking.exception;

import java.io.IOException;

public class UserDoubleEntry extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserDoubleEntry(String msg) {
		super(msg);
	}

}
