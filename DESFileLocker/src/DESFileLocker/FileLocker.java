package DESFileLocker;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.thehowtotutorial.splashscreen.JSplash;
import java.net.URL;

import java.io.*;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
import java.util.StringTokenizer;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class FileLocker extends JFrame {

	static String keyValue = "password"; // �н�����

	JFrame frame; 		// ������ ����
	JTextArea textArea; // �ؽ�Ʈ ����
	String fileName; 	// ���� �̸�

	FileReader fr; 		// ���� �б�
	BufferedReader br; 	// ���� �Է� ȿ����
	StringWriter sw; 	// ���� ����
	FileWriter fw; 		// ���� ����
	BufferedWriter bw; 	// ���� ��� ȿ����

	// FileLcoker ������
	FileLocker() throws Exception {

		loadMainImage();
		createMenu();
		createMainWindow();
	} // FileLocker	

	public void createMainWindow() {
		
		textArea = new JTextArea();
		
		JPanel panel = new JPanel();
		JLabel titleLabel = new JLabel();
		EtchedBorder eBorder = new EtchedBorder(EtchedBorder.RAISED);		// EtchedBorder�� �ؽ�Ʈ �ʵ� ������ �߰�
		
		titleLabel.setBorder(eBorder);
		titleLabel.setText("TEXT Field");
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setOpaque(true);										// BackGround Ȱ��ȭ
		titleLabel.setBackground(Color.darkGray);						// ��� ����
		titleLabel.setForeground(Color.WHITE);							// ��Ʈ ����
		titleLabel.setFont(new Font("SansSerif",Font.BOLD, 17));		// setFont�� �ؽ�Ʈ �ʵ� ������ �߰�
		
		textArea.setBorder(eBorder);
		textArea.setFont(new Font("",Font.BOLD, 17));
		
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 10 , 10));	// ���� �߰�
		panel.setBackground(Color.lightGray);
		
		panel.add(titleLabel, BorderLayout.NORTH);
		panel.add(textArea, BorderLayout.CENTER);
		
		getContentPane().add(panel);

		setTitle("FILE LOCKER");
		setSize(650, 450);
		setLocationRelativeTo(null); 					// �������� ����� ����� ����
		setDefaultCloseOperation(EXIT_ON_CLOSE); 		// �������� ������ ���� ó��
		setVisible(true);
	}

	// ���α׷� ���� �����ξ�Ʈ JSplash.jar �̿�
	// https://m.blog.naver.com/ndb796/220621281539
	public void loadMainImage() throws InterruptedException {

		URL mainImageURL = FileLocker.class.getClassLoader().getResource("main.jpg");
		
		// main.jpg �� ������ JSplash ��ü ����
		JSplash splash = new JSplash(mainImageURL, true, true, false, "Version 1.0.0", null, Color.RED, Color.BLACK);

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
	} // loadMainImage

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
		JMenuItem menuLockItem = new JMenuItem("DES Lock");
		menuLockItem.addActionListener(listener);
		menuLock.add(menuLockItem);

		// UNLOCK�޴��� �޴������� ��ü ����, �޴��������� �޴��� �߰�
		JMenuItem menuUnLockItem = new JMenuItem("DES UnLock");
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

			case "DES Lock":
				lockFile();
				break;

			case "DES UnLock":
				unlockFile();
				break;

			case "Exit":
				exit();
				break;
			} // switch
		} // actionPerformed
	} // MenuActionListener

	// new �޴������� �޼ҵ�
	public void newFile() {

		// �ڹ� ���̾�α� JOptionPane �̿�
		int answer = JOptionPane.showConfirmDialog(this, "Are you sure?", "TEXT CLEAR", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (answer == JOptionPane.YES_OPTION) {		// TextClear Ȯ��
			
			textArea.setText(""); 	// �ؽ�Ʈ ���� �ʱ�ȭ
			
			JOptionPane.showMessageDialog(this, "TEXT Cleared", "CLEAR", JOptionPane.INFORMATION_MESSAGE);
		} // if
	} // newFile

	// open �޴������� �޼ҵ�
	public void openFile() {
		
		JFileChooser fileOpenChooser = new JFileChooser();
		FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("TXT ���� | *.txt","txt");
		fileOpenChooser.setDialogTitle("���� ����");
		fileOpenChooser.setFileFilter(txtFilter);
		
		if  (fileOpenChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {		// ����ڰ� ������ �����ϰ� "����" ��ư�� ���� ���
			
			String filePath = fileOpenChooser.getSelectedFile().getPath();				// ���� ���� ��� ����
			
			// ����������� ���� ������ ���� ó��
			try {

				int ch = 0;

				fr = new FileReader(filePath);
				br = new BufferedReader(fr);
				sw = new StringWriter();

				while ((ch = br.read()) != -1) {

					sw.write(ch);
				} // while

				br.close();
				textArea.setText(sw.toString()); 	// �ؽ�Ʈ ������ ���� ���� ����

			} catch (IOException e) { 	// ����� ���� ó��

				e.printStackTrace();
				
				JOptionPane.showMessageDialog(this, "File Input Error!", "ERROR", JOptionPane.ERROR_MESSAGE);
			} // try
		} // if
	} // openFile

	// save �޴������� �޼ҵ�
	public void saveFile() { 
		
		JFileChooser fileSaveChooser = new JFileChooser();
		FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("TXT ���� | *.txt","txt");
		
		File selectedFile = null;
		int overWrite = 0;
		
		fileSaveChooser.setFileFilter(txtFilter);
		fileSaveChooser.setDialogTitle("���� ����");
		
		if  (fileSaveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {		// ����ڰ� ������ �����ϰ� "����" ��ư�� ���� ���

			selectedFile = new File(fileSaveChooser.getSelectedFile() + ".txt"); 		// ���� ���� ��� �̸� .txt ����
			
			if (selectedFile.exists() == true) {										// selectedFile �� �̹� �ִ� ���
				
				// overWrite �Ǻ�
				overWrite = JOptionPane.showConfirmDialog(this, "The file aleady Exists. OverWrite?","WARNING",
	            		JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			} // if
			
			else {			// selectedFile ���� ���
				
				overWrite = JOptionPane.YES_OPTION;	
			} //else
		} // if
		
		if (overWrite == JOptionPane.YES_OPTION) {
			
			// ����������� ���� ������ ���� ó��
			try {
				
				fw = new FileWriter(selectedFile);
				bw = new BufferedWriter(fw);
				bw.write(textArea.getText().replaceAll("\n", "\r\n"));
				bw.close();
				
			} catch (IOException ie) { 		// ����� ���� ó��
	
				ie.printStackTrace();
				
				JOptionPane.showMessageDialog(this, "File Output Error!", "ERROR", JOptionPane.ERROR_MESSAGE);
			} // try
		} // if
	} // saveFile

	//Key ���� �޼ҵ�
	public static Key getKey() throws Exception { 								// DES Ű�� ȹ��
		
		DESKeySpec desKeySpec = new DESKeySpec(keyValue.getBytes()); 			// keyValue ����Ʈȭ�Ͽ� DESKeySpec�� ����
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); 		// DESKeySpec�� DES ������ Ű������ ����
		System.out.println("DES KEY ACCESS");
		
		return keyFactory.generateSecret(desKeySpec); 							// DESKeySpec key�� ����
	} // getKey

	// lock �޴������� �޼ҵ�
	public void lockFile() {

		// ���� ó��
		try {

			Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); 		// DES�� ��ȯ�� �����ϴ� Cipher ��ü ����
			Encoder encoder = Base64.getEncoder(); 								// Base64 ���ڴ� ��ü ����

			// Cipher ��ü ��ȣȭ ���� initialize getkey()�� �̿��� DES Ű�� ����
			desCipher.init(Cipher.ENCRYPT_MODE, getKey()); 

			byte[] text = textArea.getText().getBytes(); 						// textArea �ؽ�Ʈ ����Ʈȭ
			byte[] textEncrypted = desCipher.doFinal(text); 					// byte���� text�� DES ��ȣȭ

			String s = new String(encoder.encode(textEncrypted)); 				// ��ȣȭ�� ����Ʈ ������ textEncrypted ���ڵ�

			System.out.println(s);
			textArea.setText(s);
			
			JOptionPane.showMessageDialog(this, "File LOCK Success", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e) { 	// ��� ���� ó��

			System.out.println("Exception");
			
			JOptionPane.showMessageDialog(this, "Not Access!", "ERROR", JOptionPane.ERROR_MESSAGE);
		} // try
	} // lockFile

	// unlock �޴������� �޼ҵ�
	public void unlockFile() {

		// textArea ��ȿ ����
		if (textArea == null || textArea.getText().length() == 0) {
			
			JOptionPane.showMessageDialog(this, "TEXT does not Exist!", "ERROR", JOptionPane.ERROR_MESSAGE);
		} // if
		
		else {
			try {

				Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); 	// DES�� ��ȯ�� �����ϴ� Cipher ��ü ����
				Decoder decoder = Base64.getDecoder();	 						// Base64 ���ڴ� ��ü ����
	
				// Cipher ��ü ��ȣȭ ���� initialize getkey()�� �̿��� DES Ű�� ����
				desCipher.init(Cipher.DECRYPT_MODE, getKey());
	
				byte[] text = decoder.decode(textArea.getText().getBytes()); 	// textArea �ؽ�Ʈ��  ����Ʈȭ�Ͽ� ���ڵ�
				byte[] textDecrypted = desCipher.doFinal(text);					// ���ڵ� �� text�� DES ��ȣȭ
	
				String s = new String(textDecrypted); 							// ��ȣȭ�� ����Ʈ ������ textDecrypted
	
				System.out.println(s);
				textArea.setText(s);
	
				JOptionPane.showMessageDialog(this, "File UNLOCK Success", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
	
			} catch (Exception e) { 	// ��� ���� ó��
	
				System.out.println("Exception");
				
				JOptionPane.showMessageDialog(this, "Not Access!", "ERROR", JOptionPane.ERROR_MESSAGE);
			} // try
		}// else
	} // unLockFile

	// exit �޴������� �޼ҵ�
	public void exit() {

		int answer = JOptionPane.showConfirmDialog(this, "Are you sure?", "EXIT",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (answer == JOptionPane.YES_OPTION) {		// EXIT
			
			System.exit(0); 						//����
		} // if
	} // exit

	public static void main(String[] args) throws Exception {

		new FileLocker();
	} // main
} // FileLocker