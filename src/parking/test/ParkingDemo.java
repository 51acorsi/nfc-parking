package parking.test;

import java.util.ArrayList;
import java.util.List;

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
		List<ParkingTicketMachine> ticketMachineList = new ArrayList<ParkingTicketMachine>();
		
		// Create Entry PTM		
		EntryPTM entry = (EntryPTM) createPTM(PTMMode.ENTRY);
		
		ticketMachineList.add(entry);

		
		// Create Exit PTM
		
		
		
		initializeTicketMachines(ticketMachineList);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//stopTicketMachines(ticketMachineList);
	}

	private void initializeTicketMachines(List<ParkingTicketMachine> ticketMachineList) {
		for (ParkingTicketMachine ptm : ticketMachineList) {
			
			Thread t = new Thread(ptm);
			t.start();
			
		}
	}
	
	private void stopTicketMachines(List<ParkingTicketMachine> ticketMachineList) {
		for (ParkingTicketMachine ptm : ticketMachineList) {
			
			ptm.stop();
			
		}
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
