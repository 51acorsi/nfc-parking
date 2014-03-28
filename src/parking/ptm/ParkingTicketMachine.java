package parking.ptm;

import javax.smartcardio.CardTerminal;

public abstract class ParkingTicketMachine {

	protected CardTerminal cardTerminal;
	protected BoomGate boomGate;
	
	public enum PTMMode {
		 ENTRY,
		 EXIT
		}
	
	public ParkingTicketMachine (CardTerminal cardTerminal)
	{
		this.cardTerminal = cardTerminal;
		boomGate = new BoomGate();
		
	}
	
	public abstract void Initialize ();
	
	public abstract void Stop();
	
}
