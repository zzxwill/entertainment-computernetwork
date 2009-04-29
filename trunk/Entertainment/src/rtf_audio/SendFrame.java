package rtf_audio;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
public class SendFrame extends JFrame{    
	private String fileName = null;
	  private RTPSender rtpTransmit = null;           
	  private JLabel fileTypeLabel = null;
	  JRadioButton movFileRB = null;
	  JRadioButton auFileRB = null;
	  JRadioButton mpegFileRB = null;
	  ButtonGroup bGroup = null;
	  JButton browsBut = null;
	  JTextField fileNameText =null;
	  JLabel desMchLabel=null;
	  JLabel ipLabel =null;
	  JTextField ipText =null;
	  JLabel portLabel =null;
	  JTextField portText =null;
	  JButton tranBut =null;
	  JButton stopBut =null; 
	  // ��ʼ������
	  private void jbInit() {
	    this.setLayout(null);
	    this.setBackground(Color.DARK_GRAY);
	    this.getContentPane().setLayout(null);
	    fileTypeLabel= new JLabel("");
	    fileTypeLabel.setFont(new Font("", Font.BOLD, 20));
	    fileTypeLabel.setPreferredSize(new Dimension(200,500));
	    fileTypeLabel.setBounds(100, 50, 200, 50);
	    getContentPane().add(fileTypeLabel);
	    movFileRB = new JRadioButton(".mov�ļ�");
	    movFileRB.setPreferredSize(new Dimension(200,30));
	    movFileRB.setBounds(100, 110, 200, 30);
//	    getContentPane().add(movFileRB);
	    auFileRB = new JRadioButton(".wav�ļ�");
	    auFileRB.setPreferredSize(new Dimension(200,30));
	    auFileRB.setBounds(100, 150, 200, 30);
	    getContentPane().add(auFileRB);
	    auFileRB.setSelected(true);
	    
	    mpegFileRB =new JRadioButton(".mepg��.mpg�ļ�");
	    mpegFileRB.setPreferredSize(new Dimension(200,30));
	    mpegFileRB.setBounds(100, 190, 200, 30);
	    mpegFileRB.setSelected(true);
//	    getContentPane().add(mpegFileRB);
	    bGroup =new ButtonGroup();
	    bGroup.add(movFileRB);
	    bGroup.add(auFileRB);
	    bGroup.add(mpegFileRB);
	    browsBut = new JButton("ѡ���ļ�");
	    browsBut.setPreferredSize(new Dimension(100,30));
	    browsBut.setBounds(80, 230, 120, 30);
	    getContentPane().add(browsBut);
	    fileNameText= new JTextField("");
	    fileNameText.setPreferredSize(new Dimension(200,30));
	    fileNameText.setBounds(210,230,200,30);
	    getContentPane().add(fileNameText);
	    desMchLabel =new JLabel("Ŀ�ĵػ�����Ϣ");
	    desMchLabel.setFont(new Font("", Font.BOLD, 20));
	    desMchLabel.setPreferredSize(new Dimension(200,30));
	    desMchLabel.setBounds(100, 280, 200, 30);
	    getContentPane().add(desMchLabel);
	    ipLabel = new JLabel("IP��ַ:");
	    ipLabel.setPreferredSize(new Dimension(80,30));
	    ipLabel.setBounds(100, 320, 80, 30);
	    getContentPane().add(ipLabel);
	    ipText = new JTextField("");
	    ipText.setPreferredSize(new Dimension(200,30));
	    ipText.setBounds(190,320,200,30);
	    getContentPane().add(ipText);
	    portLabel=new JLabel("�˿�:");
	    portLabel.setPreferredSize(new Dimension(80,30));
	    portLabel.setBounds(100,360,80,30);
	    getContentPane().add(portLabel);
	    portText=new JTextField();
	    portText.setPreferredSize(new Dimension(100,30));
	    portText.setBounds(190,360,100,30);
	    getContentPane().add(portText);
	    tranBut=new JButton("����");
	    tranBut.setPreferredSize(new Dimension(80,30));
	    tranBut.setBounds(150,400,80,30);
	    getContentPane().add(tranBut);
	    stopBut=new JButton("ֹͣ");
	    stopBut.setPreferredSize(new Dimension(80,30));
	    stopBut.setBounds(300,400,80,30);
	    getContentPane().add(stopBut);
	    browsBut.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        browsButPress(e);
	      }
	    });
        tranBut.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        tranButPress(e);
	      }
	    });
        stopBut.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        stopButPress(e);
	      }
	    });
	    this.addWindowListener(new java.awt.event.WindowAdapter() {
	      public void windowClosing(WindowEvent e) {
	        this_windowClosing(e);
	      }
	    });
	    // ���ñ���
	    this.setTitle("RTP����");                  
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// �Ѵ��ڷ�����Ļ�м�
		this.setPreferredSize(new Dimension(500, 600));
		this.setBounds(screenSize.width / 2 - 250, screenSize.height / 2 - 300,
				500, 600);
		this.setVisible(true);
		setResizable(false);
		pack();
	  }
	  public SendFrame(){
	  try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}	   
	      jbInit();            // ��ʾ������	    
	  }
	  // �õ����贫���ļ�������
	  int getFileType() {
	    int index = 0;
	    if(movFileRB.isSelected()){
	    	index=0;
	    }
	    if(auFileRB.isSelected()){
	    	index = 1;
	    }
	    if(mpegFileRB.isSelected()){
	    	index=2;
	    }
	    return index;
	  }
	  // ��Ӧ��ѡ���ļ�����ť�ĵ����Ϣ
	  void browsButPress(ActionEvent e) {
	    JFileChooser fileChooser = new JFileChooser("E:");
	    MyFileFilter filter = new MyFileFilter();
	    //��������ļ�����
	    int iTypeFile = getFileType();
	    switch(iTypeFile)
	    {
	    case 0:
	      filter.putExtType("mov"); 
	      filter.setComment("QuickTime Files");
	      break;
	    case 1:
	      filter.putExtType("au");
	      filter.putExtType("wav");
	      filter.setComment("Audio Files");
	      break;
	    case 2:
	      filter.putExtType("mpg");
	      filter.putExtType("mpeg");
	      filter.setComment("MPEG Files");
	      break;
	    }
	    fileChooser.setFileFilter(filter);
	    int retVal = fileChooser.showOpenDialog(this);
	    if(retVal == JFileChooser.APPROVE_OPTION){
	        fileName = fileChooser.getSelectedFile().getAbsolutePath();
	        fileNameText.setText(fileName);
	    }
	  }
	  // ��Ӧ�����䡱��ť�ĵ����Ϣ����ʼ��������
	  void tranButPress(ActionEvent e) {
	    String strIPAddr = ipText.getText();
	    //��ö˿ں�
	    String strPort = portText.getText();
        //�õ��ļ���
	    fileName = fileNameText.getText();
	    fileName = "file:/" + fileName;
	    MediaLocator medLoc = new MediaLocator(fileName);
	    Format fmt = null;
	    rtpTransmit = new RTPSender(fmt,strIPAddr,medLoc,strPort);
        //��ʼ����
	    boolean result = rtpTransmit.start(); 
	    if (result==false) {    
	    	// �������
	    	System.out.println("����ʧ�ܣ�");
	    }
	    else {
	    	//��ʼ����
	      System.out.println("��ʼ���� ...");
	    }
	  }
	  // ����ֹͣ��ť�¼�
	  void stopButPress(ActionEvent e) {
	    if(rtpTransmit == null)
	      return;
       //ֹͣ����
	    rtpTransmit.stop();
	    System.out.println("...�������.");
	  }
	  // ��Ӧ�����¼�
	  void this_windowClosing(WindowEvent e) {
	    System.exit(0);
	  }
	  public static void main(String [] args) {
	    SendFrame sf = new SendFrame();
	  }
	}
