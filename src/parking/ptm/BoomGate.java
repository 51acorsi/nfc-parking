package parking.ptm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoomGate {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public void open()
	{
		log.info("Boom gate opened");
	}
	
	public void close()
	{
		log.info("Boom gate closed");
	}
}
