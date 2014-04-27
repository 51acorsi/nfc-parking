package parking.test;

import parking.protocol.Protocol.PaymentMethod;

public class GenericTests {
	
	public static void main(String[] args) {
		int a = PaymentMethod.BY_ENTRY.ordinal();
		PaymentMethod p = PaymentMethod.fromOrdinal(a);
	}
	
}
