package parking.ptm;

import javax.smartcardio.CardTerminal;

import org.nfctools.spi.acs.AbstractTerminalTagScanner;

import parking.db.User;
import parking.db.UserEntry;

public abstract class ParkingTicketMachine extends AbstractTerminalTagScanner{

	protected BoomGate boomGate;
	protected boolean interrupted;
	
	public enum PTMMode {
		 ENTRY,
		 EXIT
		}
	
	public ParkingTicketMachine (CardTerminal cardTerminal)
	{
		super(cardTerminal);		
		boomGate = new BoomGate();
		interrupted = false;		
	}
	
	public abstract void initialize ();
	
	public abstract void stop();	
	
	//Events
	public interface IOnUserEntry {
		public void onUserEntry(ParkingTicketMachine ptm, UserEntry uEntry);
	}
	
	public interface IOnUserExit {
		public void onUserExit(ParkingTicketMachine ptm, UserEntry uEntry);
	}	
}
