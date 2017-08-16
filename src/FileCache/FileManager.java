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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
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
public class FileManager<K,V> {
   private ConcurrentHashMap<K,V> cache=new ConcurrentHashMap<K,V>();
   private ConcurrentLinkedQueue<K> queue=new ConcurrentLinkedQueue<K>();
   //private ConcurrentHashMap<K,FileIndex> dataindex=new ConcurrentHashMap<K,FileIndex>();
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
   private String dataDir="sessiondata";
   FileWrite sw=new FileWrite();
   private volatile boolean  isInit=false;
   private boolean isClearOld=true;
   private String indexFile="DBIndex.index";
   private long oldTime=10*60*1000;//缓存时间
  // private long maxKVSize=Long.MAX_VALUE;
   
   public FileManager()
   {
      
       
   }
   public void initDB()
   {
       if(isInit)
       {
           return;
       }
       File f=new File(dataDir);
       if(isClearOld) {
       deleteAllFilesOfDir(f);}
       f.mkdir();
       isInit=true;
//       String sql="create table IF NOT EXISTS indexFile(Key varchar,FileID varchar,position BIGINT , len int,ID BIGINT,TMPFILE varchar)";
//       //创建索引表
//       FileDBManager.setDir(dataDir);
//       FileDBManager obj=  FileDBManager.getObj();
//       obj.exeSql(sql);
//        String  csql=sql.replaceFirst("indexFile", "globleIndex");
//       obj.exeSql(csql);
//       csql=sql.replaceFirst("indexFile", "globleDelete");
//       obj.exeSql(csql);
//       csql=sql.replaceFirst("indexFile", "globleTmp");
//       obj.exeSql(csql);
//        String tmpsql="create table globleKeys(Key varchar)";
//       obj.exeSql(tmpsql);
////       sql="CREATE INDEX IF NOT EXISTS globle_KEY ON globleIndex(Key)";
////       obj.exeSql(sql);
////       sql="CREATE INDEX IF NOT EXISTS globle_ID ON globleIndex(ID)";
////       obj.exeSql(sql);
//       if(isClearOld)
//       {
//           sql="TRUNCATE TABLE globleIndex";
//           obj.exeSql(sql);
//       }
   }
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
     
   }
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
     
//  private String updateFile(StringBuffer buf)
//       {
//           String  csvFile=dataDir+"/"+System.currentTimeMillis()+".csv";
//           FileWriter fw;
//        try {
//            fw = new FileWriter(csvFile,true);
//            fw.write(buf.toString());
//            fw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return csvFile;
     //  }
  private void updateIndexFile(String file)
  {
      try {
          
            long id=System.currentTimeMillis();
            FileWriter findex=new FileWriter(dataDir+"/"+indexFile,true);
            String data=file+","+id+System.getProperty("line.separator");;
            findex.write(data);
            findex.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
//  private void updateIndex1(ArrayList<FileIndex<K>> indexs)
//       {
//           FileDBManager obj=  FileDBManager.getObj();
//           StringBuffer buf=new StringBuffer();
//           for(int i=0;i<indexs.size();i++)
//           {
//               FileIndex<K> index=indexs.get(i);
//               String key=convertToKey(index.key);
//               buf.append("('"+key+"','"+index.fileid+"',"+index.position+","+index.len+"),");
//           }
//           //
//          if(buf.length()>0)
//          {
//            String data=buf.toString();
//            data=data.substring(0, data.length()-1);
//            String sql="insert into globleTmp(key,fileid,position,len) VALUES"+data;
//            obj.exeSql(sql);
//            //
//            sql="select  table.* from globleIndex right join  globleTmp on globleTmp.key=globleIndex.key";
//            String tmpsql=sql.replaceFirst("table", "globleIndex");
//            //CALL CSVWRITE('d:/test.csv', 'SELECT * FROM t');
//            String deleteSql="insert into globleDelete "+tmpsql+" where globleIndex.key is not null";
//            String  csvFile=dataDir+"/"+System.currentTimeMillis()+".csv";
//            deleteSql="CALL CSVWRITE('"+csvFile+"'"+", '"+tmpsql+" where globleIndex.key is not null"+"')";
//            obj.exeSql(deleteSql);
//            //INSERT INTO TEST ( SELECT  * FROM  CSVREAD('d:/test.csv ')) ;
//            obj.exeSql("insert into globleDelete (SELECT  * FROM  CSVREAD('"+csvFile+"'"+"))");
//            tmpsql=sql.replaceFirst("table", "globleTmp");
//            String sqlInsert="insert into globleIndex "+tmpsql+" where globleIndex.key is null";
//            sqlInsert="CALL CSVWRITE('"+csvFile+"'"+", '"+tmpsql+" where globleIndex.key is  null"+"')";
//            obj.exeSql(sqlInsert);
//            obj.exeSql("insert into globleIndex (SELECT  * FROM  CSVREAD('"+csvFile+"'"+"))");
//            //
//            sql="TRUNCATE TABLE globleTmp";
//            obj.exeSql(sql);
//        
//          }
//          //
//          for(int i=0;i<indexs.size();i++)
//          {
//              FileIndex<K> index=indexs.get(i);
//             // dataindex.remove(index.key);
//              cache.remove(index.key);
//          }
//       }
//      
// private void updateIndex2(ArrayList<FileIndex<K>> indexs)
//       {
//           FileDBManager obj=  FileDBManager.getObj();
//           StringBuffer buf=new StringBuffer();
//           long curID=System.currentTimeMillis();
//           String title="\"KEY\",\"FILEID\",\"POSITION\",\"LEN\",\"ID\",\"TMPFILE\"";
//           buf.append(title+"\r\n");
//           String csvFileID=System.currentTimeMillis()+".csv";
//           for(int i=0;i<indexs.size();i++)
//           {
//               FileIndex<K> index=indexs.get(i);
//               String key=convertToKey(index.key);
//               buf.append("\""+key+"\",");
//               buf.append("\""+index.fileid+"\",");
//               buf.append("\""+index.position+"\",");
//               buf.append("\""+index.len+"\",");
//               buf.append("\""+curID+"\",");
//               buf.append("\""+csvFileID+"\"");
//               buf.append("\r\n");
//              
//           }
//           //
//          if(buf.length()>0)
//          {
//          // String csvFile=updateFile(buf);
//           updateFile(buf,csvFileID);
//           String  csvFile=dataDir+"/"+csvFileID;
//           obj.exeSql("insert into globleIndex(SELECT  * FROM  CSVREAD('"+csvFile+"'"+"))");
//           //
//         //  File f=new File(csvFile);
//        //   f.deleteOnExit();
//          }
//          //
//          for(int i=0;i<indexs.size();i++)
//          {
//              FileIndex<K> index=indexs.get(i);
//             // dataindex.remove(index.key);
//              cache.remove(index.key);
//          }
//       }
     
 private void updateIndex(ArrayList<FileIndex<K>> indexs)
       {
          // FileDBManager obj=  FileDBManager.getObj();
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
             // dataindex.remove(index.key);
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
        }
           
       });
       file.setDaemon(true);
       file.setName("cacheFile");
       file.start();
   }
   public byte[] convertToValue(V val)
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
public V get1(K key)
{
   V val=  cache.get(key);
   if(val==null)
   {
       FileDBManager obj=  FileDBManager.getObj();
       String strKey=convertToKey(key);
       String sql="select * from globleIndex where key='"+strKey+"'";
       ResultSet r=  obj.exeSelect(sql);
            FileIndex<K> index=new FileIndex<K>();
            try
            {
            index.fileid=r.getNString("fileid");
            index.len=r.getInt("len");
            index.position=r.getLong("position");
            FileRead rd=new FileRead();
            rd.path=dataDir+"/"+index.fileid;
            byte[] data=  rd.readFile(index.position, index.len);
            val=  convertToValue(data);
            }
            catch(Exception ex)
            {
                
            }
            
   }
return val;
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
    cache.remove(key);
    FileDBManager obj=  FileDBManager.getObj();
    String strKey=convertToKey(key);
    String sql="delete * from globleIndex where key='"+strKey+"'";
    obj.exeSql(sql);
    sql="inser into globleDelete select * from globleIndex where key='"+strKey+"'";
    obj.exeSql(sql);
}
public void clear()
{
    queue.clear();
    cache.clear();
    FileDBManager obj=  FileDBManager.getObj();
    String sql="TRUNCATE TABLE globleIndex";
    obj.exeSql(sql);
    sql="TRUNCATE TABLE globleDelete";
    obj.exeSql(sql);
    sql="TRUNCATE TABLE globleTmp";
    obj.exeSql(sql);
    //
    File f=new File(dataDir);
    f.deleteOnExit();
}
public void deleteByKeys(ArrayList<K> list)
{
    StringBuffer buf=new StringBuffer();
    for(int i=0;i<list.size();i++)
    {
        K key=list.get(i);
        String str=convertToKey(key);
        buf.append("('"+str+"'),");
        queue.remove(key);
    }
    if(buf.length()>0)
    {
        FileDBManager obj=  FileDBManager.getObj();
        String data=buf.toString();
        data=data.substring(0, data.length()-1);
         String  sql="TRUNCATE TABLE globleKeys";
         obj.exeSql(sql);
         sql="inser into globleKeys values "+data;
         obj.exeSql(sql);
        String  csql="select from globleIndex,globleKeys  where globleIndex.key=globleKeys.key";
         sql="insert into globleDelete "+csql;
         obj.exeSql(sql);
         //delete from globleIndex where exists (select 1 from 表2 where 表1.id=表2.id and 表1.name=表2.name);
         sql="delete from globleIndex where exists ("+csql+")";
         sql="delete globleIndex from globleIndex,globleKeys where globleIndex.key=globleKeys.key";
        obj.exeSql(sql);
    }
}
public long getLen()
{
    return sumLen;
}
}
