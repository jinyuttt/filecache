/**    
 * 文件名：MyFileFilter.java    
 *    
 * 版本信息：    
 * 日期：2017年8月14日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package FileCache;

import java.io.File;
import java.io.FilenameFilter;

/**    
 *     
 * 项目名称：FileCache    
 * 类名称：MyFileFilter    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年8月14日 上午1:24:59    
 * 修改人：jinyu    
 * 修改时间：2017年8月14日 上午1:24:59    
 * 修改备注：    
 * @version     
 *     
 */
public class MyFileFilter implements FilenameFilter {
    String[] files=null;
    
    public MyFileFilter(String filetype)
    {
        files=new String[] {filetype};
    }
    public MyFileFilter(String[] filetypes)
    {
        files=new String[filetypes.length];
        System.arraycopy(filetypes, 0, files, 0, filetypes.length);
    }
//    @Override
//    public boolean accept(File dir, String name) {
//        // TODO Auto-generated method stub
//        // 创建返回值
//        boolean flag = true;
//        // 定义筛选条件
//        //endWith(String str);判断是否是以指定格式结尾的
//        if (name.toLowerCase().endsWith(".jpg")) {
// 
//        } else if (name.toLowerCase().endsWith(".txt")) {
// 
//        } else if (name.toLowerCase().endsWith(".gif")) {
// 
//        } else {
//            flag = false;
//        }
//        return flag;
//    }
    @Override
    public boolean accept(File dir, String name) {
       String lowername=name.toLowerCase();
       boolean flag = false;
      for(int i=0;i<files.length;i++)
      {
          if(lowername.endsWith(files[i]))
          {
              flag=true;
              break;
          }
      }
      return flag;
    }

}
