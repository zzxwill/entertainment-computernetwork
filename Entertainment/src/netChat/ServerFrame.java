package netChat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 客户端的图形界面
 */
public class ServerFrame extends JFrame implements ActionListener {
	// "发送"按钮
	JButton sendButton; 
	// 发送内容的输入框
	JTextField inputField; 
	// 服务器返回内容的文本域
	JTextArea outputArea;

	// 客户端socket对象
	SimpleServer server=new SimpleServer(8000);

	// 在构造函数中完成图形界面的初始化
	public ServerFrame() {
		JLabel label1 = new JLabel("输入: ");
		inputField = new JTextField(20);
		JPanel panel1 = new JPanel();
		panel1.add(label1);
		panel1.add(inputField);

		JLabel label2 = new JLabel("客户端请求: ");
		outputArea = new JTextArea(6, 20); 
		JScrollPane crollPane = new JScrollPane(outputArea);
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(label2, BorderLayout.NORTH);
		panel2.add(crollPane, BorderLayout.CENTER);

		sendButton = new JButton("发 送");
		sendButton.addActionListener(this);
		
		JPanel panel = new JPanel(); 
		panel.setLayout(new BorderLayout()); 
		panel.add(panel1, BorderLayout.NORTH);
		panel.add(sendButton, BorderLayout.CENTER);
		panel.add(panel2, BorderLayout.PAGE_END);

		setTitle("Socket 服务端");
		this.getContentPane().add(panel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void actionPerformed(ActionEvent aeServer) {
		// 判断事件源控件是否是"发送"按钮

		if (aeServer.getSource() == sendButton) {
			
			try {
				// 发送文本框中的文本
				server.sendResponse(inputField.getText());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// 接收客户端请求并写入文本域
			outputArea.append(server.getRequest()+"\n");

		}
		

	}

	public static void main(String[] args) {
		ServerFrame frameServer = new ServerFrame();
		/*
		 * 调整窗口的大小为最佳大小
		 */
		frameServer.pack();
		// 连接服务器
//		frame.client = new SimpleClient("127.0.0.1", 8000); 
		frameServer.setVisible(true);

	}

}

