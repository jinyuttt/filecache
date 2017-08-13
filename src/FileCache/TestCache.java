/**    
 * �ļ�����TestCache.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��8��12��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package FileCache;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**    
 *     
 * ��Ŀ���ƣ�FileCache    
 * �����ƣ�TestCache    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��8��12�� ����10:34:51    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��8��12�� ����10:34:51    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class TestCache {

   
    public static void main(String[] args) {
        FileCache<Long,String> cache=new FileCache<Long, String>();
        int num=0;
        Random rdom=new Random();
        long curid=0;
        cache.init("F:\\dbfile\\session");
      while(true)
      {
          num++;
         long id=  rdom.nextLong();
         String v="jinyu�ɿ�����"+id;
         cache.addCache(id, v);
        if(num>10000)
        {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            num=0;
            System.out.print(cache.get(curid)+"\r\n");
            System.out.print(cache.getLen()/1024/1024+"\r\n");
        }
        if(num==1)
        {
            curid=id;
        }
      }

    }

}
