package rtf;

import java.io.*;
import java.awt.Dimension;
import javax.media.protocol.DataSource;
import javax.media.format.*;
import javax.media.control.TrackControl;
import javax.media.rtp.*;
import java.net.InetAddress;
import javax.media.*;
import javax.media.protocol.*;

public class RTPSender {
  //����״̬������waitObj�Ͻ��еȴ����������Ӧ��ת��
  public Object waitObj = new Object();
  //�жϱ�־
  public boolean flag = false; 
  //���RTP�Ự������������
  private RTPManager managerRTP[];                
  //����˿ں�
  private int transPortNum;           
  //�������ݵĴ���������
  private Processor processor = null;        
  //Ŀ�Ķ˿ڵ�IP��ַ
  private String destinyIP;
  //��������������������
  private DataSource outputData = null;
  //ý�����ݵĴ��λ��
  private MediaLocator mediaLocator;    
  // ���캯��
  public RTPSender( Format format,String destinyIP,MediaLocator mediaLocator,String portNum) {
    Integer pn = new Integer(portNum);
    if (pn != null)
    {
      this.transPortNum = pn.intValue();
    }
    this.mediaLocator = mediaLocator;
    this.destinyIP = destinyIP;
  }
  // ��ʼ����
  // ���һ�ɹ��򷵻�true�����򷵻�false
  public synchronized boolean start() {
		boolean b = false;
		// ����һ��������
		b = getProcessor();
		if (b == false) {
			return b;
		}
		// ����RTP�Ự����Processor����������ݴ���Ŀ��IP��ַ��ָ���˿ں�
		b = createRTPSession();
		if (b == false) {
			processor.close();
			processor = null;
			return b;
		} else {
			// ��������
			processor.start();
			return true;
		}
	}
  // ͨ��ý��������Ϣ���촦����
  private boolean getProcessor() 
  {
		DataSource ds = null;
		if (mediaLocator == null) {
			System.out.println("mediaLocator����Ϊ��!");
			return false;
		}
		try {
			// Ϊ�����MediaLocator��λ��ʵ����һ���ʵ�������Դ��
			ds = Manager.createDataSource(mediaLocator);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("����DataSourceʧ�ܣ�");
			return false;
		}
		try {
			// ʹ������Դ������processor����
			processor = Manager.createProcessor(ds);
		} catch (NoProcessorException e1) {
			e1.printStackTrace();
			System.out.println("����processorʧ�ܣ�");
			return false;
		} catch (IOException e2) {
			e2.printStackTrace();
			System.out.println("����processorʧ�ܣ�");
			return false;
		}
		// ���û�����úý�һֱ�ȴ�
		boolean result = handleStateChange(processor, Processor.Configured);
		if (result == false) {
			System.out.println("����processorʧ�ܣ�");
			return false;
		}
		// Ϊý�����е�ÿһ���ŵ��õ�һ��������
		TrackControl[] allTracks = processor.getTrackControls();
		if (allTracks == null || allTracks.length < 1) {
			// ���û�й���򷵻�ʧ��
			System.out.println("�Ҳ���processor�еĹ����");
			return false;
		}
		// �������������������ΪRAW_RTP
		// ��ָ��ÿ���ŵ�֧�ֵĸ�ʽ��Ϊ�Ϸ���RTP��ʽ
		ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
		processor.setContentDescriptor(cd);
		// �������֧�ֵĸ�ʽ
		Format supFormats[];
		// ѡ�����֧�ֵĸ�ʽ
		Format selFormat = null;
		boolean hasOneTrack = false;
		for (int i = 0; i < allTracks.length; i++) {
			// ��ÿһ���ŵ���ѡ��һ��RTP֧�ֵĴ����ʽ
			Format format = allTracks[i].getFormat();
			if (allTracks[i].isEnabled()) {
				// ����дŵ����Դ���
				supFormats = allTracks[i].getSupportedFormats();
				if (supFormats.length > 0) {
					if (supFormats[0] instanceof VideoFormat) {
						// ����ֻ֧�����޼��ָ�ʽ��������Ҫ�����ת��
						selFormat = videoSizeCheck(allTracks[i].getFormat(),
								supFormats[0]);
					} else {
						// ǰ���Ѿ������������������ΪRIP������֧�ֵĸ�ʽ��������RTP��Ϲ���
						// ����ѡ���һ��֧�ֵĸ�ʽ
						selFormat = supFormats[0];
					}
					allTracks[i].setFormat(selFormat);
					System.err.println("Track " + i + " �����óɰ���:");
					System.err.println(" " + selFormat + "����!");
					hasOneTrack = true;
				} else
					allTracks[i].setEnabled(false);
			} else
				allTracks[i].setEnabled(false);
		}
		if (!hasOneTrack) {
			// ���û����Ч�ĸ�ʽ�򷵻�false
			System.out.println("�Ҳ�����Ч��RTP��ʽ��");
			return false;
		}
		// �ȴ����������Realized״̬
		result = handleStateChange(processor, Controller.Realized); // �ȴ�������ʵ��
		if (result == false) {
			System.out.println("����ʵ����processor��");
			return false;
		}
		outputData = processor.getDataOutput(); // �Ӵ������õ����������Դ
		return true;
	}
    // Ϊ��������ÿһ��ý��ŵ�����һ��RTP�Ự
  private boolean createRTPSession() {
    // �ڱ�����ʹ������������Դ
		PushBufferDataSource pbds = (PushBufferDataSource) outputData;
		// �õ�������������
		PushBufferStream pbss[] = pbds.getStreams();
		// Ϊÿ���ŵ�����һ��RTP�Ự������
		managerRTP = new RTPManager[pbss.length];
		for (int i = 0; i < pbss.length; i++) {
			try {
				managerRTP[i] = RTPManager.newInstance();
				int port = transPortNum + 2 * i;
				InetAddress ipAddr = InetAddress.getByName(destinyIP);
				System.out.println("loal port: " + port);
				SessionAddress localAddr = new SessionAddress(InetAddress
						.getLocalHost(), port);
				System.out.println("des port: " + (port));
				// ���ﴫ���ʹ�úͽ���Ŀ�Ķ���ͬ�Ķ˿ں�
				SessionAddress destAddr = new SessionAddress(ipAddr, port);
				// �������Ự��ַ����RTP������
				managerRTP[i].initialize(localAddr);
				// ����Ŀ�ĵص�IP��ַ
				managerRTP[i].addTarget(destAddr);
				System.err.println("Created RTP session: " + destinyIP + " "
						+ port);
				// ��������Դ��RTP������
				SendStream sendStream = managerRTP[i].createSendStream(
						outputData, i);
				// ����������
				sendStream.start();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
  }
  // ����JPEG��H.263�����׼��ֻ֧��һЩ�ض���ͼ���С������������б�Ҫ�ļ�飬��ȷ���������ȷ����
  Format videoSizeCheck(Format original, Format supported) {
    int width, height;
    Dimension size = ((VideoFormat)original).getSize();         // �õ���Ƶͼ��ĳߴ�
    Format jpegFmt = new Format(VideoFormat.JPEG_RTP);
    Format h263Fmt = new Format(VideoFormat.H263_RTP);

    if (supported.matches(jpegFmt)) { 
    // ��JPEG��ʽ����Ƶͼ��Ŀ�͸߱�����8���ص�������
      width = size.width % 8 == 0 ? size.width : ((int)(size.width / 8) * 8);
      height = size.height % 8 == 0 ? size.height : ((int)(size.height / 8) * 8);
    }
    else if (supported.matches(h263Fmt)) {     
    // H.263��ʽ��֧�������ض���ͼ��ߴ�
      if (size.width <= 128) {
          width = 128;
          height = 96;
      }
      else if (size.width <= 176) {
          width = 176;
          height = 144;
      }
      else {
          width = 352;
          height = 288;
      }
    }
    else {         
    	// ����������ʽ
      return supported;
    }
    //����������Ƶ��ʽ����
    return (new VideoFormat(null,new Dimension(width, height),Format.NOT_SPECIFIED,
                            null,Format.NOT_SPECIFIED)).intersects(supported);
  }
  // ��ֹ����
  public void stop() {
    synchronized (this) {
      if (processor != null) {
        processor.stop();
        processor.close();                          
        processor = null;                           
        for (int i = 0; i < managerRTP.length; i++) {  
          managerRTP[i].removeTargets( "Session ended.");
          managerRTP[i].dispose();
        }
      }
    }
  }
  // �õ��ȴ���
  Object getWaitObj() {
    return waitObj;
  }
  // ���ñ�־
  void markFail() {
    flag = true;
  }
  // �ȴ��������ﵽ��Ӧ��״̬
  private synchronized boolean handleStateChange(Processor p, int state) {
		// ������������״̬
		p.addControllerListener(new MyListener());
		flag = false;
		if (state == Processor.Configured) {
			// ������configured״̬������Processor
			p.configure();
		} else if (state == Processor.Realized) {
			// ʵ�ִ�����
			p.realize();
		}
		// һֱ�ȴ���ֱ���ɹ��ﵽ����״̬����ʧ��
		while (p.getState() < state && !flag) {
			synchronized (getWaitObj()) {
				try {
					getWaitObj().wait();
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		if (flag == true) {
			return false;
		} else {
			return true;
		}
	}
  // �����࣬��Ҫʵ��ControllerListener�ӿ�
  class MyListener implements ControllerListener {
    public void controllerUpdate(ControllerEvent e) {
      // ����ڴ��������û�ʵ�ֹ����г��ִ��������ر�
      if (e instanceof ControllerClosedEvent)
      {
        markFail();
      }
      // �������еĿ������¼���֪ͨ��handleStateChange�����еȴ����߳�
      if (e instanceof ControllerEvent) {
        synchronized (getWaitObj()) {
          getWaitObj().notifyAll();
        }
      }
    }
  }
}



