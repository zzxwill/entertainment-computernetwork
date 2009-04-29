package rtf_audio;
import java.net.*;
import java.awt.*;
import javax.media.rtp.*;
import javax.media.rtp.event.*;
import java.awt.event.*;
import java.util.Vector;
import javax.media.*;
import javax.media.protocol.DataSource;
import javax.media.control.BufferControl;

// MyReceiver�����ڽ���RTP����
public class MyReceiver implements ReceiveStreamListener, SessionListener,ControllerListener {
  //�洢rpt�Ự��Ϣ����
  String rtpSessions[] = null; 
  //RTP����������
  RTPManager rtpManagers[] = null;     
  //��Ź����������ڵ�����
  Vector playerFrames = null;         
  //�Ƿ���յ����ݵı�־
  boolean isDataArrived = false;        
  //����ͬ��������ͬ������
  Object synObj = new Object();          
  public MyReceiver(String rtpSessions[]) {
    this.rtpSessions = rtpSessions;
  }
  // �رղ������ͻỰ������
  protected void shutDownAll() {
    // �رղ��Ŵ���
    for (int i = 0; i < playerFrames.size(); i++) {
      try {
        ((PlayerFrame)playerFrames.elementAt(i)).close();
      }
      catch (Exception e) {}
    }
   // ɾ�����в��Ŵ���
    playerFrames.removeAllElements();          
    // �ر�RTP�Ự�������Լ�RTP�Ự
    for (int i = 0; i < rtpManagers. length; i++) {
      if (rtpManagers[i] != null) {
        rtpManagers[i].removeTargets( "�ر�RTP�Ự");
        rtpManagers[i].dispose();                      
        rtpManagers[i] = null;
      }
    }
  }
  // ׼���������ݣ���ʼ��RTP�Ự
  protected boolean iniReceiver() {
		try {
			// ���в��Ŵ��ڷ�������ͳһ����
			playerFrames = new Vector();
			// Ϊÿһ��RTP�Ự����һ��������
			rtpManagers = new RTPManager[rtpSessions. length];
			SessionARP seLabel;
			for (int i = 0; i < rtpSessions. length; i++) {
				// ����ÿһ��RTP�Ự
				try {
					// ����RTP�Ự��ַ
					seLabel = new SessionARP(rtpSessions[i]);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					System.out.println("���ܽ���: " + rtpSessions[i]);
					return false;
				}
				System.out.println("  - RTP �Ự����: ��ַ: " + seLabel.addr
						+ " �˿ں�: " + seLabel.port);
				// Ϊÿһ��RTP�Ự����һ��RTP������
				rtpManagers[i] = (RTPManager) RTPManager.newInstance();
				// ע��������������
				rtpManagers[i].addReceiveStreamListener(this);
				// ע��Ự������
				rtpManagers[i].addSessionListener(this);
				// ��÷��ͷ�IP��ַ
				InetAddress ipAddr = InetAddress.getByName(seLabel.addr);
				// ���ص�ַ��Ϣ
				SessionAddress localAddr = null;
				// Ŀ�ĵ�ַ��Ϣ
				SessionAddress destAddr = null;
				if (ipAddr.isMulticastAddress()) {
					// ������鲥�����غ�Ŀ�ĵص�IP��ַ��ͬ
					localAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println("is MulticastAddress, localAddr = "
							+ localAddr.toString() + ", port=" + seLabel.port);
					destAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println("is MulticastAddress, destAddr = "
							+ destAddr.toString() + ", port =" + seLabel.port);
				} else {
					// �ñ���IP��ַ�Ͷ˿ںŹ���Դ�Ự��ַ
					localAddr = new SessionAddress(InetAddress.getLocalHost(),
							seLabel.port);
					System.out.println(" localAddr = " + localAddr.toString()
							+ ", port=" + seLabel.port);
					// ��Ŀ�Ļ������Ͷˣ���IP��ַ�Ͷ˿ںŹ���Ŀ�ĻỰ��ַ
					destAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println(" destAddr = " + destAddr.toString()
							+ ", port =" + seLabel.port);
				}
				// ʹ�ñ����Ự��ַ��ʼ��RTP������
				rtpManagers[i].initialize(localAddr);
				BufferControl bc = (BufferControl) rtpManagers[i]
						.getControl("javax.media.control.BufferControl");
				if (bc != null)
					// �������û�������С
					bc.setBufferLength(500);
				// ����Ŀ�ĻỰ��ַ
				rtpManagers[i].addTarget(destAddr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("����RTP�Ựʧ��: " + e.getMessage());
			return false;
		}
		// ֱ�����ݽ���
		long waitTime = System.currentTimeMillis();
		// ������ȴ�ʱ��60��
		long maxTime = 60000;
		try {
			synchronized (synObj) {
				while (!isDataArrived
						&& System.currentTimeMillis() - waitTime < maxTime) {
					// �ȴ��趨��ʱ��
					if (!isDataArrived)
						System.out.println("  - �ȴ�RTP������...");
					synObj.wait(1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			shutDownAll();
			return false;
		}
		if (!isDataArrived) {
			// �������û�е�
			System.err.println("û�н��յ�RTP���ݣ�");
			shutDownAll();
			return false;
		}
		return true;
	}
  // ������ݴ�������򷵻�true�����򷵻�false
  public boolean isFinished() {
	  if(playerFrames.size()==0){
		  return true;
	  }else{
		  return false;
	  }    
  }
  // ͨ�����������Ҳ��Ŵ���
  PlayerFrame find(Player p) {
    for (int i = 0; i < playerFrames.size(); i++) {
      PlayerFrame pw = (PlayerFrame)playerFrames.elementAt(i);
      if (pw.player == p)
      {
        return pw;
      }
    }
    return null;
  }
  // ͨ���������������Ҳ��Ŵ���
  PlayerFrame find(ReceiveStream strm) {
    for (int i = 0; i < playerFrames.size(); i++) {
      PlayerFrame pw = (PlayerFrame)playerFrames.elementAt(i);
      if (pw.stream == strm){
        return pw;
      }
    }
    return null;
  }
  // ʵ��ReceiveStreamListener�ӿڵ�update����
  public synchronized void update(ReceiveStreamEvent event) {
		RTPManager rtpManager = (RTPManager) event.getSource();
		// ��÷�������Ϣ
		Participant sender = event.getParticipant();
		// ��ý��յ���Ϣ��
		ReceiveStream recStream = event.getReceiveStream();
		if (event instanceof NewReceiveStreamEvent) {
			// ������½��յ�������
			try {
				recStream = ((NewReceiveStreamEvent) event).getReceiveStream(); // �õ���������
				DataSource ds = recStream.getDataSource(); // �õ�����Դ
				RTPControl rtpCtrl = (RTPControl) ds
						.getControl("javax.media.rtp.RTPControl"); // �õ�RTP������
				if (rtpCtrl != null) {
					System.err.println("  -���յ��µ�RTP��: " + rtpCtrl.getFormat()); // �õ��������ݵĸ�ʽ
				} else {
					System.err.println("  -���յ��µ�RTP��");
				}
				if (sender == null) {
					System.err.println("  ������������Ҫ��һ������.");
				} else {
					System.err.println("  �µ�����������: " + sender.getCNAME());
				}
				// ͨ������Դ����һ��ý�岥����
				Player p = Manager.createPlayer(ds);
				if (p == null) {
					// ����ʧ���򷵻�
					return;
				}
				p.addControllerListener(this);
				p.realize();
				PlayerFrame pw = new PlayerFrame(p, recStream);
				playerFrames.addElement(pw);
				// ֪ͨinitialize()�����еĵȴ����̣��Ѿ����յ���һ����������
				synchronized (synObj) {
					isDataArrived = true;
					synObj.notifyAll();
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("������������" + e.getMessage());
				return;
			}
		} else if (event instanceof StreamMappedEvent) {
			if (recStream != null && recStream.getDataSource() != null) {
				DataSource ds = recStream.getDataSource();
				RTPControl rtpCtrl = (RTPControl) ds
						.getControl("javax.media.rtp.RTPControl");
				if (rtpCtrl != null) {
					System.err.println("      " + rtpCtrl.getFormat());
					System.err
							.println("     ��������ʶ�𣬷��ͷ���: " + sender.getCNAME());
				}
			}
		} else if (event instanceof ByeEvent) { // ���ݽ������
			System.err.println("  - �յ� \"bye\" from: " + sender.getCNAME());
			PlayerFrame playerWin = find(recStream);
			if (playerWin != null) {
				// �رղ��Ŵ���
				playerWin.close();
				playerFrames.removeElement(playerWin);
			}
		}
	}
   // ʵ��SessionListener�ӿ��е�update����
  public synchronized void update(SessionEvent evnet) {
    if (evnet instanceof NewParticipantEvent) {
      Participant pt = ((NewParticipantEvent)evnet).getParticipant();
      System.err.println("  - �µķ��ͷ�����: " + pt.getCNAME());
    }
  }
   // ʵ��ControllerListener�ӿڵ�controllerUpdate����
  public synchronized void controllerUpdate(ControllerEvent ctlEvent) {
		// �õ��¼�Դ
		Player player = (Player) ctlEvent.getSourceController();
		if (player == null) {
			return;
		}
		if (ctlEvent instanceof RealizeCompleteEvent) {
			// ����������Realize״̬
			PlayerFrame pw = find(player);
			if (pw == null) {
				System.err.println("Internal error!");
				return;
			}
			pw.initialize();
			pw.setVisible(true);
			player.start();
		}
		if (ctlEvent instanceof ControllerErrorEvent) {
			// �������������
			player.removeControllerListener(this);
			PlayerFrame pw = find(player);
			if (pw != null) {
				pw.close();
				playerFrames.removeElement(pw);
			}
			System.err.println("�ڲ�����: " + ctlEvent);
		}
	}
  public static void main(String args[]) {
		if (args. length == 0) {
			System.err.println("���������:<session> <session> ...");
			System.err.println("���磺127.0.0.1/100");
			System.exit(0);
		}
		MyReceiver myReceiver = new MyReceiver(args);
		if (!myReceiver.iniReceiver()) {
			System.out.println("��ʼ���Ựʧ�ܣ�");
			System.exit(-3);
		}
		try {
			while (!myReceiver.isFinished()) {
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("�������");
		}
		System.err.println("�˳�����");
	}
}
