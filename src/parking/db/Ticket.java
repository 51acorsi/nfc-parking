package parking.db;

import static javax.persistence.GenerationType.SEQUENCE;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.persistence.Column;  
import javax.persistence.Entity;  
import javax.persistence.GeneratedValue;  
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;

@Entity(name="Ticket")
public class Ticket {
	
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "seq_ticket")
	@SequenceGenerator(name="seq_ticket", sequenceName="seq_ticket")
	private Long id;
	
	@Column(name="BarCode")  
	private String barCode; 	
	
	public Ticket (String barCode)
	{
		this.barCode = barCode;
	}

	public BufferedImage getBarCodeImage() throws OutputException, BarcodeException
	{
		Barcode barcode = BarcodeFactory.createCode128(this.barCode);
        return BarcodeImageHandler.getImage(barcode);
	}	
}
