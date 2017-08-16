/**    
 * �ļ�����MyFileFilter.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��8��14��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package FileCache;

import java.io.File;
import java.io.FilenameFilter;

/**    
 *     
 * ��Ŀ���ƣ�FileCache    
 * �����ƣ�MyFileFilter    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��8��14�� ����1:24:59    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��8��14�� ����1:24:59    
 * �޸ı�ע��    
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
//        // ��������ֵ
//        boolean flag = true;
//        // ����ɸѡ����
//        //endWith(String str);�ж��Ƿ�����ָ����ʽ��β��
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
