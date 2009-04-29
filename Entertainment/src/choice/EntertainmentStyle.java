package choice;

import java.applet.Applet;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
/*
 * 回忆Java GUI
 */
public class EntertainmentStyle extends Applet implements ItemListener {
	/**
	 * 下面是文本聊天的标志
	 */
	public int netChatFlag=0;
	/*
	 * 下面是视频点播的标志
	 */
	public int vedioOnDemand=0;
	
	private static final long serialVersionUID = 1L;
	String entertainmentStyle[]={"文本聊天","视频点播","音频点播"};
	
	/*
	 * 初始化图像，设置大小，按钮等
	 * @see java.applet.Applet#init()
	 */
	public void init(){
		setSize(400,30);
		CheckboxGroup style=new CheckboxGroup();
		for(int i=0;i<entertainmentStyle.length;i++){
			Checkbox one=new Checkbox(entertainmentStyle[i],false,style);
			one.addItemListener(this);
			add(one);
		}	
	}
	/*监听状态变化
	 * 
	 * (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		Process  p =null;
		
		// TODO Auto-generated method stub
		Checkbox temp=(Checkbox) e.getItemSelectable();
//		for(int i=0;i<entertainmentStyle.length;i++){
//			if(temp.getLabel()==entertainmentStyle[i]){
//				System.out.println("您选择了："+entertainmentStyle[i]);
//			}
//		}
		if(temp.getLabel()==entertainmentStyle[0]){
			System.out.println("您选择了："+entertainmentStyle[0]);
			netChatFlag=1;
			try
			{
				p =Runtime.getRuntime().exec("C:/Documents and Settings/周正喜/桌面/temp/ClientFrame.exe");
		       
		    	 //获得一个Runtime的实例对象并调用exec方法
				Thread.sleep(5000);
//		       p.destroy();
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}else if(temp.getLabel()==entertainmentStyle[1]){
			System.out.println("您选择了："+entertainmentStyle[1]);
			vedioOnDemand=1;
			try
			{
				p =Runtime.getRuntime().exec("C:/Documents and Settings/周正喜/桌面/temp/ClientFrame.exe");
		       
		    	 //获得一个Runtime的实例对象并调用exec方法
				Thread.sleep(5000);
//		       p.destroy();
			} catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}
		
		
			
		}
	}


