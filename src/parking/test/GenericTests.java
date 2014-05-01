package parking.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import parking.protocol.Protocol.PaymentMethod;

public class GenericTests {
	
	public static void main(String[] args) {
		
			@SuppressWarnings("unused")
			ImageIcon icon  = new ImageIcon("src/images/EntryGateClosed.png");
			
			File f = new File("src/images/gate/EntryGateClosed.png");
			System.out.println(f.exists());

		
		//int a = PaymentMethod.BY_ENTRY.ordinal();
		//PaymentMethod p = PaymentMethod.fromOrdinal(a);
	}
	
}
