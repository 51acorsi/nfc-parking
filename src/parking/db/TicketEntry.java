package parking.db;

import java.awt.image.BufferedImage;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;

@Entity(name = "TicketEntry")
@DiscriminatorValue("TicketEntry")
public class TicketEntry extends Entry {

	@Column(name = "BarCode")
	private String barCode;

	public TicketEntry() {

	}

	public TicketEntry(Date enteredOn, String barCode) {
		super(enteredOn);
		this.barCode = barCode;
	}

	public BufferedImage getBarCodeImage() throws OutputException,
			BarcodeException {
		Barcode barcode = BarcodeFactory.createCode128(this.barCode);
		return BarcodeImageHandler.getImage(barcode);
	}
}
