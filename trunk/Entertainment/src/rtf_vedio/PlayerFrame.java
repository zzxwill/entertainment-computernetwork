package rtf_vedio;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Panel;
import javax.media.Player;
import javax.media.rtp.ReceiveStream;

class PlayerFrame extends Frame {
	//播放器
    Player player;
    //接收数据流对象
    ReceiveStream stream;       
    PlayerFrame(Player player, ReceiveStream strm) {
      this.player = player;
      this.stream = strm;
    }
    //  初始化
    public void initialize() {
      add(new PlayerPanel(player));
    }
   //  如果增加了组件，需要整窗口大小
    public void addNotify() {
      super.addNotify();
      pack();                        
    }
   //  关闭播放器
    public void close() {
      player.close();
      setVisible(false);
      dispose();
    }
    // 播放器组件类
    class PlayerPanel extends Panel {
      Component visualCom, ctrlCom;
      PlayerPanel(Player p) {
        setLayout(new BorderLayout());
        if ((visualCom = p.getVisualComponent()) != null){
          add("Center", visualCom);    
        }
        if ((ctrlCom = p.getControlPanelComponent()) != null){
          add("South", ctrlCom); 
        }
      }
    }
  }

