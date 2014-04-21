package parking.exception;

import java.io.IOException;

public class UserIdNotFound extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7432665415955361952L;

	public UserIdNotFound(String msg) {
		super(msg);
	}
}
