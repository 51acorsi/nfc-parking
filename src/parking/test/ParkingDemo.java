package parking.test;

import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.CardTerminal;

import org.nfctools.examples.TerminalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parking.db.HibernateSession;
import parking.db.User;
import parking.db.UserEntry;
import parking.ptm.EntryPTM;
import parking.ptm.ExitPTM;
import parking.ptm.ParkingTicketMachine;
import parking.ptm.ParkingTicketMachine.IEntryPTMEvents;
import parking.ptm.ParkingTicketMachine.IExitPTMEvents;
import parking.ptm.ParkingTicketMachine.PTMMode;
import parking.ptm.ParkingTicketMachine.PTMMsgType;

public class ParkingDemo implements IEntryPTMEvents, IExitPTMEvents{

	private List<ParkingTicketMachine> ticketMachineList = new ArrayList<ParkingTicketMachine>();
	private UserEntry userEntry;
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) {
		new ParkingDemo().run();
	}

	public void run() {
		prepareDB();
		createParkingLot();
	}

	private void prepareDB() {

		//Create Users
		User user1 = new User("felipe.01", "Felipe");
		User user2 = new User("felipe.02", "Felipe 2");

		HibernateSession.getSession().beginTransaction();

		HibernateSession.getSession().save(user1);
		HibernateSession.getSession().save(user2);

		HibernateSession.getSession().getTransaction().commit();
		HibernateSession.getSession().close();
	}

	private void createParkingLot() {

		//List<ParkingTicketMachine> ticketMachineList = new ArrayList<ParkingTicketMachine>();

		// Create Entry PTM
		this.createEntryPTM();
		
		initializeTicketMachines();

//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// stopTicketMachines(ticketMachineList);
	}

	private void createEntryPTM()
	{
		ParkingTicketMachine ptm = createPTM(PTMMode.ENTRY);
		this.ticketMachineList.add(ptm);
	}
	
	private void createExitPTM()
	{
		ParkingTicketMachine ptm = createPTM(PTMMode.EXIT);
		this.ticketMachineList.add(ptm);
	}
	
	private void initializeTicketMachines() {
		for (ParkingTicketMachine ptm : this.ticketMachineList) {
			
			if (ptm instanceof EntryPTM)
			{
				//((EntryPTM)ptm).addEventListener(this);
			}
			
			if (ptm instanceof ExitPTM)
			{
				//((ExitPTM)ptm).addEventListener(this);
			}
			
			ptm.addEventListener(this);
			
			Thread t = new Thread(ptm);
			t.start();
		}
	}

	private void stopTicketMachines() {
		for (ParkingTicketMachine ptm : this.ticketMachineList) {
			ptm.stop();
		}
	}
	
	private void removePTMS()
	{
		for (ParkingTicketMachine ptm : this.ticketMachineList) {
			if (ptm instanceof EntryPTM)
			{
				((EntryPTM)ptm).removeEventListener(this);
			}			
		}
		
		this.ticketMachineList.clear();		
	}

	private ParkingTicketMachine createPTM(PTMMode mode) {

		CardTerminal cardTerminal = TerminalUtils.getAvailableTerminal()
				.getCardTerminal();

		switch (mode) {
		case ENTRY:

			return new EntryPTM(cardTerminal);

		case EXIT:

			return new ExitPTM(cardTerminal);

		default:
			return null;
		}
	}

	@Override
	public void onUserEntry(ParkingTicketMachine ptm, UserEntry uEntry) {
		log.info("User " + uEntry.getUser().getUserName() + " logged");
		userEntry = uEntry;
		
		//Stop Entry PTMs
		this.stopTicketMachines();
		this.removePTMS();
		
		//Create new Exit PTM
		createExitPTM();
		this.initializeTicketMachines();		
	}
	
	@Override
	public void onUserExit(ParkingTicketMachine ptm, UserEntry uEntry) {
		log.info("User " + uEntry.getUser().getUserName() + " paid");
		userEntry = null;
		
		//Stop Exit PTMs
		this.stopTicketMachines();
		this.removePTMS();
		
		//Create new Entry PTM
		createEntryPTM();
		this.initializeTicketMachines();		
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		HibernateSession.close();
	}

	@Override
	public void onBoomGateOpened(ParkingTicketMachine ptm) {
		//No Action
	}

	@Override
	public void onBoomGateClosed(ParkingTicketMachine ptm) {
		//No Action
	}

	@Override
	public void onTerminalMessage(ParkingTicketMachine PTM, String msg, PTMMsgType msgType) {
		// TODO Auto-generated method stub
		
	}
}
