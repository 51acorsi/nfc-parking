package parking.ptm;

import javax.smartcardio.CardTerminal;
import parking.ptm.nfc.HostCardEmulationTagScanner;

public class EntryPTM extends ParkingTicketMachine {

	public EntryPTM(CardTerminal cardTerminal) {
		super(cardTerminal);
	}

	@Override
	public void Initialize() {
		HostCardEmulationTagScanner tagScanner = new HostCardEmulationTagScanner(this.cardTerminal);
		
	}

	@Override
	public void Stop() {
		// TODO Auto-generated method stub
		
	}

}
