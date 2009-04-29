package rtf_vedio;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Panel;
import javax.media.Player;
import javax.media.rtp.ReceiveStream;

class PlayerFrame extends Frame {
	//������
    Player player;
    //��������������
    ReceiveStream stream;       
    PlayerFrame(Player player, ReceiveStream strm) {
      this.player = player;
      this.stream = strm;
    }
    //  ��ʼ��
    public void initialize() {
      add(new PlayerPanel(player));
    }
   //  ����������������Ҫ�����ڴ�С
    public void addNotify() {
      super.addNotify();
      pack();                        
    }
   //  �رղ�����
    public void close() {
      player.close();
      setVisible(false);
      dispose();
    }
    // �����������
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

