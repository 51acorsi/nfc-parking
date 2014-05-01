package parking.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.smartcardio.CardTerminal;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.UIManager;

import org.nfctools.examples.TerminalUtils;

import parking.db.HibernateSession;
import parking.db.ParkingTerminal;
import parking.db.User;
import parking.db.UserEntry;
import parking.ptm.EntryPTM;
import parking.ptm.ExitPTM;
import parking.ptm.ParkingTicketMachine;
import parking.ptm.ParkingTicketMachine.IEntryPTMEvents;
import parking.ptm.ParkingTicketMachine.IExitPTMEvents;
import parking.ptm.ParkingTicketMachine.PTMMode;
import parking.ptm.ParkingTicketMachine.PTMMsgType;

import javax.swing.JTextArea;

import java.awt.BorderLayout;

import javax.swing.border.LineBorder;
import java.awt.Font;

public class ParkingUI implements IEntryPTMEvents, IExitPTMEvents {

	private JFrame frame;
	private JLabel lblExitGate;
	private JLabel lblEntryGate;
	private JTextArea txtAreaInfo;
	private JLabel lblTerminalOutput;

	private List<ParkingTicketMachine> ticketMachineList = new ArrayList<ParkingTicketMachine>();

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
		frame.setBounds(100, 100, 941, 559);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 868 };
		gridBagLayout.rowHeights = new int[] { 476 };
		gridBagLayout.columnWeights = new double[] { Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JPanel pnlMain = new JPanel();
		GridBagConstraints gbc_pnlMain = new GridBagConstraints();
		gbc_pnlMain.fill = GridBagConstraints.BOTH;
		gbc_pnlMain.gridx = 0;
		gbc_pnlMain.gridy = 0;
		frame.getContentPane().add(pnlMain, gbc_pnlMain);
		GridBagLayout gbl_pnlMain = new GridBagLayout();
		gbl_pnlMain.columnWidths = new int[] { 0, 0, };
		gbl_pnlMain.rowHeights = new int[] { 296 };
		gbl_pnlMain.columnWeights = new double[] { 1, 0.0 };
		gbl_pnlMain.rowWeights = new double[] { Double.MIN_VALUE };
		pnlMain.setLayout(gbl_pnlMain);

		JPanel pnlLeft = new JPanel();
		GridBagConstraints gbc_pnlLeft = new GridBagConstraints();
		gbc_pnlLeft.insets = new Insets(10, 10, 10, 5);
		gbc_pnlLeft.fill = GridBagConstraints.BOTH;
		gbc_pnlLeft.anchor = GridBagConstraints.WEST;
		gbc_pnlLeft.gridx = 0;
		gbc_pnlLeft.gridy = 0;
		pnlMain.add(pnlLeft, gbc_pnlLeft);
		GridBagLayout gbl_pnlLeft = new GridBagLayout();
		gbl_pnlLeft.columnWidths = new int[] { 0 };
		gbl_pnlLeft.rowHeights = new int[] { 0 };
		gbl_pnlLeft.columnWeights = new double[] { 0.0 };
		gbl_pnlLeft.rowWeights = new double[] { 0.0 };
		pnlLeft.setLayout(gbl_pnlLeft);

		txtAreaInfo = new JTextArea();
		txtAreaInfo.setEditable(false);
		txtAreaInfo.setLineWrap(true);

		JScrollPane txtScrollPane = new JScrollPane(txtAreaInfo);
		txtScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		txtAreaInfo.setLineWrap(true);
		GridBagConstraints gbc_txtScrollPane = new GridBagConstraints();
		gbc_txtScrollPane.weighty = 1.0;
		gbc_txtScrollPane.weightx = 1.0;
		gbc_txtScrollPane.fill = GridBagConstraints.BOTH;
		gbc_txtScrollPane.gridx = 0;
		gbc_txtScrollPane.gridy = 0;
		pnlLeft.add(txtScrollPane, gbc_txtScrollPane);

		// pnlLeft.add(txtAreaInfo, gbc_txtAreaInfo);

		JPanel pnlGate = new JPanel();
		GridBagConstraints gbc_pnlGate = new GridBagConstraints();
		gbc_pnlGate.insets = new Insets(10, 5, 10, 10);
		gbc_pnlGate.fill = GridBagConstraints.VERTICAL;
		gbc_pnlGate.gridx = 1;
		gbc_pnlGate.gridy = 0;
		pnlMain.add(pnlGate, gbc_pnlGate);
		GridBagLayout gbl_pnlGate = new GridBagLayout();
		gbl_pnlGate.columnWidths = new int[] { 0, 0 };
		gbl_pnlGate.rowHeights = new int[] { 100, 0, 0 };
		gbl_pnlGate.columnWeights = new double[] { 1.0, 0 };
		gbl_pnlGate.rowWeights = new double[] { 0, 0.5, Double.MIN_VALUE };
		pnlGate.setLayout(gbl_pnlGate);

		JPanel pnlTerminal = new JPanel();
		pnlTerminal.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagConstraints gbc_pnlTerminal = new GridBagConstraints();
		gbc_pnlTerminal.fill = GridBagConstraints.VERTICAL;
		gbc_pnlTerminal.gridwidth = 2;
		gbc_pnlTerminal.gridx = 0;
		gbc_pnlTerminal.gridy = 0;
		gbc_pnlTerminal.ipadx = 150;
		gbc_pnlTerminal.ipady = 100;
		pnlGate.add(pnlTerminal, gbc_pnlTerminal);
		pnlTerminal.setLayout(new BorderLayout(0, 0));

		lblTerminalOutput = new JLabel("<html>Hello World!<br>blahblahblah</html>");
		lblTerminalOutput.setOpaque(true);
		lblTerminalOutput.setBackground(Color.WHITE);
		lblTerminalOutput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		pnlTerminal.add(lblTerminalOutput, BorderLayout.CENTER);

		JButton btnTooglePTM = new JButton("");
		btnTooglePTM.setIcon(new ImageIcon(ParkingUI.class.getResource("/icon/switch.png")));
		GridBagConstraints gbc_btnTooglePTM = new GridBagConstraints();
		gbc_btnTooglePTM.anchor = GridBagConstraints.SOUTH;
		gbc_btnTooglePTM.insets = new Insets(0, 0, 5, 0);
		gbc_btnTooglePTM.gridwidth = 2;
		gbc_btnTooglePTM.gridx = 0;
		gbc_btnTooglePTM.gridy = 1;
		pnlGate.add(btnTooglePTM, gbc_btnTooglePTM);
		btnTooglePTM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tooglePTM();
			}
		});

		lblExitGate = new JLabel();
		lblExitGate.setIcon(new ImageIcon(ParkingUI.class.getResource("/gate/ExitGateClosed.png")));
		lblExitGate.setDisabledIcon(new ImageIcon(ParkingUI.class.getResource("/gate/ExitGateDisabled.png")));
		GridBagConstraints gbc_lblExitGate = new GridBagConstraints();
		gbc_lblExitGate.insets = new Insets(0, 0, 5, 5);
		gbc_lblExitGate.gridx = 0;
		gbc_lblExitGate.gridy = 2;
		gbc_lblExitGate.anchor = GridBagConstraints.SOUTHEAST;
		pnlGate.add(lblExitGate, gbc_lblExitGate);

		lblEntryGate = new JLabel();
		lblEntryGate.setIcon(new ImageIcon(ParkingUI.class.getResource("/gate/EntryGateClosed.png")));
		lblEntryGate.setDisabledIcon(new ImageIcon(ParkingUI.class.getResource("/gate/EntryGateDisabled.png")));
		GridBagConstraints gbc_lblEntryGate = new GridBagConstraints();
		gbc_lblEntryGate.insets = new Insets(0, 0, 5, 0);
		gbc_lblEntryGate.gridx = 1;
		gbc_lblEntryGate.gridy = 2;
		gbc_lblEntryGate.anchor = GridBagConstraints.SOUTHWEST;
		pnlGate.add(lblEntryGate, gbc_lblEntryGate);

		// Set Borders for testing
		// this.setBorders(frame.getContentPane().getComponents());

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
		this.resetTerminal();
		lblEntryGate.setEnabled(false);
		lblExitGate.setEnabled(false);
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
				// ((EntryPTM) ptm).addEventListener(this);
				this.lblEntryGate.setEnabled(true);
			}

			if (ptm instanceof ExitPTM) {
				// ((ExitPTM) ptm).addEventListener(this);
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

	private void addInformation(String info) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
		this.txtAreaInfo.append(sdf.format(new Date()) + ": " + info + ";\n");
	}

	private void showInTerminal(String msg, PTMMsgType msgType) {
		String xmlStart = "<html>";
		String xmlEnd = "</html>";
		String newLine = "<br>";

		msg = msg.replace("\n", newLine);
		lblTerminalOutput.setText(xmlStart + msg + xmlEnd);

		if (msgType == PTMMsgType.ERROR)
		{
			lblTerminalOutput.setForeground(Color.RED);
		}
		
		if (msgType == PTMMsgType.INFO)
		{
			lblTerminalOutput.setForeground(Color.BLUE);
		}
		
		// Restart Terminal Afterwards
		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				resetTerminal();
			}
		}, 2500);
	}

	private void resetTerminal() {
		lblTerminalOutput.setText("Parking: " + ParkingTerminal.getParkingName());
		lblTerminalOutput.setForeground(Color.BLACK);
	}

	@Override
	public void onUserEntry(ParkingTicketMachine ptm, UserEntry uEntry) {
		this.addInformation("User " + uEntry.getUser().getUserName() + " logged");
	}

	@Override
	public void onUserExit(ParkingTicketMachine ptm, UserEntry uEntry) {
		this.addInformation("User " + uEntry.getUser().getUserName() + " paid");
	}

	@Override
	public void onBoomGateOpened(ParkingTicketMachine ptm) {
		if (ptm instanceof EntryPTM) {
			lblEntryGate.setIcon(new ImageIcon(ParkingUI.class.getResource("/gate/EntryGateOpened.png")));
		} else if (ptm instanceof ExitPTM) {
			lblExitGate.setIcon(new ImageIcon(ParkingUI.class.getResource("/gate/ExitGateOpened.png")));
		}
	}

	@Override
	public void onBoomGateClosed(ParkingTicketMachine ptm) {
		if (ptm instanceof EntryPTM) {
			lblEntryGate.setIcon(new ImageIcon(ParkingUI.class.getResource("/gate/EntryGateClosed.png")));
		} else if (ptm instanceof ExitPTM) {
			lblExitGate.setIcon(new ImageIcon(ParkingUI.class.getResource("/gate/ExitGateClosed.png")));
		}
	}

	@Override
	public void onTerminalMessage(ParkingTicketMachine PTM, String msg, PTMMsgType msgType) {
		this.showInTerminal(msg, msgType);
	}

	// Form Event Actions
	private void tooglePTM() {

		if (lblEntryGate.isEnabled()) {
			// Stop Entry PTMs
			this.stopTicketMachines();
			this.removePTMS();

			// Create new Exit PTM
			createExitPTM();
			this.initializeTicketMachines();
		} else if (lblExitGate.isEnabled()) {
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

	// Test Methods
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
