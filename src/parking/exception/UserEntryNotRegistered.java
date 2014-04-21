package parking.exception;

import java.io.IOException;

public class UserEntryNotRegistered extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UserEntryNotRegistered(String msg) {
		super(msg);
	}

}
