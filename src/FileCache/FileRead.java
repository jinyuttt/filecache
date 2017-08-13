package FileCache;
/**    
 * �ļ�����FileRead.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��19��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**    
 *     
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�FileRead    
 * ��������    ��ȡ�ļ�����
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��19�� ����8:47:05    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��19�� ����8:47:05    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class FileRead {
    String path="";
    private int blockSize=100*1024*102;//100M�ֽ�
    private long fileMax=2*1024*1024*1024;//2G�ֽ�
@SuppressWarnings("resource")
/*
 * ��ȡС�ļ�
 * �ļ�λ��
 * ��ȡ����
 */
public byte[] readSmall(long position,int len)
{
    byte[] data=new byte[len];
    int bufSzie=len;
    File fin=new File(path);
    if(!fin.exists())
    {
        return null;
    }
    if(fin.isDirectory())
    {
        return null;
    }
    //
    FileChannel fcin=null;
    try {
        fcin=new RandomAccessFile(fin,"r").getChannel();
    } catch (FileNotFoundException e) {
     return null;
    }
    try {
        fcin.position(position);
    } catch (IOException e) {
     return null;
    }
    ByteBuffer rBuffer=ByteBuffer.allocate(bufSzie);
   try {
    fcin.read(rBuffer);
} catch (IOException e) {
  return null;
}
   rBuffer.rewind();
   rBuffer.get(data);
   rBuffer.clear();
   return data;
   
}
/*
 * ��ȡ���ļ�
 * ��ȡλ��
 * ��ȡ����
 */
@SuppressWarnings("resource")
public byte[] read(long position,int len)
{
    long finLen=0;
    byte[]data=new byte[len];
    File fin=new File(path);
    if(!fin.exists())
    {
        return null;
    }
    if(fin.isDirectory())
    {
        return null;
    }
    finLen=fin.length();
    MappedByteBuffer inputBuf=null;
    try {
        inputBuf=new RandomAccessFile(fin,"r").getChannel().map(FileChannel.MapMode.READ_ONLY, position,finLen);
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    inputBuf.get(data);
    inputBuf=null;
    System.gc();
    return data;
}

/*
 * ��ȡ�ļ�
 * ��ȡλ��
 * ��ȡ����
 * �ļ������ڻ������õ���Ŀ¼�򷵻�null
 */
public byte[] readFile(long position,int len)
{
    File f=new File(path);
    if(!f.exists())
    {
        return null;
    }
    if(f.isDirectory())
    {
        return null;
    }
    //
    if(f.length()<=fileMax)
    {
        return readSmall(position,len);
        
    }
    else
    {
        byte[] buf=new byte[len];
        int num=len/blockSize;
        int left=len%blockSize;
        int offset=0;
        for(int i=0;i<num;i++)
        {
            byte[]tmp=read(position+offset,blockSize);
            System.arraycopy(tmp, 0, buf, offset, blockSize);
            offset+=blockSize;
        }
        if(left>0)
        {
            byte[]tmp=read(position+offset,left);
            System.arraycopy(tmp, 0, buf, offset, left);
            offset+=left;
        }
        return buf;
    }
   
    
}
}
