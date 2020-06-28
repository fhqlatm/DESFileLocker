package DESFileLocker;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import com.thehowtotutorial.splashscreen.JSplash;
import java.net.URL;

import java.io.*;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class FileLocker extends JFrame {

	static String keyValue = "1111111111222222222233333333333"; // ��ȣ�� ���õ� 24byte Ű��

	JFrame frame; // ������ ����
	JTextArea textArea; // �ؽ�Ʈ ����
	String fileName; // ���� �̸�

	FileReader fr; // ���� �б�
	BufferedReader br; // ���� �д� ���� ȿ�������� ���̱� ����
	StringWriter sw; // ���� ����
	FileWriter fw; // ���� ����
	BufferedWriter bw; // ���� ���� ���� ȿ�������� ���̱� ����

	// FileLcoker ������
	FileLocker() throws InterruptedException {

		loadMainImage();
		createMenu();
		createMainWindow();

	} // FileLocker	

	public void createMainWindow() {
		
		textArea = new JTextArea();
		
		JPanel panel = new JPanel();
		JLabel titleLabel = new JLabel();
		EtchedBorder eBorder = new EtchedBorder(EtchedBorder.RAISED);
		
		titleLabel.setBorder(eBorder);
		titleLabel.setText("TEXT Field");
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setFont(new Font("SansSerif",Font.BOLD, 17));
		
		textArea.setBorder(eBorder);
		textArea.setFont(new Font("",Font.BOLD, 17));
		
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 10 , 10));
		panel.setBackground(Color.lightGray);
		
		panel.add(titleLabel, BorderLayout.NORTH);
		panel.add(textArea, BorderLayout.CENTER);
		
		getContentPane().add(panel); // �ؽ�Ʈ ������ �����ӿ� �߰�

		setTitle("FILE LOCKER");
		setSize(650, 450);
		setLocationRelativeTo(null); // �������� ����� ����� ����
		setDefaultCloseOperation(EXIT_ON_CLOSE); // �������� ������ ���� ó��
		setVisible(true);
	}

	// ���α׷� ���� ������ JSplash.jar �̿�
	// https://m.blog.naver.com/ndb796/220621281539
	public void loadMainImage() throws InterruptedException {

		URL mainImageURL = FileLocker.class.getClassLoader().getResource("main.jpg");
		
		// main.jpg �� ������ JSplash ��ü ����
		JSplash splash = new JSplash(mainImageURL, true, true, false, "V1", null, Color.RED, Color.BLACK);

		int sleepDelay = 500;

		splash.splashOn();

		splash.setProgress(20, "Initializing");
		Thread.sleep(sleepDelay);
		splash.setProgress(40, "Loading");
		Thread.sleep(sleepDelay);
		splash.setProgress(60, "Applying Configs");
		Thread.sleep(sleepDelay);
		splash.setProgress(80, "Starting Program");
		Thread.sleep(sleepDelay);

		splash.splashOff();
	} //loadMainImage

	public void createMenu() {

		// �޴��� ��ü ����
		JMenuBar menuBar = new JMenuBar();

		// �޴� ��ü ����
		JMenu menuFile = new JMenu("FILE");
		JMenu menuLock = new JMenu("LOCK");
		JMenu menuUnLock = new JMenu("UNLOCK");
		JMenu menuExit = new JMenu("EXIT");

		// �� �޴��� ������ ��ü ����
		MenuActionListener listener = new MenuActionListener();

		// �� �޴����� �޴��ٿ� �߰�
		menuBar.add(menuFile);
		menuBar.add(menuLock);
		menuBar.add(menuUnLock);
		menuBar.add(menuExit);

		// �޴��� �߰�
		setJMenuBar(menuBar);

		// FILE�޴��� �޴������� ��ü ������, �޴��������� �޴��� �߰�
		JMenuItem[] menuFileItem = new JMenuItem[3];
		String[] menuFileItemTitles = { "New", "Open", "Save" };

		for (int i = 0; i < menuFileItem.length; i++) {
			menuFileItem[i] = new JMenuItem(menuFileItemTitles[i]);
			menuFileItem[i].addActionListener(listener);
			menuFile.add(menuFileItem[i]);
		} // for

		// LOCK�޴��� �޴������� ��ü ����, �޴��������� �޴��� �߰�
		JMenuItem menuLockItem = new JMenuItem("Lock_DES");
		menuLockItem.addActionListener(listener);
		menuLock.add(menuLockItem);

		// UNLOCK�޴��� �޴������� ��ü ����, �޴��������� �޴��� �߰�
		JMenuItem menuUnLockItem = new JMenuItem("UnLock_DES");
		menuUnLockItem.addActionListener(listener);
		menuUnLock.add(menuUnLockItem);
		

		// EXIT�޴��� �޴������� ��ü ����, �޴��������� �޴��� �߰�
		JMenuItem menuExitItem = new JMenuItem("Exit");
		menuExitItem.addActionListener(listener);
		menuExit.add(menuExitItem);
	} // createMenu

	// �޴� �������� ������ Ŭ����
	class MenuActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			switch (e.getActionCommand()) {

			case "New":
				newFile();
				break;

			case "Open":
				openFile();
				break;

			case "Save":
				saveFile();
				break;

			case "Lock_DES":
				lockFile();
				break;

			case "UnLock_DES":
				unlockFile();
				break;

			case "Exit":
				exit();
				break;
			} // switch
		} // actionPerformed
	} // MenuActionListener

	// new �޴������� �̺�Ʈ
	public void newFile() {

		textArea.setText(""); // �ؽ�Ʈ ������ ����ȭ
	} // newFile

	// open �޴������� �̺�Ʈ
	public void openFile() {

		// ������ �ҷ� ���̴� ��ü ����
		FileDialog fileOpen = new FileDialog(frame, "���Ͽ���", FileDialog.LOAD);
		fileOpen.setLocationRelativeTo(null);
		fileOpen.setVisible(true); // ��������� ���̾�α� ����
		fileName = fileOpen.getDirectory() + fileOpen.getFile(); // ���� ��� �� ����
																	// �̸� �� Ȯ����
																	// ����
		System.out.println(fileName);

		// ����������� ���� ������ ���� ó��
		try {

			int ch = 0;

			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			sw = new StringWriter();

			while ((ch = br.read()) != -1) {

				sw.write(ch);
			} // while

			br.close(); // ���� �ݱ�
			textArea.setText(sw.toString()); // �ؽ�Ʈ ������ ���� ���� ����

		} catch (IOException e) { // ����� ���� ó��

			e.printStackTrace(); // ���ϰ� X, ���� �ڼ��� ���ܰ�� ����
		} // try
	} // openFile

	// save �޴������� �̺�Ʈ
	public void saveFile() {

		// ������ �����ϴ� ��ü ����
		FileDialog fileSave = new FileDialog(frame, "��������", FileDialog.SAVE); // ���������
																				// ���̾�α�
																				// ����
		fileSave.setVisible(true);
		fileName = fileSave.getDirectory() + fileSave.getFile(); // ���� ��� �� ����
																	// �̸� �� Ȯ����
																	// ����
		System.out.println(fileName);

		// ����������� ���� ������ ���� ó��
		try {

			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			bw.write(textArea.getText()); // ���� �����ϱ�
			bw.close(); // ���� �ݱ�

		} catch (IOException ie) { // ����� ���� ó��

			ie.printStackTrace(); // ���ϰ� X, ���� �ڼ��� ���ܰ�� ����
		} // try
	} // saveFile

	public static Key getKey() throws Exception { // DES Ű�� ȹ��

		System.out.println("DES KEY ACCESS");
		DESKeySpec desKeySpec = new DESKeySpec(keyValue.getBytes()); // keyValue ����Ʈȭ�Ͽ� DESKeySpec�� ����
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); // DESKeySpec�� DES ������ Ű������ ����

		return keyFactory.generateSecret(desKeySpec); // Ű������ ������ DESKeySpec ����
	} // getKey

	// lock �޴������� �̺�Ʈ
	public void lockFile() {

		// ���� ó��
		try {

			Cipher desCipher = Cipher.getInstance("DES"); // DES�� ��ȯ�� �����ϴ� Cipher ��ü ����
			Encoder encoder = Base64.getEncoder(); // Base64 ���ڴ� ��ü ����
			
			// Cipher ��ü ��ȣȭ ���� initialize getkey()�� �̿��� DES Ű�� ����
			desCipher.init(Cipher.ENCRYPT_MODE, getKey()); 

			byte[] text = textArea.getText().getBytes(); // textArea �ؽ�Ʈ ����Ʈȭ
			byte[] textEncrypted = desCipher.doFinal(text); // byte���� text�� DES ��ȣȭ

			String s = new String(encoder.encode(textEncrypted)); // ��ȣȭ�� ����Ʈ ������ textEncrypted ���ڵ�

			System.out.println(s);
			textArea.setText(s);

		} catch (Exception e) { // ��� ���� ó��

			System.out.println("Exception"); // ���� �߻��� "Exception"���
		} // try
	} // lockFile

	// unlock �޴������� �̺�Ʈ
	public void unlockFile() {

		// ���� ó��
		try {

			Cipher desCipher = Cipher.getInstance("DES"); // DES�� ��ȯ�� �����ϴ�
															// Cipher ��ü ����
			Decoder decoder = Base64.getDecoder(); // Base64 ���ڴ� ��ü ����

			// Cipher ��ü ��ȣȭ ���� initialize getkey()�� �̿��� DES Ű�� ����
			desCipher.init(Cipher.DECRYPT_MODE, getKey());

			byte[] text = decoder.decode(textArea.getText().getBytes()); // textArea �ؽ�Ʈ��  ����Ʈȭ�Ͽ� ���ڵ�
			byte[] textDecrypted = desCipher.doFinal(text); // ���ڵ� �� text�� DES ��ȣȭ

			String s = new String(textDecrypted); // ��ȣȭ�� ����Ʈ ������ textDecrypted

			System.out.println(s);
			textArea.setText(s);

		} catch (Exception e) { // ��� ���� ó��

			System.out.println("Exception"); // ���� �߻��� "Exception"���
		} // try
	} // unLockFile

	// exit �޴������� �̺�Ʈ
	public void exit() {
		System.exit(0); // ������ â ����
	} // exit

	public static void main(String[] args) throws InterruptedException {

		new FileLocker();
	} // main
} // FileLocker