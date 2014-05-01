package parking.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.smartcardio.CardTerminal;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import com.sun.xml.internal.ws.api.server.Container;

import javax.swing.JTextField;
import javax.swing.JSplitPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.UIManager;

import org.nfctools.examples.TerminalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parking.db.HibernateSession;
import parking.db.User;
import parking.db.UserEntry;
import parking.ptm.EntryPTM;
import parking.ptm.ExitPTM;
import parking.ptm.ParkingTicketMachine;
import parking.ptm.ParkingTicketMachine.IEntryPTMEvents;
import parking.ptm.ParkingTicketMachine.IExitPTMEvents;
import parking.ptm.ParkingTicketMachine.PTMMode;

public class ParkingUI implements IEntryPTMEvents, IExitPTMEvents {

	private JFrame frame;
	private JLabel lblExitGate;
	private JLabel lblEntryGate;

	private List<ParkingTicketMachine> ticketMachineList = new ArrayList<ParkingTicketMachine>();
	private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ParkingUI window = new ParkingUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ParkingUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 841, 529);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

		JPanel panel1 = new JPanel();
		frame.getContentPane().add(panel1);
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

		JPanel panel2 = new JPanel();
		frame.getContentPane().add(panel2, BorderLayout.CENTER);
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		panel2.add(panel);

		JButton btnNewButton = new JButton("New button");
		panel.add(btnNewButton);

		JButton btnTooglePTM = new JButton("<-\r\n->");
		btnTooglePTM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tooglePTM();
			}
		});
		panel.add(btnTooglePTM);

		JPanel pnlGate = new JPanel();
		panel2.add(pnlGate);
		GridBagLayout gbl_pnlGate = new GridBagLayout();
		gbl_pnlGate.columnWidths = new int[] { 0, 0 };
		gbl_pnlGate.rowHeights = new int[] { 0, 0, 0 };
		gbl_pnlGate.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlGate.rowWeights = new double[] { 1.0, Double.MIN_VALUE, Double.MIN_VALUE };
		pnlGate.setLayout(gbl_pnlGate);

		lblExitGate = new JLabel();
		GridBagConstraints gbc_lblExitGate = new GridBagConstraints();
		gbc_lblExitGate.anchor = GridBagConstraints.SOUTHEAST;
		pnlGate.add(lblExitGate, gbc_lblExitGate);

		lblEntryGate = new JLabel();
		GridBagConstraints gbc_lblEntryGate = new GridBagConstraints();
		gbc_lblEntryGate.anchor = GridBagConstraints.SOUTHWEST;
		pnlGate.add(lblEntryGate, gbc_lblEntryGate);

		// Set Borders for testing
		this.setBorders(frame.getContentPane().getComponents());
		
		this.addApplicationEvents();
		this.prepareUI();
		this.prepareDB();
		this.createParkingLot();
	}

	private void addApplicationEvents() {
		 this.frame.addWindowListener(new java.awt.event.WindowAdapter() {
		        public void windowClosing(WindowEvent winEvt) {
		            onExit(winEvt);
		        }
		    });		
	}

	private void prepareUI() {
		this.loadInitialImages();
		
		lblEntryGate.setEnabled(false);
		lblExitGate.setEnabled(false);		
	}
	
	private void loadInitialImages() {
		//Entry Gate
		this.lblEntryGate.setIcon(new ImageIcon("C:\\Users\\i827040\\Desktop\\TCC\\TC2\\catracas\\EntryGateClosed.png"));
		this.lblEntryGate.setDisabledIcon(new ImageIcon("C:\\Users\\i827040\\Desktop\\TCC\\TC2\\catracas\\EntryGateDisabled.png"));
		
		//Exit Gate
		this.lblExitGate.setIcon(new ImageIcon("C:\\Users\\i827040\\Desktop\\TCC\\TC2\\catracas\\ExitGateClosed.png"));
		this.lblExitGate.setDisabledIcon(new ImageIcon("C:\\Users\\i827040\\Desktop\\TCC\\TC2\\catracas\\ExitGateDisabled.png"));
	}

	private void prepareDB() {

		// Create Users
		User user1 = new User("felipe.01", "Felipe");
		User user2 = new User("felipe.02", "Felipe 2");

		HibernateSession.getSession().beginTransaction();

		HibernateSession.getSession().save(user1);
		HibernateSession.getSession().save(user2);

		HibernateSession.getSession().getTransaction().commit();
		HibernateSession.getSession().close();
	}

	private void createParkingLot() {		
		// Create Entry PTM
		this.createEntryPTM();

		initializeTicketMachines();
	}

	private void createEntryPTM() {
		ParkingTicketMachine ptm = createPTM(PTMMode.ENTRY);
		this.ticketMachineList.add(ptm);
	}

	private void createExitPTM() {
		ParkingTicketMachine ptm = createPTM(PTMMode.EXIT);
		this.ticketMachineList.add(ptm);
	}

	private void initializeTicketMachines() {
		for (ParkingTicketMachine ptm : this.ticketMachineList) {

			if (ptm instanceof EntryPTM) {
				//((EntryPTM) ptm).addEventListener(this);
				this.lblEntryGate.setEnabled(true);
			}

			if (ptm instanceof ExitPTM) {
				//((ExitPTM) ptm).addEventListener(this);
				this.lblExitGate.setEnabled(true);
			}

			ptm.addEventListener(this);
			
			Thread t = new Thread(ptm);
			t.start();
		}
	}

	private void stopTicketMachines() {
		for (ParkingTicketMachine ptm : this.ticketMachineList) {
			ptm.stop();
		}
		
		this.lblEntryGate.setEnabled(false);
		this.lblExitGate.setEnabled(false);
	}

	private void removePTMS() {
		for (ParkingTicketMachine ptm : this.ticketMachineList) {
				ptm.removeEventListener(this);
		}

		this.ticketMachineList.clear();
	}

	private ParkingTicketMachine createPTM(PTMMode mode) {

		CardTerminal cardTerminal = TerminalUtils.getAvailableTerminal().getCardTerminal();

		switch (mode) {
		case ENTRY:

			return new EntryPTM(cardTerminal);

		case EXIT:

			return new ExitPTM(cardTerminal);

		default:
			return null;
		}
	}

	@Override
	public void onUserEntry(ParkingTicketMachine ptm, UserEntry uEntry) {
		log.info("User " + uEntry.getUser().getUserName() + " logged");
	}

	@Override
	public void onUserExit(ParkingTicketMachine ptm, UserEntry uEntry) {
		log.info("User " + uEntry.getUser().getUserName() + " paid");
	}
	
	@Override
	public void onBoomGateOpened(ParkingTicketMachine ptm) {
		if (ptm instanceof EntryPTM)
		{
			lblEntryGate.setIcon(new ImageIcon("C:\\Users\\i827040\\Desktop\\TCC\\TC2\\catracas\\EntryGateOpened.png"));
		}
		else if (ptm instanceof ExitPTM)
		{
			lblExitGate.setIcon(new ImageIcon("C:\\Users\\i827040\\Desktop\\TCC\\TC2\\catracas\\ExitGateOpened.png"));
		}		
	}

	@Override
	public void onBoomGateClosed(ParkingTicketMachine ptm) {
		if (ptm instanceof EntryPTM)
		{
			lblEntryGate.setIcon(new ImageIcon("C:\\Users\\i827040\\Desktop\\TCC\\TC2\\catracas\\EntryGateClosed.png"));
		}
		else if (ptm instanceof ExitPTM)
		{
			lblExitGate.setIcon(new ImageIcon("C:\\Users\\i827040\\Desktop\\TCC\\TC2\\catracas\\ExitGateClosed.png"));
		}		
	}

	//Form Event Actions
	private void tooglePTM() {

		if (lblEntryGate.isEnabled())
		{
			// Stop Entry PTMs
			this.stopTicketMachines();
			this.removePTMS();

			// Create new Exit PTM
			createExitPTM();
			this.initializeTicketMachines();
		}		
		else if (lblExitGate.isEnabled())
		{
			// Stop Exit PTMs
			this.stopTicketMachines();
			this.removePTMS();

			// Create new Entry PTM
			createEntryPTM();
			this.initializeTicketMachines();
		}		
	}
	
	private void onExit(WindowEvent winEvt) {
        HibernateSession.close();
        System.exit(0);					
	}

	//Test Methods
	private void setBorders(Component[] comps) {
		for (Component component : comps) {
			if (component instanceof JComponent) {
				JComponent jcomp = (JComponent) component;
				jcomp.setBorder(BorderFactory.createLineBorder(Color.black));
				setBorders(jcomp.getComponents());
			}
		}
	}
}
