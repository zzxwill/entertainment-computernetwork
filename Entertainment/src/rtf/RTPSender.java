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
  //所有状态将会在waitObj上进行等待，以完成相应的转换
  public Object waitObj = new Object();
  //判断标志
  public boolean flag = false; 
  //存放RTP会话管理器的数组
  private RTPManager managerRTP[];                
  //传输端口号
  private int transPortNum;           
  //处理数据的处理器对象
  private Processor processor = null;        
  //目的端口的IP地址
  private String destinyIP;
  //经过处理器处理后的数据
  private DataSource outputData = null;
  //媒体数据的存放位置
  private MediaLocator mediaLocator;    
  // 构造函数
  public RTPSender( Format format,String destinyIP,MediaLocator mediaLocator,String portNum) {
    Integer pn = new Integer(portNum);
    if (pn != null)
    {
      this.transPortNum = pn.intValue();
    }
    this.mediaLocator = mediaLocator;
    this.destinyIP = destinyIP;
  }
  // 开始传输
  // 如果一成功则返回true，否则返回false
  public synchronized boolean start() {
		boolean b = false;
		// 创建一个处理器
		b = getProcessor();
		if (b == false) {
			return b;
		}
		// 创建RTP会话，把Processor处理过的数据传给目的IP地址的指定端口号
		b = createRTPSession();
		if (b == false) {
			processor.close();
			processor = null;
			return b;
		} else {
			// 启动传输
			processor.start();
			return true;
		}
	}
  // 通过媒体数据信息构造处理器
  private boolean getProcessor() 
  {
		DataSource ds = null;
		if (mediaLocator == null) {
			System.out.println("mediaLocator不能为空!");
			return false;
		}
		try {
			// 为定义的MediaLocator定位并实例化一个适当的数据源。
			ds = Manager.createDataSource(mediaLocator);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("创建DataSource失败！");
			return false;
		}
		try {
			// 使用数据源来构造processor对象
			processor = Manager.createProcessor(ds);
		} catch (NoProcessorException e1) {
			e1.printStackTrace();
			System.out.println("创建processor失败！");
			return false;
		} catch (IOException e2) {
			e2.printStackTrace();
			System.out.println("创建processor失败！");
			return false;
		}
		// 如果没有配置好将一直等待
		boolean result = handleStateChange(processor, Processor.Configured);
		if (result == false) {
			System.out.println("配置processor失败！");
			return false;
		}
		// 为媒体流中的每一个磁道得到一个控制器
		TrackControl[] allTracks = processor.getTrackControls();
		if (allTracks == null || allTracks.length < 1) {
			// 如果没有轨道则返回失败
			System.out.println("找不到processor中的轨道！");
			return false;
		}
		// 把输入的内容描述设置为RAW_RTP
		// 并指定每个磁道支持的格式仅为合法的RTP格式
		ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
		processor.setContentDescriptor(cd);
		// 存放所有支持的格式
		Format supFormats[];
		// 选择的所支持的格式
		Format selFormat = null;
		boolean hasOneTrack = false;
		for (int i = 0; i < allTracks.length; i++) {
			// 对每一个磁道，选择一种RTP支持的传输格式
			Format format = allTracks[i].getFormat();
			if (allTracks[i].isEnabled()) {
				// 如果有磁道可以处理
				supFormats = allTracks[i].getSupportedFormats();
				if (supFormats.length > 0) {
					if (supFormats[0] instanceof VideoFormat) {
						// 由于只支持有限几种格式，所以需要检验和转换
						selFormat = videoSizeCheck(allTracks[i].getFormat(),
								supFormats[0]);
					} else {
						// 前面已经设置了输出内容描述为RIP，这里支持的格式都可以与RTP配合工作
						// 这里选择第一种支持的格式
						selFormat = supFormats[0];
					}
					allTracks[i].setFormat(selFormat);
					System.err.println("Track " + i + " 被设置成按照:");
					System.err.println(" " + selFormat + "传输!");
					hasOneTrack = true;
				} else
					allTracks[i].setEnabled(false);
			} else
				allTracks[i].setEnabled(false);
		}
		if (!hasOneTrack) {
			// 如果没有有效的格式则返回false
			System.out.println("找不到有效的RTP格式！");
			return false;
		}
		// 等待处理器变成Realized状态
		result = handleStateChange(processor, Controller.Realized); // 等待处理器实现
		if (result == false) {
			System.out.println("不能实例化processor！");
			return false;
		}
		outputData = processor.getDataOutput(); // 从处理器得到输出的数据源
		return true;
	}
    // 为处理器的每一个媒体磁道产生一个RTP会话
  private boolean createRTPSession() {
    // 在本例中使用推类型数据源
		PushBufferDataSource pbds = (PushBufferDataSource) outputData;
		// 得到推类型数据流
		PushBufferStream pbss[] = pbds.getStreams();
		// 为每个磁道产生一个RTP会话管理器
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
				// 这里传输端使用和接收目的端相同的端口号
				SessionAddress destAddr = new SessionAddress(ipAddr, port);
				// 将本机会话地址传给RTP管理器
				managerRTP[i].initialize(localAddr);
				// 设置目的地的IP地址
				managerRTP[i].addTarget(destAddr);
				System.err.println("Created RTP session: " + destinyIP + " "
						+ port);
				// 产生数据源的RTP传输流
				SendStream sendStream = managerRTP[i].createSendStream(
						outputData, i);
				// 启动流传输
				sendStream.start();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
  }
  // 由于JPEG和H.263编码标准，只支持一些特定的图像大小，所以这里进行必要的检查，以确保其可以正确编码
  Format videoSizeCheck(Format original, Format supported) {
    int width, height;
    Dimension size = ((VideoFormat)original).getSize();         // 得到视频图像的尺寸
    Format jpegFmt = new Format(VideoFormat.JPEG_RTP);
    Format h263Fmt = new Format(VideoFormat.H263_RTP);

    if (supported.matches(jpegFmt)) { 
    // 对JPEG格式，视频图像的宽和高必须是8像素的整数倍
      width = size.width % 8 == 0 ? size.width : ((int)(size.width / 8) * 8);
      height = size.height % 8 == 0 ? size.height : ((int)(size.height / 8) * 8);
    }
    else if (supported.matches(h263Fmt)) {     
    // H.263格式仅支持三种特定的图像尺寸
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
    	// 忽略其他格式
      return supported;
    }
    //将处理后的视频格式返回
    return (new VideoFormat(null,new Dimension(width, height),Format.NOT_SPECIFIED,
                            null,Format.NOT_SPECIFIED)).intersects(supported);
  }
  // 终止传输
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
  // 得到等待锁
  Object getWaitObj() {
    return waitObj;
  }
  // 设置标志
  void markFail() {
    flag = true;
  }
  // 等待处理器达到相应的状态
  private synchronized boolean handleStateChange(Processor p, int state) {
		// 监听处理器的状态
		p.addControllerListener(new MyListener());
		flag = false;
		if (state == Processor.Configured) {
			// 当进入configured状态后，配置Processor
			p.configure();
		} else if (state == Processor.Realized) {
			// 实现处理器
			p.realize();
		}
		// 一直等待，直到成功达到所需状态，或失败
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
  // 监听类，需要实现ControllerListener接口
  class MyListener implements ControllerListener {
    public void controllerUpdate(ControllerEvent e) {
      // 如果在处理器配置或实现过程中出现错误，它将关闭
      if (e instanceof ControllerClosedEvent)
      {
        markFail();
      }
      // 对于所有的控制器事件，通知在handleStateChange方法中等待的线程
      if (e instanceof ControllerEvent) {
        synchronized (getWaitObj()) {
          getWaitObj().notifyAll();
        }
      }
    }
  }
}



