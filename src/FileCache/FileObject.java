package FileCache;
/**    
 * �ļ�����FileObject.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��19��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
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
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�FileObject    
 * ��������    �������
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��19�� ����8:47:33    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��19�� ����8:47:33    
 * �޸ı�ע��    
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
