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

// MyReceiver类用于接收RTP数据
public class MyReceiver implements ReceiveStreamListener, SessionListener,ControllerListener {
  //存储rpt会话信息参数
  String rtpSessions[] = null; 
  //RTP管理器数组
  RTPManager rtpManagers[] = null;     
  //存放管理播放器窗口的容器
  Vector playerFrames = null;         
  //是否接收到数据的标志
  boolean isDataArrived = false;        
  //所有同步操作的同步对象
  Object synObj = new Object();          
  public MyReceiver(String rtpSessions[]) {
    this.rtpSessions = rtpSessions;
  }
  // 关闭播放器和会话管理器
  protected void shutDownAll() {
    // 关闭播放窗口
    for (int i = 0; i < playerFrames.size(); i++) {
      try {
        ((PlayerFrame)playerFrames.elementAt(i)).close();
      }
      catch (Exception e) {}
    }
   // 删除所有播放窗口
    playerFrames.removeAllElements();          
    // 关闭RTP会话管理器以及RTP会话
    for (int i = 0; i < rtpManagers. length; i++) {
      if (rtpManagers[i] != null) {
        rtpManagers[i].removeTargets( "关闭RTP会话");
        rtpManagers[i].dispose();                      
        rtpManagers[i] = null;
      }
    }
  }
  // 准备接收数据，初始化RTP会话
  protected boolean iniReceiver() {
		try {
			// 所有播放窗口放入容器统一管理
			playerFrames = new Vector();
			// 为每一个RTP会话建立一个管理器
			rtpManagers = new RTPManager[rtpSessions. length];
			SessionARP seLabel;
			for (int i = 0; i < rtpSessions. length; i++) {
				// 处理每一个RTP会话
				try {
					// 解析RTP会话地址
					seLabel = new SessionARP(rtpSessions[i]);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					System.out.println("不能解析: " + rtpSessions[i]);
					return false;
				}
				System.out.println("  - RTP 会话开启: 地址: " + seLabel.addr
						+ " 端口号: " + seLabel.port);
				// 为每一个RTP会话产生一个RTP管理器
				rtpManagers[i] = (RTPManager) RTPManager.newInstance();
				// 注册数据流监听器
				rtpManagers[i].addReceiveStreamListener(this);
				// 注册会话监听器
				rtpManagers[i].addSessionListener(this);
				// 获得发送方IP地址
				InetAddress ipAddr = InetAddress.getByName(seLabel.addr);
				// 本地地址信息
				SessionAddress localAddr = null;
				// 目的地址信息
				SessionAddress destAddr = null;
				if (ipAddr.isMulticastAddress()) {
					// 如果是组播，本地和目的地的IP地址相同
					localAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println("is MulticastAddress, localAddr = "
							+ localAddr.toString() + ", port=" + seLabel.port);
					destAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println("is MulticastAddress, destAddr = "
							+ destAddr.toString() + ", port =" + seLabel.port);
				} else {
					// 用本机IP地址和端口号构造源会话地址
					localAddr = new SessionAddress(InetAddress.getLocalHost(),
							seLabel.port);
					System.out.println(" localAddr = " + localAddr.toString()
							+ ", port=" + seLabel.port);
					// 用目的机（发送端）的IP地址和端口号构造目的会话地址
					destAddr = new SessionAddress(ipAddr, seLabel.port);
					System.out.println(" destAddr = " + destAddr.toString()
							+ ", port =" + seLabel.port);
				}
				// 使用本机会话地址初始化RTP管理器
				rtpManagers[i].initialize(localAddr);
				BufferControl bc = (BufferControl) rtpManagers[i]
						.getControl("javax.media.control.BufferControl");
				if (bc != null)
					// 设置设置缓冲区大小
					bc.setBufferLength(500);
				// 加入目的会话地址
				rtpManagers[i].addTarget(destAddr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("创建RTP会话失败: " + e.getMessage());
			return false;
		}
		// 直到数据结束
		long waitTime = System.currentTimeMillis();
		// 设置最长等待时间60秒
		long maxTime = 60000;
		try {
			synchronized (synObj) {
				while (!isDataArrived
						&& System.currentTimeMillis() - waitTime < maxTime) {
					// 等待设定的时间
					if (!isDataArrived)
						System.out.println("  - 等待RTP数据中...");
					synObj.wait(1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			shutDownAll();
			return false;
		}
		if (!isDataArrived) {
			// 如果数据没有到
			System.err.println("没有接收到RTP数据！");
			shutDownAll();
			return false;
		}
		return true;
	}
  // 如果数据传输完成则返回true，否则返回false
  public boolean isFinished() {
	  if(playerFrames.size()==0){
		  return true;
	  }else{
		  return false;
	  }    
  }
  // 通过播放器查找播放窗口
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
  // 通过接收数据流查找播放窗口
  PlayerFrame find(ReceiveStream strm) {
    for (int i = 0; i < playerFrames.size(); i++) {
      PlayerFrame pw = (PlayerFrame)playerFrames.elementAt(i);
      if (pw.stream == strm){
        return pw;
      }
    }
    return null;
  }
  // 实现ReceiveStreamListener接口的update方法
  public synchronized void update(ReceiveStreamEvent event) {
		RTPManager rtpManager = (RTPManager) event.getSource();
		// 获得发送者信息
		Participant sender = event.getParticipant();
		// 获得接收的信息流
		ReceiveStream recStream = event.getReceiveStream();
		if (event instanceof NewReceiveStreamEvent) {
			// 如果是新接收的数据流
			try {
				recStream = ((NewReceiveStreamEvent) event).getReceiveStream(); // 得到新数据流
				DataSource ds = recStream.getDataSource(); // 得到数据源
				RTPControl rtpCtrl = (RTPControl) ds
						.getControl("javax.media.rtp.RTPControl"); // 得到RTP控制器
				if (rtpCtrl != null) {
					System.err.println("  -接收到新的RTP流: " + rtpCtrl.getFormat()); // 得到接收数据的格式
				} else {
					System.err.println("  -接收到新的RTP流");
				}
				if (sender == null) {
					System.err.println("  发送数据流需要进一步解析.");
				} else {
					System.err.println("  新的数据流来自: " + sender.getCNAME());
				}
				// 通过数据源构造一个媒体播放器
				Player p = Manager.createPlayer(ds);
				if (p == null) {
					// 构造失败则返回
					return;
				}
				p.addControllerListener(this);
				p.realize();
				PlayerFrame pw = new PlayerFrame(p, recStream);
				playerFrames.addElement(pw);
				// 通知initialize()函数中的等待过程：已经接收到了一个新数据流
				synchronized (synObj) {
					isDataArrived = true;
					synObj.notifyAll();
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("新数据流错误：" + e.getMessage());
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
							.println("     数据流被识别，发送方是: " + sender.getCNAME());
				}
			}
		} else if (event instanceof ByeEvent) { // 数据接收完毕
			System.err.println("  - 收到 \"bye\" from: " + sender.getCNAME());
			PlayerFrame playerWin = find(recStream);
			if (playerWin != null) {
				// 关闭播放窗口
				playerWin.close();
				playerFrames.removeElement(playerWin);
			}
		}
	}
   // 实现SessionListener接口中的update方法
  public synchronized void update(SessionEvent evnet) {
    if (evnet instanceof NewParticipantEvent) {
      Participant pt = ((NewParticipantEvent)evnet).getParticipant();
      System.err.println("  - 新的发送方加入: " + pt.getCNAME());
    }
  }
   // 实现ControllerListener接口的controllerUpdate方法
  public synchronized void controllerUpdate(ControllerEvent ctlEvent) {
		// 得到事件源
		Player player = (Player) ctlEvent.getSourceController();
		if (player == null) {
			return;
		}
		if (ctlEvent instanceof RealizeCompleteEvent) {
			// 播放器处于Realize状态
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
			// 处理控制器错误
			player.removeControllerListener(this);
			PlayerFrame pw = find(player);
			if (pw != null) {
				pw.close();
				playerFrames.removeElement(pw);
			}
			System.err.println("内部错误: " + ctlEvent);
		}
	}
  public static void main(String args[]) {
		if (args. length == 0) {
			System.err.println("请输入参数:<session> <session> ...");
			System.err.println("比如：127.0.0.1/100");
			System.exit(0);
		}
		MyReceiver myReceiver = new MyReceiver(args);
		if (!myReceiver.iniReceiver()) {
			System.out.println("初始化会话失败！");
			System.exit(-3);
		}
		try {
			while (!myReceiver.isFinished()) {
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("程序出错！");
		}
		System.err.println("退出程序！");
	}
}
