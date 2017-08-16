/**    
 * �ļ�����FileDataDelete.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��8��15��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package FileCache;

import java.io.File;

/**    
 *     
 * ��Ŀ���ƣ�FileCache    
 * �����ƣ�FileDataDelete    
 * ����������������DB
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��8��15�� ����1:43:11    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��8��15�� ����1:43:11    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class FileDataDelete {
   public boolean isStart=false;
  // private boolean isRun=false;
   /**
    * ���ɾ���������ļ�
    */
   public String fileDelete;//ɾ����key
   public String fileindex;//ɾ����index
   public String globleIndex;
   private long maxSize=1000*1024*1024;//10M;
   private int  freeHours=24;
   public String dir="";
   
   /*
    * �������
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
       //�����ļ���С
       startThread();
       fs.delete();
   }
}

/*
 * ����DB�ļ�ɾ���߳�
 */
private void startThread()
{
   
    Thread removeFile=new Thread(new Runnable() {

        @Override
        public void run() {
          //�����ļ���С
            check();
          
        }
        
    });
    removeFile.setDaemon(true);
    removeFile.setName("�����ļ�");
    removeFile.start();
}

/*
 * ����ļ�����
 */
public void check()
{
    File dirf=new File(dir);
  String[] files=dirf.list(new MyFileFilter(".DB"));
    if(files.length>1)
    {
        //�����ҵ���С��һ��;
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
