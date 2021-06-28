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

	static String keyValue = "password"; // 패스워드

	JFrame frame; 		// 프레임 생성
	JTextArea textArea; // 텍스트 영역
	String fileName; 	// 파일 이름

	FileReader fr; 		// 파일 읽기
	BufferedReader br; 	// 파일 입력 효율성
	StringWriter sw; 	// 파일 내용
	FileWriter fw; 		// 파일 쓰기
	BufferedWriter bw; 	// 파일 출력 효율성

	// FileLcoker 생성자
	FileLocker() throws Exception {

		loadMainImage();
		createMenu();
		createMainWindow();
	} // FileLocker	

	public void createMainWindow() {
		
		textArea = new JTextArea();
		
		JPanel panel = new JPanel();
		JLabel titleLabel = new JLabel();
		EtchedBorder eBorder = new EtchedBorder(EtchedBorder.RAISED);		// EtchedBorder로 텍스트 필드 디자인 추가
		
		titleLabel.setBorder(eBorder);
		titleLabel.setText("TEXT Field");
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setOpaque(true);										// BackGround 활성화
		titleLabel.setBackground(Color.darkGray);						// 배경 색상
		titleLabel.setForeground(Color.WHITE);							// 폰트 색상
		titleLabel.setFont(new Font("SansSerif",Font.BOLD, 17));		// setFont로 텍스트 필드 디자인 추가
		
		textArea.setBorder(eBorder);
		textArea.setFont(new Font("",Font.BOLD, 17));
		
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 10 , 10));	// 마진 추가
		panel.setBackground(Color.lightGray);
		
		panel.add(titleLabel, BorderLayout.NORTH);
		panel.add(textArea, BorderLayout.CENTER);
		
		getContentPane().add(panel);

		setTitle("FILE LOCKER");
		setSize(650, 450);
		setLocationRelativeTo(null); 					// 프레임이 모니터 가운데에 설정
		setDefaultCloseOperation(EXIT_ON_CLOSE); 		// 프레임의 정상적 종료 처리
		setVisible(true);
	}

	// 프로그램 구동 디자인아트 JSplash.jar 이용
	// https://m.blog.naver.com/ndb796/220621281539
	public void loadMainImage() throws InterruptedException {

		URL mainImageURL = FileLocker.class.getClassLoader().getResource("main.jpg");
		
		// main.jpg 를 대입한 JSplash 객체 생성
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

		// 메뉴바 객체 생성
		JMenuBar menuBar = new JMenuBar();

		// 메뉴 객체 생성
		JMenu menuFile = new JMenu("FILE");
		JMenu menuLock = new JMenu("LOCK");
		JMenu menuUnLock = new JMenu("UNLOCK");
		JMenu menuExit = new JMenu("EXIT");

		// 각 메뉴의 리스너 객체 생성
		MenuActionListener listener = new MenuActionListener();

		// 각 메뉴들을 메뉴바에 추가
		menuBar.add(menuFile);
		menuBar.add(menuLock);
		menuBar.add(menuUnLock);
		menuBar.add(menuExit);

		// 메뉴바 추가
		setJMenuBar(menuBar);

		// FILE메뉴의 메뉴아이템 객체 생성후, 메뉴아이템을 메뉴에 추가
		JMenuItem[] menuFileItem = new JMenuItem[3];
		String[] menuFileItemTitles = { "New", "Open", "Save" };

		for (int i = 0; i < menuFileItem.length; i++) {
			
			menuFileItem[i] = new JMenuItem(menuFileItemTitles[i]);
			menuFileItem[i].addActionListener(listener);
			menuFile.add(menuFileItem[i]);
		} // for

		// LOCK메뉴의 메뉴아이템 객체 생성, 메뉴아이템을 메뉴에 추가
		JMenuItem menuLockItem = new JMenuItem("DES Lock");
		menuLockItem.addActionListener(listener);
		menuLock.add(menuLockItem);

		// UNLOCK메뉴의 메뉴아이템 객체 생성, 메뉴아이템을 메뉴에 추가
		JMenuItem menuUnLockItem = new JMenuItem("DES UnLock");
		menuUnLockItem.addActionListener(listener);
		menuUnLock.add(menuUnLockItem);
		

		// EXIT메뉴의 메뉴아이템 객체 생성, 메뉴아이템을 메뉴에 추가
		JMenuItem menuExitItem = new JMenuItem("Exit");
		menuExitItem.addActionListener(listener);
		menuExit.add(menuExitItem);
	} // createMenu

	// 메뉴 아이템의 리스너 클래스
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

	// new 메뉴아이템 메소드
	public void newFile() {

		// 자바 다이얼로그 JOptionPane 이용
		int answer = JOptionPane.showConfirmDialog(this, "Are you sure?", "TEXT CLEAR", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (answer == JOptionPane.YES_OPTION) {		// TextClear 확인
			
			textArea.setText(""); 	// 텍스트 영역 초기화
			
			JOptionPane.showMessageDialog(this, "TEXT Cleared", "CLEAR", JOptionPane.INFORMATION_MESSAGE);
		} // if
	} // newFile

	// open 메뉴아이템 메소드
	public void openFile() {
		
		JFileChooser fileOpenChooser = new JFileChooser();
		FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("TXT 파일 | *.txt","txt");
		fileOpenChooser.setDialogTitle("파일 열기");
		fileOpenChooser.setFileFilter(txtFilter);
		
		if  (fileOpenChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {		// 사용자가 파일을 선택하고 "열기" 버튼을 누른 경우
			
			String filePath = fileOpenChooser.getSelectedFile().getPath();				// 선택 파일 경로 저장
			
			// 파일입출력을 위해 강제적 예외 처리
			try {

				int ch = 0;

				fr = new FileReader(filePath);
				br = new BufferedReader(fr);
				sw = new StringWriter();

				while ((ch = br.read()) != -1) {

					sw.write(ch);
				} // while

				br.close();
				textArea.setText(sw.toString()); 	// 텍스트 영역에 파일 내용 쓰기

			} catch (IOException e) { 	// 입출력 예외 처리

				e.printStackTrace();
				
				JOptionPane.showMessageDialog(this, "File Input Error!", "ERROR", JOptionPane.ERROR_MESSAGE);
			} // try
		} // if
	} // openFile

	// save 메뉴아이템 메소드
	public void saveFile() { 
		
		JFileChooser fileSaveChooser = new JFileChooser();
		FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("TXT 파일 | *.txt","txt");
		
		File selectedFile = null;
		int overWrite = 0;
		
		fileSaveChooser.setFileFilter(txtFilter);
		fileSaveChooser.setDialogTitle("파일 저장");
		
		if  (fileSaveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {		// 사용자가 파일을 선택하고 "저장" 버튼을 누른 경우

			selectedFile = new File(fileSaveChooser.getSelectedFile() + ".txt"); 		// 선택 파일 경로 이름 .txt 저장
			
			if (selectedFile.exists() == true) {										// selectedFile 이 이미 있는 경우
				
				// overWrite 판별
				overWrite = JOptionPane.showConfirmDialog(this, "The file aleady Exists. OverWrite?","WARNING",
	            		JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			} // if
			
			else {			// selectedFile 없는 경우
				
				overWrite = JOptionPane.YES_OPTION;	
			} //else
		} // if
		
		if (overWrite == JOptionPane.YES_OPTION) {
			
			// 파일입출력을 위해 강제적 예외 처리
			try {
				
				fw = new FileWriter(selectedFile);
				bw = new BufferedWriter(fw);
				bw.write(textArea.getText().replaceAll("\n", "\r\n"));
				bw.close();
				
			} catch (IOException ie) { 		// 입출력 예외 처리
	
				ie.printStackTrace();
				
				JOptionPane.showMessageDialog(this, "File Output Error!", "ERROR", JOptionPane.ERROR_MESSAGE);
			} // try
		} // if
	} // saveFile

	//Key 구현 메소드
	public static Key getKey() throws Exception { 								// DES 키값 획득
		
		DESKeySpec desKeySpec = new DESKeySpec(keyValue.getBytes()); 			// keyValue 바이트화하여 DESKeySpec에 저장
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); 		// DESKeySpec을 DES 형식의 키값으로 구현
		System.out.println("DES KEY ACCESS");
		
		return keyFactory.generateSecret(desKeySpec); 							// DESKeySpec key에 저장
	} // getKey

	// lock 메뉴아이템 메소드
	public void lockFile() {

		// 예외 처리
		try {

			Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); 		// DES형 변환을 구현하는 Cipher 객체 생성
			Encoder encoder = Base64.getEncoder(); 								// Base64 인코더 객체 생성

			// Cipher 객체 암호화 모드로 initialize getkey()를 이용해 DES 키값 대입
			desCipher.init(Cipher.ENCRYPT_MODE, getKey()); 

			byte[] text = textArea.getText().getBytes(); 						// textArea 텍스트 바이트화
			byte[] textEncrypted = desCipher.doFinal(text); 					// byte형식 text를 DES 암호화

			String s = new String(encoder.encode(textEncrypted)); 				// 암호화된 바이트 형식의 textEncrypted 인코딩

			System.out.println(s);
			textArea.setText(s);
			
			JOptionPane.showMessageDialog(this, "File LOCK Success", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e) { 	// 모든 예외 처리

			System.out.println("Exception");
			
			JOptionPane.showMessageDialog(this, "Not Access!", "ERROR", JOptionPane.ERROR_MESSAGE);
		} // try
	} // lockFile

	// unlock 메뉴아이템 메소드
	public void unlockFile() {

		// textArea 유효 검증
		if (textArea == null || textArea.getText().length() == 0) {
			
			JOptionPane.showMessageDialog(this, "TEXT does not Exist!", "ERROR", JOptionPane.ERROR_MESSAGE);
		} // if
		
		else {
			try {

				Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); 	// DES형 변환을 구현하는 Cipher 객체 생성
				Decoder decoder = Base64.getDecoder();	 						// Base64 디코더 객체 생성
	
				// Cipher 객체 복호화 모드로 initialize getkey()를 이용해 DES 키값 대입
				desCipher.init(Cipher.DECRYPT_MODE, getKey());
	
				byte[] text = decoder.decode(textArea.getText().getBytes()); 	// textArea 텍스트를  바이트화하여 디코딩
				byte[] textDecrypted = desCipher.doFinal(text);					// 디코딩 된 text를 DES 복호화
	
				String s = new String(textDecrypted); 							// 복호화된 바이트 형식의 textDecrypted
	
				System.out.println(s);
				textArea.setText(s);
	
				JOptionPane.showMessageDialog(this, "File UNLOCK Success", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
	
			} catch (Exception e) { 	// 모든 예외 처리
	
				System.out.println("Exception");
				
				JOptionPane.showMessageDialog(this, "Not Access!", "ERROR", JOptionPane.ERROR_MESSAGE);
			} // try
		}// else
	} // unLockFile

	// exit 메뉴아이템 메소드
	public void exit() {

		int answer = JOptionPane.showConfirmDialog(this, "Are you sure?", "EXIT",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (answer == JOptionPane.YES_OPTION) {		// EXIT
			
			System.exit(0); 						//종료
		} // if
	} // exit

	public static void main(String[] args) throws Exception {

		new FileLocker();
	} // main
} // FileLocker