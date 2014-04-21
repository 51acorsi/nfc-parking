package parking.test;

import java.awt.image.BufferedImage;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import parking.db.TicketEntry;
import java.util.Date;

public class TicketPrintingDemo {

	public static void main(String[] args) throws OutputException, BarcodeException {
		
		TicketEntry tk = new TicketEntry(new Date(), "123456789");
		BufferedImage bi = tk.getBarCodeImage();		
	}
	
}
