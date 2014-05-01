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
import parking.db.User;
import parking.db.UserEntry;
import parking.exception.UserDoubleEntry;
import parking.exception.UserIdNotFound;
import parking.ptm.nfc.AcsDirectChannelTag;
import parking.ptm.nfc.IsoDepTamaCommunicator;

public class EntryPTM extends ParkingTicketMachine {

	private Logger log = LoggerFactory.getLogger(getClass());
	private IsoDepTamaCommunicator tamaCommunicator;
//	private List<IEntryPTMEvents> onUserEntryListeners = new ArrayList<IEntryPTMEvents>();

	// private HostCardEmulationTagScanner tagScanner;

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
				ApduTagReaderWriter readerWriter = new ApduTagReaderWriter(new AcsDirectChannelTag(TagType.ISO_DEP,
						null, card));

				try {
					this.tamaCommunicator = new IsoDepTamaCommunicator(readerWriter, readerWriter);

					// Connect reader as initiator
					log.info("Connecting reader as initiator");
					this.tamaCommunicator.connectAsInitiator();
					
					// Authenticate User Id
					log.info("Authenticating User");
					User user = this.authUserID(this.tamaCommunicator.getUserId());
					log.info("User authenticated");

					// Register User Entry in SW
					log.info("Registering UserEntry in SW");
					UserEntry ue = this.registerUserEntry(user);
					log.info("User Entry Registered");

					// Register User Entry in APP
					log.info("Registering UserEntry in APP");
					this.tamaCommunicator.addEntryRegister(ue);
					log.info("UserEntry registered in APP");

					// Open Boom Gate
					this.openGate();

					Thread.sleep(1500);

					// Close Boom Gate
					this.closeGate();

					// Notify User Entry
					this.notifyUserEntry(ue);

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


//	// Events
//	public synchronized void addEventListener(IEntryPTMEvents listener) {
//		onUserEntryListeners.add(listener);
//	}
//
//	public synchronized void removeEventListener(IEntryPTMEvents listener) {
//		onUserEntryListeners.remove(listener);
//	}

	private User authUserID(String uId) throws IOException {
		// Check if user exists in DB
		User user = User.findUserbyUserName(uId);

		if (user == null) {
			throw new UserIdNotFound("Authentication not received");
		}

		// Check double entry
		UserEntry ue = UserEntry.findUserEntry(user, EntryStatus.ENTERED);

		if (ue != null) {
			throw new UserDoubleEntry("Double entry not allowed");
		}

		return user;
	}

	private UserEntry registerUserEntry(User user) {
		// Register Parking Entry in SW
		return UserEntry.registerUserEntry(user);
	}

	private void notifyUserEntry(UserEntry ue) {
		List<IPTMEvents> callListeners = new ArrayList<IPTMEvents>(this.onPTMListeners);

		for (Object listener : callListeners) {
			((IEntryPTMEvents) listener).onUserEntry(this, ue);
		}
	}

	@Override
	public void stop() {
		this.interrupted = true;
		this.tamaCommunicator.stop();
	}
}
