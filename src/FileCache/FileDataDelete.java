/**    
 * 文件名：FileDataDelete.java    
 *    
 * 版本信息：    
 * 日期：2017年8月15日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package FileCache;

import java.io.File;

/**    
 *     
 * 项目名称：FileCache    
 * 类名称：FileDataDelete    
 * 类描述：清理数据DB
 * 创建人：jinyu    
 * 创建时间：2017年8月15日 上午1:43:11    
 * 修改人：jinyu    
 * 修改时间：2017年8月15日 上午1:43:11    
 * 修改备注：    
 * @version     
 *     
 */
public class FileDataDelete {
   public boolean isStart=false;
  // private boolean isRun=false;
   /**
    * 存放删除的索引文件
    */
   public String fileDelete;//删除的key
   public String fileindex;//删除的index
   public String globleIndex;
   private long maxSize=1000*1024*1024;//10M;
   private int  freeHours=24;
   public String dir="";
   
   /*
    * 启动清除
    */
public void  start()
{
   File f=new File(dir+"/"+fileDelete);
   if(f.length()>maxSize)
   {
       startThread();
       f.delete();
   }
   File fs=new File(dir+"/"+fileindex);
   if(fs.length()>maxSize)
   {
       //索引文件大小
       startThread();
       fs.delete();
   }
}

/*
 * 启动DB文件删除线程
 */
private void startThread()
{
   
    Thread removeFile=new Thread(new Runnable() {

        @Override
        public void run() {
          //索引文件大小
            check();
          
        }
        
    });
    removeFile.setDaemon(true);
    removeFile.setName("清理文件");
    removeFile.start();
}

/*
 * 检查文件清理
 */
public void check()
{
    File dirf=new File(dir);
  String[] files=dirf.list(new MyFileFilter(".DB"));
    if(files.length>1)
    {
        //遍历找到最小的一个;
        long min=Long.MAX_VALUE;
        for(int i=0;i<files.length;i++)
        {
            String file=files[i];
            String id=file.substring(0, file.length()-3);
           long fileid=Long.valueOf(id);
           if(fileid<min)
           {
               min=fileid;
           }
        }
        //
        File minFile=new File(dir+"/"+min+".DB");
        if(minFile.exists())
        {
           long acessTime= ComTools.lastAcessTime(dir+"/"+min+".DB");
           if(System.currentTimeMillis()-acessTime>freeHours*60*60*1000)
           {
               minFile.delete();
           }
        }
    }
}
}
