package parking.ptm.nfc;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

import org.nfctools.api.TagType;
import org.nfctools.scio.TerminalStatus;
import org.nfctools.spi.acs.AbstractTerminalTagScanner;
import org.nfctools.spi.acs.ApduTagReaderWriter;

public class HostCardEmulationTagScanner extends AbstractTerminalTagScanner {

	private IsoDepTamaCommunicator tamaCommunicator;
	private boolean interrupted = false;

	public HostCardEmulationTagScanner(CardTerminal cardTerminal) {
		super(cardTerminal);
	}

	@Override
	public void run() {
		notifyStatus(TerminalStatus.WAITING);
		try {
			Card card = cardTerminal.connect("direct");
			ApduTagReaderWriter readerWriter = new ApduTagReaderWriter(new AcsDirectChannelTag(TagType.ISO_DEP, null, card));
			
			try {
				this.tamaCommunicator = new IsoDepTamaCommunicator(readerWriter, readerWriter);
				this.tamaCommunicator.connectAsInitiator();
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

	public void stop() {
		this.tamaCommunicator.stop();
	}
}
