package FileCache;
/**    
 * 文件名：FileRead.java    
 *    
 * 版本信息：    
 * 日期：2017年7月19日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
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
 * 项目名称：PersistDB    
 * 类名称：FileRead    
 * 类描述：    读取文件内容
 * 创建人：jinyu    
 * 创建时间：2017年7月19日 下午8:47:05    
 * 修改人：jinyu    
 * 修改时间：2017年7月19日 下午8:47:05    
 * 修改备注：    
 * @version     
 *     
 */
public class FileRead {
    String path="";
    private int blockSize=100*1024*102;//100M字节
    private long fileMax=2*1024*1024*1024;//2G字节
@SuppressWarnings("resource")
/*
 * 读取小文件
 * 文件位置
 * 读取长度
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
 * 读取大文件
 * 读取位置
 * 读取长度
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
 * 读取文件
 * 读取位置
 * 读取长度
 * 文件不存在或者设置的是目录则返回null
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
