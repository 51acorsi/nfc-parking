package parking.exception;

import java.io.IOException;

public class ParkingException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ParkingException (String msg)
	{
		super(msg);
	}

}
