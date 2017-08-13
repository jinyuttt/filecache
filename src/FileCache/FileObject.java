package FileCache;
/**    
 * 文件名：FileObject.java    
 *    
 * 版本信息：    
 * 日期：2017年7月19日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：FileObject    
 * 类描述：    保存对象
 * 创建人：jinyu    
 * 创建时间：2017年7月19日 下午8:47:33    
 * 修改人：jinyu    
 * 修改时间：2017年7月19日 下午8:47:33    
 * 修改备注：    
 * @version     
 *     
 */
public class FileObject {
String path="";
@SuppressWarnings("resource")
public Object readObject()
{
    File f=new File(path);
    FileInputStream fread = null;
    try {
        fread = new FileInputStream(f);
    } catch (FileNotFoundException e1) {
      return null;
    }
    ObjectInputStream objInput=null;
    try {
          objInput=new ObjectInputStream(fread);
    } catch (IOException e) {
       
       return null;
    }
    try {
        return objInput.readObject();
    } catch (ClassNotFoundException e) {
        return null;
    } catch (IOException e) {
        return null;
    }
}
public void writeObject(Object obj)
{
    File f=new File(path);
    if(f.isDirectory())
    {
        return;
    }
    FileOutputStream foutput=null;
    try {
        foutput=new FileOutputStream(f);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    ObjectOutputStream objOutput=null;
    try {
        objOutput=new ObjectOutputStream(foutput);
        objOutput.writeObject(obj);
    } catch (IOException e) {
      
        e.printStackTrace();
    }
}
}
