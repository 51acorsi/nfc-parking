package parking.db;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import parking.db.Entry.EntryStatus;
import parking.protocol.Protocol.PaymentMethod;

public class ParkingTerminal {
	
	private static int parkingId = 1;
	private static String parkingName = "Test-a-Parking";
	private static PaymentMethod paymentMethod = PaymentMethod.BY_ENTRY;
	private static float parkingFee = 5;

	public static int getParkingId()
	{
		return ParkingTerminal.parkingId;
	}	
	
	public static String getParkingName()
	{
		return ParkingTerminal.parkingName;
	}

	public static PaymentMethod getPaymentMethod() {
		return ParkingTerminal.paymentMethod;
	}
	
	public static float getParkingFee()
	{
		return ParkingTerminal.parkingFee;
	}

}
