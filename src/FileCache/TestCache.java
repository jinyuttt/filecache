/**    
 * 文件名：TestCache.java    
 *    
 * 版本信息：    
 * 日期：2017年8月12日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package FileCache;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**    
 *     
 * 项目名称：FileCache    
 * 类名称：TestCache    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年8月12日 下午10:34:51    
 * 修改人：jinyu    
 * 修改时间：2017年8月12日 下午10:34:51    
 * 修改备注：    
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
         String v="jinyu松开了手"+id;
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
