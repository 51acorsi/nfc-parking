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
import parking.exception.UserEntryNotRegistered;
import parking.exception.UserIdNotFound;
import parking.ptm.nfc.AcsDirectChannelTag;
import parking.ptm.nfc.IsoDepTamaCommunicator;

public class ExitPTM extends ParkingTicketMachine {

	private Logger log = LoggerFactory.getLogger(getClass());
	private IsoDepTamaCommunicator tamaCommunicator;

	// private List<IExitPTMEvents> onUserExitListeners = new
	// ArrayList<IExitPTMEvents>();

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
				ApduTagReaderWriter readerWriter = new ApduTagReaderWriter(new AcsDirectChannelTag(TagType.ISO_DEP, null, card));

				try {
					this.tamaCommunicator = new IsoDepTamaCommunicator(readerWriter, readerWriter);

					// Connect reader as initiator
					log.info("Connecting reader as initiator");
					this.tamaCommunicator.connectAsInitiator();

					// Authenticate User Id
					log.info("Authenticating User");
					UserEntry uEntry = this.authUserID(this.tamaCommunicator.getUserId());
					log.info("User authenticated");

					// RequestPayment
					log.info("Requenting Payment");
					this.tamaCommunicator.requestPayment(uEntry);
					log.info("Entry Paid");

					// Registered Paid Entry
					log.info("Updating Paid Entry");
					this.updatePaidEntry(uEntry);
					log.info("Entry Updated");

					// Open Boom Gate
					this.openGate();

					Thread.sleep(1500);

					// Close Boom Gate
					this.closeGate();

					// Notify User Entry Exit
					this.notifyUserExit(uEntry);

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
			throw new UserEntryNotRegistered("User " + uId + " entry not registered");
		}

		return uEntry;
	}

	private void updatePaidEntry(UserEntry uEntry) {
		UserEntry.updatePaidUserEntry(uEntry);
	}

	private void notifyUserExit(UserEntry uEntry) {
		List<IPTMEvents> callListeners = new ArrayList<IPTMEvents>(this.onPTMListeners);

		for (Object listener : callListeners) {
			((IExitPTMEvents) listener).onUserExit(this, uEntry);
		}
	}

	@Override
	public void stop() {
		this.interrupted = true;
		this.tamaCommunicator.stop();
	}
}
