package parking.test;

import java.awt.image.BufferedImage;

import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import parking.db.Ticket;

public class TicketPrintingDemo {

	public static void main(String[] args) throws OutputException, BarcodeException {
		
		Ticket tk = new Ticket("123456789");
		BufferedImage bi = tk.getBarCodeImage();		
	}
	
}
