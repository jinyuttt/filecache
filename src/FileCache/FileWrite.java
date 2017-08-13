package FileCache;
/**    
 * �ļ�����FileWrite.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��19��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**    
 *     
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�FileWrite    
 * ��������    �ļ�д��
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��19�� ����8:47:17    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��19�� ����8:47:17    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class FileWrite {
    
    /*
     * �ļ�·��
     */
 String path="";
 /**
  * �Ƿ�׷���ļ�
  * Ĭ��׷��
  */
 boolean append=true;
 private long fileMax=2*1024*1024*1024;//2G�ֽ�
 
 /*
  * д��С�ļ�
  */
public long writeSmall(byte[]data)
{
    File fout=new File(path);
    FileOutputStream fs=null;
    long len=0;
    try {
        fs=new FileOutputStream(fout,append);
        BufferedOutputStream buffer=new BufferedOutputStream(fs);
        buffer.write(data);
        len=fs.getChannel().position();
        buffer.close();
        fs.close();
    } catch (IOException e) {
      
        e.printStackTrace();
    }
    return len;
}
/**
 * д����ļ�
 */
@SuppressWarnings("resource")
public long write(byte[]data)
{
    File fout=new File(path);
    if(fout.isDirectory())
    {
        return -1;
    }
    MappedByteBuffer outputBuf=null;
    try {
        outputBuf=new RandomAccessFile(fout,"rw").getChannel().map(FileChannel.MapMode.READ_WRITE, fout.length(),data.length);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    outputBuf.put(data);
  
    outputBuf.clear();
    outputBuf=null;
    System.gc();
    return fout.length()+data.length;
}

/**
 * д���ļ�
 */
public long writeFile(byte[]data)
{
    File f=new File(path);
    if(f.isDirectory())
    {
        return -1;
    }
    if(f.exists())
    {
       if(!append)
       {
          f.delete();
          try {
            f.createNewFile();
        } catch (IOException e) {
          
            e.printStackTrace();
        }
       }
    }
    if(f.length()<=fileMax)
    {
       return writeSmall(data);
    }
    else
    {
      return  write(data);
    }
}
/**
 * д���ļ�
 */
public long writeFile(byte[]data,int len)
{
    byte[] fdata=null;
    if(data==null||data.length==0)
    {
        return 0;
    }
    if(len==0||len==-1||data.length<len)
    {
        fdata=new byte[data.length];
        System.arraycopy(data, 0, fdata, 0, data.length);
    }
    else
    {
        fdata=new byte[len];
        System.arraycopy(data, 0, fdata, 0, len);
    }
    File f=new File(path);
    if(f.isDirectory())
    {
        return -1;
    }
    if(f.exists())
    {
       if(!append)
       {
          f.delete();
          try {
            f.createNewFile();
        } catch (IOException e) {
          
            e.printStackTrace();
        }
       }
    }
    if(f.length()<=fileMax)
    {
       return writeSmall(fdata);
    }
    else
    {
      return  write(fdata);
    }
}

/**
 * ���ص�ǰ�ļ���С
 * 
 */
public long getFile()
{
    File f=new File(path);
    if(f.isDirectory())
    {
        return -1;
    }
    if(f.exists())
    {
        return f.length();
    }
    else
    {
        return 0;
    }
}
}
