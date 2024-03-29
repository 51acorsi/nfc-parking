package parking.ptm.nfc;

import java.io.IOException;
import java.io.SyncFailedException;
import java.nio.ByteBuffer;

import org.nfctools.io.ByteArrayReader;
import org.nfctools.io.ByteArrayWriter;
import org.nfctools.spi.tama.AbstractTamaCommunicator;
import org.nfctools.spi.tama.request.DataExchangeReq;
import org.nfctools.spi.tama.request.InListPassiveTargetReq;
import org.nfctools.spi.tama.response.DataExchangeResp;
import org.nfctools.spi.tama.response.InListPassiveTargetResp;
import org.nfctools.utils.NfcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parking.db.ParkingTerminal;
import parking.db.UserEntry;
import parking.protocol.Protocol;

public class IsoDepTamaCommunicator extends AbstractTamaCommunicator {

	private Logger log = LoggerFactory.getLogger(getClass());
	private int messageCounter = 0;
	private static final byte[] CLA_INS_P1_P2 = { 0x00, (byte) 0xA4, 0x04, 0x00 };
	private static final byte[] AID_ANDROID = { (byte) 0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x07 };
	private InListPassiveTargetResp inListPassiveTargetResp;

	private boolean interrupted = false;

	public IsoDepTamaCommunicator(ByteArrayReader reader, ByteArrayWriter writer) {
		super(reader, writer);
	}

	private byte[] createSelectAidApdu(byte[] aid) {
		byte[] result = new byte[6 + aid.length];
		System.arraycopy(CLA_INS_P1_P2, 0, result, 0, CLA_INS_P1_P2.length);
		result[4] = (byte) aid.length;
		System.arraycopy(aid, 0, result, 5, aid.length);
		result[result.length - 1] = 0;
		return result;
	}

	public void connectAsInitiator() throws IOException {
		while (!this.interrupted) {
			inListPassiveTargetResp = sendMessage(new InListPassiveTargetReq((byte) 1, (byte) 0, new byte[0]));
			if (inListPassiveTargetResp.getNumberOfTargets() > 0) {
				log.info("TargetData: " + NfcUtils.convertBinToASCII(inListPassiveTargetResp.getTargetData()));
				if (inListPassiveTargetResp.isIsoDepSupported()) {
					log.info("IsoDep Supported");

					log.info("Authenticating App ID");
					authApplicationID(inListPassiveTargetResp);
					log.info("App ID authenticated");

					return;
				} else {
					log.info("IsoDep NOT Supported");
				}
				break;
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void stop() {
		this.interrupted = true;
	}

	private void authApplicationID(InListPassiveTargetResp inListPassiveTargetResp) throws IOException {
		byte[] selectAidApdu = createSelectAidApdu(AID_ANDROID);
		DataExchangeResp resp = sendMessage(new DataExchangeReq(inListPassiveTargetResp.getTargetId(), false,
				selectAidApdu, 0, selectAidApdu.length));
		String dataIn = new String(resp.getDataOut());

		if (!dataIn.equals("OK")) {
			throw new SyncFailedException("Authentication not received");
		}
	}

	public String getUserId() throws IOException {
		DataExchangeResp resp;

		byte[] dataOut = Protocol.getUserIDCommand();
		resp = sendMessage(new DataExchangeReq(inListPassiveTargetResp.getTargetId(), false, dataOut, 0, dataOut.length));

		return new String(resp.getDataOut());
	}

	public void addEntryRegister(UserEntry ue) throws IOException {

		DataExchangeResp resp;

		byte[] dataOut = Protocol.getSetNewRegistryCommand(ParkingTerminal.getParkingId(),
				ParkingTerminal.getParkingName(), ue.getEntryId(), ue.getEntryTime(),
				ParkingTerminal.getPaymentMethod(), ParkingTerminal.getParkingFee());
		
		resp = sendMessage(new DataExchangeReq(inListPassiveTargetResp.getTargetId(), false, dataOut, 0, dataOut.length));

		// Check response
		if (Protocol.checkConfirmMessage(resp.getDataOut()) == false)
		{
			// TODO Error Handling
		}
	}
	
	public void requestPayment(UserEntry ue) throws IOException {

		DataExchangeResp resp;

//		byte[] dataOut = Protocol.getSetNewRegistryCommand(ParkingTerminal.getParkingId(),
//				ParkingTerminal.getParkingName(), ue.getEntryId(), ue.getEntryTime(),
//				ParkingTerminal.getPaymentMethod(), ParkingTerminal.getParkingFee());
		
		byte[] dataOut = Protocol.getReqPaymentCommand(ParkingTerminal.getParkingId(), ue.getEntryId());
		resp = sendMessage(new DataExchangeReq(inListPassiveTargetResp.getTargetId(), false, dataOut, 0, dataOut.length));

		// Check response
		if (Protocol.checkConfirmMessage(resp.getDataOut()) == false)
		{
			// TODO Error Handling
		}
	}

	private void exchangeData(InListPassiveTargetResp inListPassiveTargetResp) throws IOException {
		DataExchangeResp resp;
		String dataIn;
		while (true) {
			byte[] dataOut = ("Message from desktop: " + messageCounter++).getBytes();
			resp = sendMessage(new DataExchangeReq(inListPassiveTargetResp.getTargetId(), false, dataOut, 0,
					dataOut.length));
			dataIn = new String(resp.getDataOut());
			log.info("Received: " + dataIn);
		}
	}

	public void testMessage(int a) throws IOException {
		DataExchangeResp resp;
		
		byte[] dataOut = ByteBuffer.allocate(a).array();
		resp = sendMessage(new DataExchangeReq(inListPassiveTargetResp.getTargetId(), false, dataOut, 0, dataOut.length));		
	}
}
