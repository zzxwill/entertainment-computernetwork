package runtime;

import choice.EntertainmentStyle;
public class TestRuntime {
//	netChatFlag
	public static void main(String args[]){
		TestRuntime runtime=new TestRuntime();
		EntertainmentStyle style=new EntertainmentStyle();
		
		Process  p =null;
		
		if(style.netChatFlag==1){
			try
			{
//		       p =Runtime.getRuntime().exec("notepad.exe c:\\count.txt");
//		    	 p =Runtime.getRuntime().exec("C:\\Program Files\\Tencent\\QQDownload\\QQDownload.exe");
//		    	 p =Runtime.getRuntime().exec("classes/EntertainmentStyle.class");
				p =Runtime.getRuntime().exec("C:/Documents and Settings/周正喜/桌面/temp/ClientFrame.exe");
		       
		    	 //获得一个Runtime的实例对象并调用exec方法
				Thread.sleep(5000);
//		       p.destroy();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		if(style.vedioOnDemand==1){
			System.out.println("视频点播");
			try
			{
//				p =Runtime.getRuntime().exec("notepad.exe c:\\count.txt");
//				p =Runtime.getRuntime().exec("C:/Documents and Settings/周正喜/桌面/temp/ClientFrame.exe");
		       
		    	 //获得一个Runtime的实例对象并调用exec方法
				Thread.sleep(5000);
//		       p.destroy();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		

		

	}
}
