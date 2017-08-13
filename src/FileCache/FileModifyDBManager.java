package FileCache;
/**    
 * 文件名：FileModifyManager.java    
 *    
 * 版本信息：    
 * 日期：2017年7月21日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */




import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：FileModifyManager    
 * 类描述：    修改内存表索引
 * 创建人：jinyu    
 * 创建时间：2017年7月21日 下午11:07:21    
 * 修改人：jinyu    
 * 修改时间：2017年7月21日 下午11:07:21    
 * 修改备注：    
 * @version     
 *     
 */
public class FileModifyDBManager {
    /**
     * 文件不允许修改暂时
     */
public static String fileName="";

public static boolean isCreate=true;
public static String dataDir="";

/*
 * 还没有更新到内存的索引数据量
 */
public static  HashMap<String, FileIndex<String>> hashindex=null;
public static int  indexSzie=0;
private static  volatile boolean isRuning=false;
private static FileModify filemodify=new FileModify();
private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
private static ConcurrentLinkedQueue<DataIndex> queue=new ConcurrentLinkedQueue<DataIndex>();
public static void addDataDeleteIndex(DataIndex index)
{
    queue.offer(index);
    indexSzie++;
    if(indexSzie>100)
    {
        indexSzie=0;
        start();
    }
    if(isCreate)
    {
        isCreate=false;
    String sql="create table indexFile(Id int ,Key varchar,FileID varchar, BIGINT osition, len int)";
    FileDBManager.getObj().exeSql(sql);
    }
}
public static void addFile(DataIndex index)
{
    filemodify.addFile(index);
    //
   
}
private static void start()
{
    if(queue.isEmpty())
    {
        return;
    }
    if(isRuning)
    {
        return;
    }
    isRuning=true;
    //
    cachedThreadPool.execute(new Runnable() {

        @Override
        public void run() {
            filemodify.dataDir=dataDir;
        StringBuffer buf=new StringBuffer();
         while(isRuning)
         {
            
             DataIndex tmp=queue.poll();
            buf.setLength(0);
             if(tmp==null)
             {
                 isRuning=false;
             }
             else
             {
                 //添加
                 if(tmp.flage==0)
                 {
                 buf.append("insert into indexFile(key,fileid,position,len) values(");
                 buf.append("'"+tmp.key+"'"+",");
                 buf.append("'"+tmp.fileid+"'"+",");
                 buf.append(tmp.position+",");
                 buf.append(tmp.len+",");
                 buf.append("'"+tmp.key+"'"+",");
                 FileDBManager.getObj().exeSql(buf.toString());
                 }
                 //修改
                 else if(tmp.flage==1)
                 {
                 buf.append("update indexFile set position=");
                 buf.append(tmp.position+",");
                 buf.append("len=");
                 buf.append(tmp.len);
                 buf.append("key='");
                 buf.append(tmp.len+"'");
                 buf.append(" where key='");
                 buf.append(tmp.key+"'");
                 FileDBManager.getObj().exeSql(buf.toString());
                 }
                 //删除
                 else if(tmp.flage==2)
                 {
                 buf.append("delete from indexFile where key='");
                 buf.append(tmp.key+"'");
                 FileDBManager.getObj().exeSql(buf.toString());
                 }
             }
             
         }
        }
        
    });
}
}
