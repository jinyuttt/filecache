/**    
 * 文件名：FileManager.java    
 *    
 * 版本信息：    
 * 日期：2017年8月12日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package FileCache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
/**    
 *     
 * 项目名称：FileCache    
 * 类名称：FileManager    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年8月12日 上午11:53:01    
 * 修改人：jinyu    
 * 修改时间：2017年8月12日 上午11:53:01    
 * 修改备注：    
 * @version     
 *     
 */
public class FileCacheManager<K,V> {
   private ConcurrentHashMap<K,V> cache=new ConcurrentHashMap<K,V>();
   private ConcurrentLinkedQueue<K> queue=new ConcurrentLinkedQueue<K>();
   private  byte valType=-1;//0 byte 1short 2 int 3 long 4 String 5 byte[]
   private byte keyType=-1;
   private int keylen=-1;
   private int vallen=-1;
   private volatile long sumLen=0;
   private int cacheSize=50*1024*1024;//100M  
   private volatile boolean isRuning=false;
   private long fileName=System.currentTimeMillis();
   private final long dataFileSize=1*1024*1024*1024;
   private final int curSize=10*1024*1024;//10M;
   private String dataDir="sessiondata";//文件目录，数据库
   FileWrite sw=new FileWrite();//写数据文件
   private volatile boolean  isInit=false;//是否初始化
   private boolean isClearOld=true;//清除旧数据
   private String indexFile="DBIndex.index";//全局索引，标记索引文件
   private long oldTime=10*60*1000;//缓存时间
   private long maxKVSize=Long.MAX_VALUE;//保存最大数据量
   private volatile boolean isRunDelete=false;//是否启动了文件删除
   private long skipLen=0;//跳过的索引文件,全局索引读取
   private long waitTime=30;//定时清理时间
   private String deleteFile="deleIndex.index";//需要删除的索引文件
   private String deleteKey="deleteKey.index";//删除的key
   private ArrayList<K> listkeys=new ArrayList<K>(20);//删除的key,临时保存
   private long deleteIndexFile=0;//记录跳过的索引文件
   private int deleteKeysSum=0;//计算删除过的key;
   private volatile boolean isupdate=false;//判断是否正在更新全局索引
   private volatile boolean isWrite=false;//正在写入全局文件
   private FileDataDelete fileDB=new FileDataDelete();//清除DB文件
   public  void setClearOld(boolean old)
   {
       isClearOld=old;
   }
   public void setCacheTime(long cacheTime)
   {
       oldTime=cacheTime;
   }
   public void setCacheNum(long size)
   {
       maxKVSize=size;
   }
   
   /*
    * 初始化
    */
   public void initDB()
   {
       if(isInit)
       {
           return;
       }
       File f=new File(dataDir);
       if(isClearOld) {
       deleteAllFilesOfDir(f);}
      //
       File dirdata=new File(dataDir);
       if(!dirdata.exists())
       {
           dirdata.mkdir();
       }
       fileDB.dir=dataDir;
       fileDB.fileDelete=deleteKey;
       fileDB.fileindex=deleteFile;
          
       isInit=true;
   }
   
   /*
    * 删除目录下全部文件
    */
  void deleteAllFilesOfDir(File path) {  
       if (!path.exists())  
           return;  
       if (path.isFile()) {  
           path.delete();  
           return;  
       }  
       File[] files = path.listFiles();  
       for (int i = 0; i < files.length; i++) {  
           deleteAllFilesOfDir(files[i]);  
       }  
       path.delete();  
   }  
   public void setDir(String dir)
   {
       dataDir=dir;
       fileDB.dir=dir;
   }
   
   /*
    * 启动清理数据
    */
   private void startDataDelete()
   {
       if(maxKVSize!=Long.MAX_VALUE&&oldTime<1)
       {
           //不清理数据
           return;
       }
       //
       if(isRunDelete)
       {
           return;
       }
       isRunDelete=true;
       //
       Thread filesClear=new Thread(new Runnable()
               {
           
           /*
            * 读取整个文件
            */
              public String readFile(File file)
                {
                // String encoding = "ISO-8859-1";  
                  Long filelength = file.length();  
                  byte[] filecontent = new byte[filelength.intValue()];  
                  try {  
                      FileInputStream in = new FileInputStream(file);  
                      in.read(filecontent);  
                      in.close();  
                  } catch (FileNotFoundException e) {  
                      e.printStackTrace();  
                  } catch (IOException e) {  
                      e.printStackTrace();  
                  }  
                  return new String(filecontent);  
                 
                }
              
              /*
               * 写入内容
               */
              public void writeFile(String content)
              {
                  try {
                    FileWriter fw=new FileWriter(dataDir+"/"+deleteFile,true);
                   fw.write(content);
                   fw.flush();
                   fw.close();
                  }
                  catch(IOException ex)
                  {
                      ex.printStackTrace();
                  }
                  
              }
              
              /*
               * 保存删除的key
               */
              public void writeKeys()
              {
                 
                  if(listkeys.size()==0)
                  {
                      return;
                  }
                  int curSize=listkeys.size();
                  deleteKeysSum+=curSize;//计算key,20000左右一个
                  StringBuffer buf=new StringBuffer();
                  for(int i=0;i<curSize;i++)
                  {
                      K key=listkeys.get(i);
                    
                      if(cache.remove(key)==null)
                      {
                          String str=convertToKey(key);
                          buf.append(str+",");
                      }
                      if((i+1)%11==0)
                      {
                          buf.append("\r\n");
                      }
                  }
                  if(buf.length()>0)
                  {
                      try
                      {
                        FileWriter fw=new FileWriter(dataDir+"/"+deleteKey,true);
                        fw.write(buf.toString());//最后一个是,
                        fw.flush();
                         fw.close();
                      }
                      catch(IOException ex)
                      {
                          ex.printStackTrace();
                      }
                  }
                  //删除curSize，每次从0开始
                  for(int i=0;i<curSize;i++)
                  {
                      listkeys.remove(0);
                  }
                  deleteIndexFile=deleteKeysSum/20000;
              }
              
              /*
               * 更新全局索引
               */
              private void clearFileIndex()
              {
                  if(deleteIndexFile>100000000)//大于10000w
                  {
                      FileWriter fw=null;
                      BufferedReader reader = null;
                      String tmpIndex=dataDir+"/"+indexFile+".tmp";
                      String indexfile=dataDir+"/"+indexFile;
                    try {
                        fw=new FileWriter(tmpIndex,true);
                        reader = new BufferedReader(new FileReader(indexfile));
                        String tempString = null;
                      
                        //一次读入一行，直到读入null为文件结束
                           while ((tempString = reader.readLine()) != null) {
                              if(tempString.isEmpty())
                              {
                                  continue;
                              }
                            String[] indexFile=tempString.split(",");
                            if(indexFile.length==2)
                            {
                                //
                                File f=new File(dataDir+"/"+indexFile[0]);
                                if(f.exists())
                                {
                                    //文件存在，此行写入临时文件
                                    fw.write(tempString);
                                }
                            }
                          }
                           isupdate=true;
                           while(isWrite)
                           {
                               try
                               {
                           
                              TimeUnit.MILLISECONDS.sleep(100);
                               }
                               catch(Exception ex)
                               {
                                   
                               }
                           }
                           //二次读取，可能刚刚有数据写入
                            while ((tempString = reader.readLine()) != null) {
                               if(tempString.isEmpty())
                               {
                                   continue;
                               }
                             String[] indexFile=tempString.split(",");
                             if(indexFile.length==2)
                             {
                                 //
                                 File f=new File(dataDir+"/"+indexFile[0]);
                                 if(f.exists())
                                 {
                                     //文件存在，此行写入临时文件
                                     fw.write(tempString);
                                 }
                             }
                            }
                             //
                             reader.close();
                             fw.close();
                             File gindextmp=new File(tmpIndex);
                             File gindex =new File(indexfile);
                             gindex.delete();
                             gindextmp.renameTo(gindex);
                             //
                             skipLen=0;
                             isupdate=false;
                             deleteIndexFile=0;
                    } catch (Exception e) {
                     
                        e.printStackTrace();
                    }
                    
                   
                  }
              }
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(waitTime);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    File dirf=new File(dataDir);
                    String[]files= dirf.list(new MyFileFilter(".csv"));
                    long num=0;
                    writeKeys();
                    clearFileIndex();
                   if(maxKVSize!=Long.MAX_VALUE)
                   {
                       //计算个数；
                       long needclear=(files.length*2000-maxKVSize)/20000;
                      
                       int lines=0;
                       if(needclear>0)
                       {
                           //需要删除的个数
                           try {
                               BufferedReader in
                               = new BufferedReader(new FileReader(dataDir+"/"+indexFile));
                                in.skip(skipLen);
                                String info="";
                                String lastInfo="";
                                while(true)
                                {
                                   info =in.readLine();
                                    if(info!=null)
                                  {
                                     lastInfo=info;
                                     String[] index=info.split(",");
                                     lines++;
                                     //索引文件；
                                     File f=new File(dataDir+"/"+index[0]);
                                   
                                   // 读取整个文件写入；
                                  if(f.exists())
                                  {
                                     String content=readFile(f);
                                     if(content!=null)
                                     writeFile(content);
                                     f.delete();
                                  }
                                 
                                     num++;
                                     if(lines==needclear)
                                     {
                                        break;
                                     }
                                  }
                                    else
                                    {
                                        break;
                                    }
                                    if(num%1000==0)
                                    {
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(500);
                                        } catch (InterruptedException e) {
                                           
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                //
                                if(lastInfo.length()==lastInfo.getBytes().length)
                                {
                                    skipLen+=lastInfo.length()*lines;
                                }
                                else
                                {
                                    skipLen+=lastInfo.length()*lines*2;
                                }
                                
                            } catch (IOException e) {
                               
                                e.printStackTrace();
                            }
                           deleteIndexFile+=needclear;//统计删除的索引文件个数
                       }
                   }
                   //个数
                  if(oldTime>0)
                  {
                      //按照时间
                     
                      int lines=0;
                      String lastInfo="";
                      long minTime=System.currentTimeMillis()-oldTime;
                      System.out.println("最新时间:"+minTime);
                      try
                      {
                       BufferedReader in= new BufferedReader(new FileReader(dataDir+"/"+indexFile));
                       in.skip(skipLen);
                       String info="";
                      while(true)
                      {
                        info =in.readLine();
                         if(info!=null)
                        {
                             if(info.isEmpty())
                             {
                                 continue;
                             }
                            lastInfo=info;
                           String[] index=info.split(",");
                           long curTime=0;
                           curTime=Long.valueOf(index[1]);
                           if(curTime<minTime)
                           {
                              
                              File f=new File(dataDir+"/"+index[0]);
                              // 读取整个文件写入；
                              if(f.exists())
                              {
                                 String content=readFile(f);
                                 if(content!=null)
                                 writeFile(content);
                                 f.delete();
                                 System.out.println("删除索引文件:"+index[0]);
                                 deleteIndexFile+=1;//统计删除的索引文件个数
                              }
                              lines++;
                           }
                           else
                           {
                               break;
                           }
                          
                        }
                         else
                         {
                             break;
                         }
                         num++;
                         if(num%1000==0)
                         {
                             try {
                                 TimeUnit.MILLISECONDS.sleep(500);
                             } catch (InterruptedException e) {
                                
                                 e.printStackTrace();
                             }
                         }
                      }
                      }
                      catch(Exception ex)
                      {
                          
                      }
                      //
                      if(lastInfo!=null)
                      {
                      if(lastInfo.length()==lastInfo.getBytes().length)
                      {
                          skipLen+=lastInfo.length()*lines;
                      }
                      else
                      {
                          skipLen+=lastInfo.length()*lines*2;
                      }
                      }
                  }
                  fileDB.start();
                  isRunDelete=false;
                }
           
               });
       filesClear.setDaemon(true);
       filesClear.setName("deleteFile");
       filesClear.start();
   }
   /*
    * 启动存储
    */
   private void startThread()
   {
       if(isRuning)
       {
           return;
       }
       isRuning=true;
       Thread file=new Thread(new Runnable() {
      
    private void updateFile(StringBuffer buf,String csvFileID)
       {
           String  csvFile=dataDir+"/"+csvFileID;
           FileWriter fw;
        try {
            fw = new FileWriter(csvFile,true);
            fw.write(buf.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
       }
     
 /*
  * 更新全局索引
  */
  private void updateIndexFile(String content)
  {
      try {
          while(isupdate)
          {
              
              try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
          }
            isWrite=true;
            long id=System.currentTimeMillis();
            FileWriter findex=new FileWriter(dataDir+"/"+indexFile,true);
            String data=content+","+id+System.getProperty("line.separator");;
            findex.write(data);
            findex.close();
            isWrite=false;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  
  /*
   * 更新数据索引   
   */
 private void updateIndex(ArrayList<FileIndex<K>> indexs)
       {
           StringBuffer buf=new StringBuffer();
           long curID=System.currentTimeMillis();
           String title="KEY,FILEID,POSITION,LEN,ID";
           buf.append(title+"\r\n");
           for(int i=0;i<indexs.size();i++)
           {
               FileIndex<K> index=indexs.get(i);
               String key=convertToKey(index.key);
               buf.append(key+",");
               buf.append(index.fileid+",");
               buf.append(index.position+",");
               buf.append(index.len+",");
               buf.append(curID+",");
               buf.append(System.getProperty("line.separator"));
           }
           //
          if(buf.length()>0)
          {
             updateFile(buf,curID+".csv");
             updateIndexFile(curID+".csv");
          }
          //
          for(int i=0;i<indexs.size();i++)
          {
              FileIndex<K> index=indexs.get(i);
              cache.remove(index.key);
          }
       }
        @Override
        public void run() {
            sw.path=dataDir+"/"+fileName+".DB";
            long fsize=sw.getFile();
          if(fsize>dataFileSize)
          {
              fileName=System.currentTimeMillis();
          }
          byte[] fcache=new byte[curSize];
          int copyindex=0;
          int  sumsize=0;
          long offset=0;
          int num=0;
           ArrayList<FileIndex<K>> list=new ArrayList<FileIndex<K>>();
           while(true)
           {
               if(queue.isEmpty())
               {
                   break;
               }
               //
               if(num>10000)
               {
                   updateIndex(list);
                   list.clear();
                   num=0;
                   try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                  
                    e.printStackTrace();
                }
                 
                   
               }
             K key=  queue.poll();
             num++;
             if(key==null)
             {
                 break;
             }
             V val=cache.get(key);
             if(val!=null)
             {
                 byte[] data=convertToValue(val);
                 FileIndex<K> index=new FileIndex<K>();
                 index.fileid=fileName+".DB";
                 index.key=key;
                 index.len=data.length;
                 index.position=offset+fsize;
                 //dataindex.put(key, index);
                 offset+=data.length;
                 list.add(index);
                 if(sumsize+data.length<curSize)
                 {
                     System.arraycopy(data, 0, fcache, copyindex, data.length);
                     copyindex+=data.length;
                     sumsize+=data.length;
                   
                 }
                 else
                 {
                     if(copyindex==0)
                     {
                         sw.writeFile(data);
                     }
                     else
                     {
                         sw.writeFile(fcache,sumsize);
                         sumsize=0;
                         copyindex=0;
                         System.arraycopy(data, 0, fcache, copyindex, data.length);
                         copyindex+=data.length;
                         sumsize+=data.length;
                     }
                     //
                   
                 }
             }
            
           }
           isRuning=false;
           startDataDelete();//添加后启动清理
        }
           
       });
       file.setDaemon(true);
       file.setName("cacheFile");
       file.start();
   }
   /*
    * 转换值
    */
   private byte[] convertToValue(V val)
   {
     //  0 byte 1short 2 int 3 long 4 String 5 byte[]
       byte[] data=new byte[vallen];
       ByteBuffer buf=ByteBuffer.wrap(data);
       switch(valType)
       {
       case 0:
           buf.put((byte) val);
           break;
       case 1:
           buf.putShort((short) val);
           break;
       case 2:
           buf.putInt((int) val);
       case 3:
           buf.putLong((long) val);
       case 4:
           data=val.toString().getBytes();
           break;
       case 5:
           data=(byte[])val;
           break;
       }
       return data;
       
   }
   
   /*
    * 转换KEY
    */
   private String convertToKey(K key)
   {
       String stringkey="";
       if(keyType==5)
       {
           stringkey=new String((byte[])key);
       }
       else
       {
           stringkey=String.valueOf(key);
       }
       return stringkey;
   }
  @SuppressWarnings("unchecked")
private V convertToValue(byte[]data)
  {
     
      ByteBuffer buf=ByteBuffer.wrap(data);
      V val=null;
      switch(valType)
      {
      case 0:
          val=(V)((Byte)buf.get());
          break;
      case 1:
          val=(V)((Short)buf.getShort());
          break;
      case 2:
          val=(V)((Integer)buf.getInt());
      case 3:
          val=(V)((Long)buf.getLong());
      case 4:
          val= (V) new String(data);
          break;
      case 5:
          val=(V) data;
          break;
      }
      return val;
  }
   private void sumLen(K key, V val)
   {
      switch(valType)
      {
      case 4:
          String tmp=val.toString();
          sumLen+=tmp.length()*2;
          break;
      case 5:
          byte[] ympbyte=(byte[])val;
          sumLen+=ympbyte.length;
          break;
          default:
              sumLen+=vallen;
              break;
      }
      //
      switch(keyType)
      {
      case 4:
          String tmp=key.toString();
          sumLen+=tmp.length()*2;
          break;
      case 5:
          byte[] ympbyte=(byte[])val;
          sumLen+=ympbyte.length;
          break;
          default:
              sumLen+=keylen;
              break;
      }
   }
   private void checkValType(V val)
   {
       if(valType!=-1)
       {
           return;
       }
       if(val instanceof Byte)
       {
           valType=0;
           vallen=1;
       }
       else if(val instanceof Short)
       {
           valType=1;
           vallen=2;
       }
       else if(val instanceof Integer)
       {
           valType=2;
           vallen=4;
       }
       else if(val instanceof Long)
       {
           valType=3;
           vallen=8;
       }
       else if(val instanceof String)
       {
           valType=4;
           vallen=0;
       }
       else if(val instanceof byte[])
       {
           valType=5;
           vallen=0;
       }
      
       
   }
   private void checkKeyType(K key)
   {
       if(keyType!=-1)
       {
           return;
       }
       if(key instanceof Byte)
       {
           keyType=0;
           keylen=1;
       }
       else if(key instanceof Short)
       {
           keyType=1;
           keylen=2;
       }
       else if(key instanceof Integer)
       {
           keyType=2;
           keylen=4;
       }
       else if(key instanceof Long)
       {
           keyType=3;
           keylen=8;
       }
       else if(key instanceof String)
       {
           keyType=4;
           keylen=0;
       }
       else if(key instanceof byte[])
       {
           keyType=5;
           keylen=0;
       }
      
       
   }
public void put(K key,V val)
{
    cache.put(key, val);
    checkValType(val);
    checkKeyType(key);
    sumLen(key,val);
    queue.add(key);
    if(sumLen>cacheSize)
    {
        sumLen=0;
        startThread();
    }
}

/*
 * 获取值
 */
public V get(K key)
{
   V val=  cache.get(key);
   if(val==null)
   {
    
      // String strKey=convertToKey(key);
       FileIndex<K> index= searchIndex(key);
       if(index!=null)
       {
           FileRead frd=new FileRead();
           frd.path=dataDir+"/"+index.fileid;
           byte[]data=  frd.readFile(index.position, index.len);
           val= convertToValue(data);
       }
            
            
   }
return val;
}

/*
 * 读取数据索引文件
 */
private String[] readDataIndex(String filePath)
{
    File file = new File(filePath);
    Long filelength = file.length(); // 获取文件长度
    byte[] filecontent = new byte[filelength.intValue()];
    try
    {
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);
        in.close();
    } catch (FileNotFoundException e)
    {
        e.printStackTrace();
    } catch (IOException e)
    {
        e.printStackTrace();
    }
    
    String[] fileContentArr = new String(filecontent).split("\r\n");
    
    return fileContentArr;// 返回文件内容,默认编码
}

/*
 * 检索key-value具体索引
 */
private FileIndex<K> searchQueue(Deque<String> queue,String strKey,Byte falge)
{
    FileIndex<K> index=null;
    String indexData = null;
    while(true)
    {
        if(index!=null)
        {
            break;
        }
        if(queue.isEmpty())
        {
            break;
        }
        indexData= queue.poll();
        if(indexData==null)
        {
            continue;
        }
      String[] fileinfo=indexData.split(",");
    if(fileinfo.length==2)
    {
        //
        String fileid=fileinfo[0];
        String time=fileinfo[1];//文件写入时间
        if(oldTime>0)
        {
            //判断时间
          if(System.currentTimeMillis()-Long.valueOf(time)>oldTime)
          {
              //后面的不用处理了;
              falge=1;
              return null;
          }
        }
     String dataIndex= dataDir+"/"+fileid;
     String[] datafileIndex=readDataIndex(dataIndex);
     for(int i=0;i<datafileIndex.length;i++)
     {
         if(datafileIndex[i].indexOf(strKey)!=-1)
         {
             //找到了;
               index = new FileIndex<K>();
              String[] datainfo=datafileIndex[i].split(",");
              index.fileid=datainfo[1];
              index.position=Long.valueOf(datainfo[2]);
              index.len=Integer.valueOf(datainfo[3]);
              break; 
             
         }
     }
     
    }
    //
    }
    return index;
}
/*
 * 检索数据索引
 * 1000个索引文件处理1次
 */
private FileIndex<K> searchIndex(K key)
{
    Deque<String> queue=new ArrayDeque <String>();
    FileIndex<K> index=null;
    String charset="utf-8";
    String strKey=this.convertToKey(key);
    //
   //读取索引文件
    RandomAccessFile rf = null;  
    String filename=dataDir+"/"+indexFile;
    int num=0;
    try {  
        rf = new RandomAccessFile(filename, "r");  
        long len = rf.length();  
        long start = rf.getFilePointer();  
        long nextend = start + len - 1;  
        String line; 
        rf.seek(nextend);  
        int c = -1;  
        int bytelen=-1;
        while (nextend > start) {
            c = rf.read();  
            if (c == '\n' || c == '\r') {  
                line = rf.readLine();  
                nextend--;  
                if(line!=null)
                {
                //
                    if(bytelen==-1)
                    {
                       if(line.length()==line.getBytes().length)
                       {
                           bytelen=1;
                       }
                       else
                       {
                           bytelen=2;
                       }
                    }
                queue.push(line);
                num++;
                if(num>1000)
                {
                    //读取1000行进行处理；
                    Byte flage=-1;
                    index= searchQueue(queue,strKey,flage);
                    if(index!=null)
                    {
                        return index;
                    }
                    else if(flage==1)
                    {
                        return null;
                    }
                }
                nextend=nextend-line.length()*bytelen+1;
                }
                //
               
            }  
            nextend--;  
            rf.seek(nextend);  
            if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行  
                System.out.println(new String(rf.readLine().getBytes(  
                        "ISO-8859-1"), charset));  
            }  
        }  
    } catch (IOException e) {  
        e.printStackTrace();  
    } finally {  
        try {  
            if (rf != null)  
                rf.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
    //
    if(index==null&&!queue.isEmpty())
    {
        Byte flage = -1;
        index= searchQueue(queue,strKey,flage);
    }
    return index;  
}
public void delete(K key)
{
    if(cache.remove(key)!=null)
    {
       listkeys.add(key);
       //数据可能还没有写入
    }
}
public void clear()
{
    queue.clear();
    cache.clear();
    //
    
    File f=new File(dataDir);
    deleteAllFilesOfDir(f);
    f.mkdir();
}
public void deleteByKeys(ArrayList<K> list)
{
    StringBuffer buf=new StringBuffer();
    
    for(int i=0;i<list.size();i++)
    {
        K key=list.get(i);
      
        if(cache.remove(key)==null)
        {
            String str=convertToKey(key);
            buf.append(str+",");
        }
        if((i+1)%11==0)
        {
            buf.append("\r\n");
        }
    }
    if(buf.length()>0)
    {
        try
        {
      FileWriter fw=new FileWriter(dataDir+"/"+deleteKey,true);
      fw.write(buf.toString());//最后一个是,
      fw.flush();
      fw.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
public long getLen()
{
    return sumLen;
}
}
