package parking.ptm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

import org.nfctools.api.TagType;
import org.nfctools.scio.TerminalStatus;
import org.nfctools.spi.acs.ApduTagReaderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parking.db.Entry.EntryStatus;
import parking.db.ParkingTerminal;
import parking.db.User;
import parking.db.UserEntry;
import parking.exception.UserEntryNotRegistered;
import parking.exception.UserIdNotFound;
import parking.ptm.nfc.AcsDirectChannelTag;
import parking.ptm.nfc.IsoDepTamaCommunicator;

public class ExitPTM extends ParkingTicketMachine {

	private Logger log = LoggerFactory.getLogger(getClass());
	private IsoDepTamaCommunicator tamaCommunicator;
	private List<IOnUserExit> onUserExitListeners = new ArrayList<IOnUserExit>();

	// private HostCardEmulationTagScanner tagScanner;

	public ExitPTM(CardTerminal cardTerminal) {
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
				ApduTagReaderWriter readerWriter = new ApduTagReaderWriter(
						new AcsDirectChannelTag(TagType.ISO_DEP, null, card));

				try {
					this.tamaCommunicator = new IsoDepTamaCommunicator(
							readerWriter, readerWriter);

					// Connect reader as initiator
					log.info("Connecting reader as initiator");
					this.tamaCommunicator.connectAsInitiator();

					// Authenticate User Id
					log.info("Authenticating User");
					UserEntry uEntry = this.authUserID(this.tamaCommunicator.getUserId());
					log.info("User authenticated");
					
					//RequestPayment
					//this.tamaCommunicator.checkPayment(uEntry.get);

					// Open Boom Gate
					this.boomGate.open();

					Thread.sleep(1500);

					// Close Boom Gate
					this.boomGate.close();

					// Register User
					this.registerUser(uEntry.getUser());

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

	private UserEntry authUserID(String uId) throws IOException {
		// Check if user exists
		User user = User.findUserbyUserName(uId);

		if (user == null) {
			throw new UserIdNotFound("User " + uId + " does not exist");
		}

		// Check if user is logged in parking lot
		UserEntry uEntry = UserEntry.findUserEntry(user, EntryStatus.ENTERED);

		if (uEntry == null) {
			throw new UserEntryNotRegistered("User " + uId
					+ " entry not registered");
		}

		return uEntry;
	}

	private void registerUser(User user) {
		List<IOnUserExit> callListeners = new ArrayList<IOnUserExit>(
				onUserExitListeners);

		for (Object listener : callListeners) {
			((IOnUserExit) listener).onUserExit(this, user);
		}
	}

	@Override
	public void stop() {
		this.interrupted = true;
		this.tamaCommunicator.stop();
	}

	// Events
	public synchronized void addEventListener(IOnUserExit listener) {
		onUserExitListeners.add(listener);
	}

	public synchronized void removeEventListener(IOnUserExit listener) {
		onUserExitListeners.remove(listener);
	}
	
}
