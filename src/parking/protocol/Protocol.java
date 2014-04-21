package parking.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.codec.binary.Hex;

public abstract class Protocol implements IProtocol{
	
	public enum PaymentMethod
	{
		BY_ENTRY,
		BY_HOUR;
	}	
	
	private Logger log;
	
	public Protocol ()
	{
		log = LoggerFactory.getLogger(getClass());
	}
	
	public void IdentifyCommand (byte[] dataIn)
	{
		if (dataIn.length < 4) return;
		
		//Check command start
		if (dataIn[0] != con_start)
		{
			log.info("Wrong start command");
		}
		
		switch (dataIn[1])
		{
		case con_cmd_get:
			this.getUserId();
			break;
		
		default:
			log.info("Unknow command: " + Hex.encodeHexString(dataIn));
			break;
		}
		
	}
	
	protected abstract void getUserId ();

}