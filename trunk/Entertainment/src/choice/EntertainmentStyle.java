package choice;

import java.applet.Applet;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
/*
 * ����Java GUI
 */
public class EntertainmentStyle extends Applet implements ItemListener {
	/**
	 * �������ı�����ı�־
	 */
	public int netChatFlag=0;
	/*
	 * ��������Ƶ�㲥�ı�־
	 */
	public int vedioOnDemand=0;
	
	private static final long serialVersionUID = 1L;
	String entertainmentStyle[]={"�ı�����","��Ƶ�㲥","��Ƶ�㲥"};
	
	/*
	 * ��ʼ��ͼ�����ô�С����ť��
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
	/*����״̬�仯
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
//				System.out.println("��ѡ���ˣ�"+entertainmentStyle[i]);
//			}
//		}
		if(temp.getLabel()==entertainmentStyle[0]){
			System.out.println("��ѡ���ˣ�"+entertainmentStyle[0]);
			netChatFlag=1;
			try
			{
				p =Runtime.getRuntime().exec("C:/Documents and Settings/����ϲ/����/temp/ClientFrame.exe");
		       
		    	 //���һ��Runtime��ʵ�����󲢵���exec����
				Thread.sleep(5000);
//		       p.destroy();
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
		}else if(temp.getLabel()==entertainmentStyle[1]){
			System.out.println("��ѡ���ˣ�"+entertainmentStyle[1]);
			vedioOnDemand=1;
			try
			{
				p =Runtime.getRuntime().exec("C:/Documents and Settings/����ϲ/����/temp/ClientFrame.exe");
		       
		    	 //���һ��Runtime��ʵ�����󲢵���exec����
				Thread.sleep(5000);
//		       p.destroy();
			} catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}
		
		
			
		}
	}


