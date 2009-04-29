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
  // 得到文件的扩展名
  public String getExtension(File file) {
    if(file != null) {
      String fname = file.getName();
   // 得到文件名中“.”的位置
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
    	  // 目录应接受
        return true;
      }
      String extType = getExtension(file);
      Object obj = extTypes.get(getExtension(file));
      if(extType != null && obj != null) {    
    	// 扩展名符合设定范围的文件应接受
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
      Enumeration extensions = extTypes.keys();                          // 得到哈希表的全部键（扩展名）
      if(extensions != null) {
    	 String extName = extensions.nextElement().toString();
    	 extName="*."+extName;
    	 allComment=allComment+extName;
    	     // 加上第一个扩展名
        while (extensions.hasMoreElements()) {                
        	// 加上后面的扩展名
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
