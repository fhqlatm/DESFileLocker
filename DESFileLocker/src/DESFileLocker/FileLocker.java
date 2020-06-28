package DESFileLocker;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public class FileLocker extends JFrame {

   static String keyValue = "24_key_password_password";      //��ȣ�� ���õ� 24byte Ű�� 
   
   JFrame frame;            //������ ����
   JTextArea textArea;      //�ؽ�Ʈ ����
   String fileName;         //���� �̸�

   FileReader fr;           //���� �б�
   BufferedReader br;       //���� �д� ���� ȿ�������� ���̱� ����
   StringWriter sw;         //���� ����
   FileWriter fw;           //���� ����
   BufferedWriter bw;       //���� ���� ���� ȿ�������� ���̱� ����

   
   //FileLcoker ������
   FileLocker() {
      
      textArea = new JTextArea();                //�ؽ�Ʈ ���� ��ü ����
      
      setTitle("FILE LOCKER");                   //������ â �̸�
      createMenu(); 
      getContentPane().add(textArea);            //�ؽ�Ʈ ������ �����ӿ� �߰�
      setDefaultCloseOperation(EXIT_ON_CLOSE);   //�������� ������ ���� ó��
      setLocationRelativeTo(null);               //�������� ����� ����� ����
      setSize(550,400);
      setVisible(true);
   } // FileLocker
   
   public void createMenu() {    
      
      //�޴��� ��ü ����
      JMenuBar menuBar = new JMenuBar();
      
      //�޴� ��ü ����
      JMenu menuFile = new JMenu("FILE");
      JMenu menuLock = new JMenu("LOCK");
      JMenu menuUnLock = new JMenu("UNLOCK");
      JMenu menuExit = new JMenu("EXIT");
      
      //�� �޴��� ������ ��ü ����
      MenuActionListener listener = new MenuActionListener();
      
      //�� �޴����� �޴��ٿ� �߰�
      menuBar.add(menuFile);
      menuBar.add(menuLock);
      menuBar.add(menuUnLock);
      menuBar.add(menuExit);
      
      //�޴��� �߰�
      setJMenuBar(menuBar);
      
      //FILE�޴��� �޴������� ��ü ������, �޴��������� �޴��� �߰�
      JMenuItem [] menuFileItem = new JMenuItem [3];
      String[] menuFileItemTitles = {"New", "Open", "Save"};
      
      for(int i=0; i<menuFileItem.length; i++) {
         menuFileItem[i] = new JMenuItem(menuFileItemTitles[i]);
         menuFileItem[i].addActionListener(listener);
         menuFile.add(menuFileItem[i]);
      } //for
      
      //LOCK�޴��� �޴������� ��ü ����, �޴��������� �޴��� �߰�
      JMenuItem menuLockItem = new JMenuItem("Lock_DES");
      menuLockItem.addActionListener(listener);
      menuLock.add(menuLockItem);
      
      //UNLOCK�޴��� �޴������� ��ü ����, �޴��������� �޴��� �߰�
      JMenuItem menuUnLockItem = new JMenuItem("UnLock_DES");
      menuUnLockItem.addActionListener(listener);
      menuUnLock.add(menuUnLockItem);
      
      //EXIT�޴��� �޴�������  ��ü ����, �޴��������� �޴��� �߰�
      JMenuItem menuExitItem = new JMenuItem("Exit");
      menuExitItem.addActionListener(listener);
      menuExit.add(menuExitItem);
   } //createMenu
   
   //�޴� �������� ������ Ŭ����
   class MenuActionListener implements ActionListener {
      
      public void actionPerformed(ActionEvent e) {
         
         switch(e.getActionCommand()) {
         
            case "New" : 
               newFile();
               break;
               
            case "Open" :
               openFile();
               break;
               
            case "Save" : 
               saveFile();
               break;
               
            case "Lock_DES" :
               lockFile();
               break;
               
            case "UnLock_DES" :
               unlockFile();
               break;
               
            case "Exit" : 
               exit();
               break;
         } //switch
      } //actionPerformed
   } //MenuActionListener
   
   //new �޴������� �̺�Ʈ
   public void newFile() {
      
      textArea.setText("");      //�ؽ�Ʈ ������ ����ȭ
   } //newFile
   
   //open �޴������� �̺�Ʈ
   public void openFile() {
      
      //������ �ҷ� ���̴� ��ü ����
      FileDialog fileOpen = new FileDialog(frame, "���Ͽ���", FileDialog.LOAD); //��������� ���̾�α� ����
      fileOpen.setVisible(true);
      fileName = fileOpen.getDirectory() + fileOpen.getFile();      		 //���� ��� �� ���� �̸� �� Ȯ���� ����
      System.out.println(fileName);
      
      //����������� ���� ������ ���� ó��
      try {

         int ch = 0;
         
         fr = new FileReader(fileName);
         br = new BufferedReader(fr);
         sw = new StringWriter();
      
         while ((ch=br.read())!=-1) {
            
            sw.write(ch);
         } //while
         
         br.close();                   		   //���� �ݱ�
         textArea.setText(sw.toString());      //�ؽ�Ʈ ������ ���� ���� ����
         
      } catch(IOException e) {     //����� ���� ó��
         
         e.printStackTrace();      //���ϰ� X, ���� �ڼ��� ���ܰ�� ����
      } //try
   } //openFile
   
   //save �޴������� �̺�Ʈ
   public void saveFile() {
      
      //������ �����ϴ� ��ü ����
      FileDialog fileSave = new FileDialog(frame, "��������", FileDialog.SAVE); //��������� ���̾�α� ����
      fileSave.setVisible(true);
      fileName = fileSave.getDirectory() + fileSave.getFile();      		 //���� ��� �� ���� �̸� �� Ȯ���� ����
      System.out.println(fileName);
      
      //����������� ���� ������ ���� ó��
      try {
         
         fw = new FileWriter(fileName);
         bw = new BufferedWriter(fw);
         bw.write(textArea.getText());   //���� �����ϱ�
         bw.close();             	     //���� �ݱ�
         
      } catch (IOException ie) {      //����� ���� ó��
          
          ie.printStackTrace();       //���ϰ� X, ���� �ڼ��� ���ܰ�� ����
       } //try
   } //saveFile
   
   public static Key getKey() throws Exception {      //DES Ű�� ȹ��
        
       System.out.println("DES KEY ACCESS");
       DESKeySpec desKeySpec = new DESKeySpec(keyValue.getBytes()); 		//keyValue ����Ʈȭ�Ͽ� DESKeySpec�� ���� 
       SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");   //DESKeySpec�� DES ������ Ű������ ����
        
       return keyFactory.generateSecret(desKeySpec); 			   			//Ű������ ������ DESKeySpec ����
    } //getKey
   
    //lock �޴������� �̺�Ʈ
   public void lockFile() {
      
      //���� ó��
      try {

         Cipher desCipher = Cipher.getInstance("DES");      	//DES�� ��ȯ�� �����ϴ� Cipher ��ü ����
         Encoder encoder = Base64.getEncoder();            		//Base64 ���ڴ� ��ü ����
         
         desCipher.init(Cipher.ENCRYPT_MODE, getKey());      	//Cipher ��ü  ��ȣȭ ���� initialize
         													 	//getkey()�� �̿��� DES Ű�� ����
            
         byte[] text = textArea.getText().getBytes();         	//textArea �ؽ�Ʈ ����Ʈȭ
         byte[] textEncrypted = desCipher.doFinal(text);      	//byte���� text�� DES ��ȣȭ
            
         String s = new String(encoder.encode(textEncrypted));  //��ȣȭ�� ����Ʈ ������ textEncrypted string ���ڵ�
            
         System.out.println(s);
         textArea.setText(s);
               
            
      } catch(Exception e) {               	//���  ���� ó��
           
         System.out.println("Exception");   //���� �߻��� "Exception"���
      } //try
   } //lockFile
   
   //unlock �޴������� �̺�Ʈ
   public void unlockFile() {
      
      //���� ó��
      try {
           
         Cipher desCipher = Cipher.getInstance("DES");   			   //DES�� ��ȯ�� �����ϴ� Cipher ��ü ����
         Decoder decoder = Base64.getDecoder();          			   //Base64 ���ڴ� ��ü ����
         
         desCipher.init(Cipher.DECRYPT_MODE, getKey());        		   //Cipher ��ü  ��ȣȭ ���� initialize
			 												   		   //getkey()�� �̿��� DES Ű�� ����
            
         byte[] text = decoder.decode(textArea.getText().getBytes());  //textArea �ؽ�Ʈ ����Ʈȭ �Ͽ� ���ڵ�
         byte[] textDecrypted = desCipher.doFinal(text);               //���ڵ� �� text�� DES ��ȣȭ
            
         String s = new String(textDecrypted); //��ȣȭ�� ����Ʈ ������  textDecrypted 
            
         System.out.println(s);
         textArea.setText(s);
         
      } catch(Exception e) {               	//���  ���� ó��
           
         System.out.println("Exception");   //���� �߻��� "Exception"���
      } //try
   } //unLockFile
   
   //exit �޴������� �̺�Ʈ
   public void exit() {
      System.exit(0);         //������ â ����
   } //exit
   
   public static void main(String [] args) {
      
      new FileLocker();
   } //main
} //FileLocker