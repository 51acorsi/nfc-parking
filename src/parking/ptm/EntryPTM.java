package parking.ptm;

import java.io.IOException;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import org.nfctools.api.TagScannerListener;
import org.nfctools.api.TagType;
import org.nfctools.scio.TerminalStatus;
import org.nfctools.spi.acs.ApduTagReaderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parking.exception.UserIdNotFound;
import parking.ptm.nfc.AcsDirectChannelTag;
import parking.ptm.nfc.HostCardEmulationTagScanner;
import parking.ptm.nfc.IsoDepTamaCommunicator;

public class EntryPTM extends ParkingTicketMachine {

	private Logger log = LoggerFactory.getLogger(getClass());
	private IsoDepTamaCommunicator tamaCommunicator;
	
	//private HostCardEmulationTagScanner tagScanner;

	public EntryPTM(CardTerminal cardTerminal) {
		super(cardTerminal);
	}

	@Override
	public void initialize() {
		
	}

	@Override
	public void run() {
		while (!this.interrupted) {
			notifyStatus(TerminalStatus.WAITING);
			try {
				Card card = cardTerminal.connect("direct");
				ApduTagReaderWriter readerWriter = new ApduTagReaderWriter(new AcsDirectChannelTag(TagType.ISO_DEP, null, card));
				
				try {
					this.tamaCommunicator = new IsoDepTamaCommunicator(readerWriter, readerWriter);
					
					//Connect reader as initiator			
					log.info("Connecting reader as initiator");
					this.tamaCommunicator.connectAsInitiator();
					
					//Authenticate User Id
					log.info("Authenticating User");
					this.authUserID(this.tamaCommunicator.getUserId());
					log.info("User authenticated");
					
					//Register User
					
					//Open Boom Gate
					this.boomGate.open();
					
					Thread.sleep(1500);
					
					//Close Boom Gate
					this.boomGate.close();
							
					
				} catch (Exception e1) {
					card.disconnect(true);
					e1.printStackTrace();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} finally {
					card.disconnect(true);
				}
			} catch (CardException e) {
				e.printStackTrace();
			}			
		}
	}

	private void authUserID (String uId) throws IOException
	{		
		if (!uId.equals("s4felipe"))
		{
			throw new UserIdNotFound("Authentication not received");
		}		
	}
	
	@Override
	public void stop() {
		this.interrupted = true;
		this.tamaCommunicator.stop();
	}
}
