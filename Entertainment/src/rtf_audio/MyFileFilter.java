package rtf_audio;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.filechooser.*;
import java.io.File;

public class MyFileFilter extends FileFilter {
	public Hashtable extTypes = new Hashtable();
	public String comment = null;
	public String allComment = null;
  public MyFileFilter() {   
  }
  public void putExtType(String exttype) {
	  allComment = null;
    if(extTypes == null) {
      extTypes = new Hashtable();                      
    }
    extTypes.put(exttype.toUpperCase(), this);      
  }
  // �õ��ļ�����չ��
  public String getExtension(File file) {
    if(file != null) {
      String fname = file.getName();
   // �õ��ļ����С�.����λ��
      int index = fname.lastIndexOf('.');                   
      if(index > 0 && index < fname.length() - 1) {
        return fname.substring(index+1).toUpperCase();
      }
    }
    return null;
  }
  
  public boolean accept(File file) {
    if(file != null) {
      if(file.isDirectory()) {          
    	  // Ŀ¼Ӧ����
        return true;
      }
      String extType = getExtension(file);
      Object obj = extTypes.get(getExtension(file));
      if(extType != null && obj != null) {    
    	// ��չ�������趨��Χ���ļ�Ӧ����
        return true;
      };
    }
    return false;
  }
  
  public void setComment(String comment) {
	allComment = null;
    this.comment = comment;    
  }  
 
  public String getDescription() {
    if(allComment == null) {
      if(comment==null){
    	  allComment = "(";
      }else{
    	  allComment = comment+"(";
      }
      Enumeration extensions = extTypes.keys();                          // �õ���ϣ���ȫ��������չ����
      if(extensions != null) {
    	 String extName = extensions.nextElement().toString();
    	 extName="*."+extName;
    	 allComment=allComment+extName;
    	     // ���ϵ�һ����չ��
        while (extensions.hasMoreElements()) {                
        	// ���Ϻ������չ��
          extName =extensions.nextElement().toString();
     	  extName=", *."+extName;
     	  allComment=allComment+extName;
        }
      }
      allComment += ")";
    }
    return allComment;
  }
}
