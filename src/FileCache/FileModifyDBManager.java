package FileCache;
/**    
 * �ļ�����FileModifyManager.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��21��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */




import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**    
 *     
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�FileModifyManager    
 * ��������    �޸��ڴ������
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��21�� ����11:07:21    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��21�� ����11:07:21    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class FileModifyDBManager {
    /**
     * �ļ��������޸���ʱ
     */
public static String fileName="";

public static boolean isCreate=true;
public static String dataDir="";

/*
 * ��û�и��µ��ڴ������������
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
                 //���
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
                 //�޸�
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
                 //ɾ��
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
