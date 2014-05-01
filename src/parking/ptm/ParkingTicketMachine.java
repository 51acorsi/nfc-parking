package parking.ptm;

import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.CardTerminal;

import org.nfctools.spi.acs.AbstractTerminalTagScanner;

import parking.db.User;
import parking.db.UserEntry;

public abstract class ParkingTicketMachine extends AbstractTerminalTagScanner{

	protected BoomGate boomGate;
	protected boolean interrupted;
	protected List<IPTMEvents> onPTMListeners = new ArrayList<IPTMEvents>();
	
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
	
	// Events
	public synchronized void addEventListener(IEntryPTMEvents listener) {
		onPTMListeners.add(listener);
	}

	public synchronized void removeEventListener(IEntryPTMEvents listener) {
		onPTMListeners.remove(listener);
	}
	
	protected void openGate() {
		this.boomGate.open();
		this.notifyGateOpened();
	}

	protected void closeGate() {
		this.boomGate.close();
		this.notifyGateClosed();
	}
	
	protected void notifyGateOpened() {
		List<IPTMEvents> callListeners = new ArrayList<IPTMEvents>(onPTMListeners);

		for (Object listener : callListeners) {
			((IPTMEvents) listener).onBoomGateOpened(this);
		}
	}
	
	protected void notifyGateClosed() {
		List<IPTMEvents> callListeners = new ArrayList<IPTMEvents>(onPTMListeners);

		for (Object listener : callListeners) {
			((IPTMEvents) listener).onBoomGateClosed(this);
		}
	}
	
	//Events
	public interface IPTMEvents{
		public void onBoomGateOpened(ParkingTicketMachine ptm);
		public void onBoomGateClosed(ParkingTicketMachine ptm);
	}
	
	public interface IEntryPTMEvents extends IPTMEvents {
		public void onUserEntry(ParkingTicketMachine ptm, UserEntry uEntry);
	}
	
	public interface IExitPTMEvents extends IPTMEvents{
		public void onUserExit(ParkingTicketMachine ptm, UserEntry uEntry);
	}	
}
