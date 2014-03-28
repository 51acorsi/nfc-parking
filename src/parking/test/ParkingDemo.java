package parking.test;

import javax.smartcardio.CardTerminal;
import org.nfctools.examples.TerminalUtils;
import parking.ptm.EntryPTM;
import parking.ptm.ExitPTM;
import parking.ptm.ParkingTicketMachine;
import parking.ptm.ParkingTicketMachine.PTMMode;

public class ParkingDemo {

	public static void main(String[] args) {
		new ParkingDemo().run();
	}

	public void run() {
		createParkingLot();
	}

	private void createParkingLot() {
		// Create Entry PTM
		createPTM(PTMMode.ENTRY).Initialize();

		// Create Exit PTM
	}

	private ParkingTicketMachine createPTM(PTMMode mode) {

		CardTerminal cardTerminal = TerminalUtils.getAvailableTerminal().getCardTerminal();
				
		switch (mode) {
		case ENTRY:

			return new EntryPTM(cardTerminal);

		case EXIT:
			
			return new ExitPTM(cardTerminal);

		default:
			return null;
		}
	}

}
