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

	static String keyValue = "1111111111222222222233333333333"; // 암호로 제시될 24byte 키값

	JFrame frame; // 프레임 생성
	JTextArea textArea; // 텍스트 영역
	String fileName; // 파일 이름

	FileReader fr; // 파일 읽기
	BufferedReader br; // 파일 읽는 것을 효율적으로 높이기 위함
	StringWriter sw; // 파일 내용
	FileWriter fw; // 파일 쓰기
	BufferedWriter bw; // 파일 쓰는 것을 효율적으로 높이기 위함

	// FileLcoker 생성자
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
		
		getContentPane().add(panel); // 텍스트 영역을 프레임에 추가

		setTitle("FILE LOCKER");
		setSize(650, 450);
		setLocationRelativeTo(null); // 프레임이 모니터 가운데에 설정
		setDefaultCloseOperation(EXIT_ON_CLOSE); // 프레임의 정상적 종료 처리
		setVisible(true);
	}

	// 프로그램 구동 디자인 JSplash.jar 이용
	// https://m.blog.naver.com/ndb796/220621281539
	public void loadMainImage() throws InterruptedException {

		URL mainImageURL = FileLocker.class.getClassLoader().getResource("main.jpg");
		
		// main.jpg 를 대입한 JSplash 객체 생성
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
		JMenuItem menuLockItem = new JMenuItem("Lock_DES");
		menuLockItem.addActionListener(listener);
		menuLock.add(menuLockItem);

		// UNLOCK메뉴의 메뉴아이템 객체 생성, 메뉴아이템을 메뉴에 추가
		JMenuItem menuUnLockItem = new JMenuItem("UnLock_DES");
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

	// new 메뉴아이템 이벤트
	public void newFile() {

		textArea.setText(""); // 텍스트 영역의 백지화
	} // newFile

	// open 메뉴아이템 이벤트
	public void openFile() {

		// 파일을 불러 들이는 객체 생성
		FileDialog fileOpen = new FileDialog(frame, "파일열기", FileDialog.LOAD);
		fileOpen.setLocationRelativeTo(null);
		fileOpen.setVisible(true); // 파일입출력 다이얼로그 생성
		fileName = fileOpen.getDirectory() + fileOpen.getFile(); // 파일 경로 와 파일
																	// 이름 및 확장자
																	// 대입
		System.out.println(fileName);

		// 파일입출력을 위해 강제적 예외 처리
		try {

			int ch = 0;

			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			sw = new StringWriter();

			while ((ch = br.read()) != -1) {

				sw.write(ch);
			} // while

			br.close(); // 버퍼 닫기
			textArea.setText(sw.toString()); // 텍스트 영역에 파일 내용 쓰기

		} catch (IOException e) { // 입출력 예외 처리

			e.printStackTrace(); // 리턴값 X, 가장 자세한 예외결과 제시
		} // try
	} // openFile

	// save 메뉴아이템 이벤트
	public void saveFile() {

		// 파일을 저장하는 객체 생성
		FileDialog fileSave = new FileDialog(frame, "파일저장", FileDialog.SAVE); // 파일입출력
																				// 다이얼로그
																				// 생성
		fileSave.setVisible(true);
		fileName = fileSave.getDirectory() + fileSave.getFile(); // 파일 경로 와 파일
																	// 이름 및 확장자
																	// 대입
		System.out.println(fileName);

		// 파일입출력을 위해 강제적 예외 처리
		try {

			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			bw.write(textArea.getText()); // 파일 저장하기
			bw.close(); // 버퍼 닫기

		} catch (IOException ie) { // 입출력 예외 처리

			ie.printStackTrace(); // 리턴값 X, 가장 자세한 예외결과 제시
		} // try
	} // saveFile

	public static Key getKey() throws Exception { // DES 키값 획득

		System.out.println("DES KEY ACCESS");
		DESKeySpec desKeySpec = new DESKeySpec(keyValue.getBytes()); // keyValue 바이트화하여 DESKeySpec에 저장
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); // DESKeySpec을 DES 형식의 키값으로 구현

		return keyFactory.generateSecret(desKeySpec); // 키값으로 구현된 DESKeySpec 리턴
	} // getKey

	// lock 메뉴아이템 이벤트
	public void lockFile() {

		// 예외 처리
		try {

			Cipher desCipher = Cipher.getInstance("DES"); // DES형 변환을 구현하는 Cipher 객체 생성
			Encoder encoder = Base64.getEncoder(); // Base64 인코더 객체 생성
			
			// Cipher 객체 암호화 모드로 initialize getkey()를 이용해 DES 키값 대입
			desCipher.init(Cipher.ENCRYPT_MODE, getKey()); 

			byte[] text = textArea.getText().getBytes(); // textArea 텍스트 바이트화
			byte[] textEncrypted = desCipher.doFinal(text); // byte형식 text를 DES 암호화

			String s = new String(encoder.encode(textEncrypted)); // 암호화된 바이트 형식의 textEncrypted 인코딩

			System.out.println(s);
			textArea.setText(s);

		} catch (Exception e) { // 모든 예외 처리

			System.out.println("Exception"); // 예외 발생시 "Exception"출력
		} // try
	} // lockFile

	// unlock 메뉴아이템 이벤트
	public void unlockFile() {

		// 예외 처리
		try {

			Cipher desCipher = Cipher.getInstance("DES"); // DES형 변환을 구현하는
															// Cipher 객체 생성
			Decoder decoder = Base64.getDecoder(); // Base64 디코더 객체 생성

			// Cipher 객체 복호화 모드로 initialize getkey()를 이용해 DES 키값 대입
			desCipher.init(Cipher.DECRYPT_MODE, getKey());

			byte[] text = decoder.decode(textArea.getText().getBytes()); // textArea 텍스트를  바이트화하여 디코딩
			byte[] textDecrypted = desCipher.doFinal(text); // 디코딩 된 text를 DES 복호화

			String s = new String(textDecrypted); // 복호화된 바이트 형식의 textDecrypted

			System.out.println(s);
			textArea.setText(s);

		} catch (Exception e) { // 모든 예외 처리

			System.out.println("Exception"); // 예외 발생시 "Exception"출력
		} // try
	} // unLockFile

	// exit 메뉴아이템 이벤트
	public void exit() {
		System.exit(0); // 프레임 창 종료
	} // exit

	public static void main(String[] args) throws InterruptedException {

		new FileLocker();
	} // main
} // FileLocker