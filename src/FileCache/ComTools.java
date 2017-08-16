/**    
 * �ļ�����ComTools.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��8��15��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package FileCache;

import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**    
 *     
 * ��Ŀ���ƣ�FileCache    
 * �����ƣ�ComTools    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��8��15�� ����12:33:37    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��8��15�� ����12:33:37    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class ComTools {
    
    /**
     * ��ȡ����ID
     */
public static String getProcessID()
{
    String name = ManagementFactory.getRuntimeMXBean().getName();  
    System.out.println(name);  
    String pid = name.split("@")[0]; 
    return pid;
}

/**
 * ��ȡ�ļ�����ʱ��
 */
public static long lastAcessTime(String file)
{
    try {
        Path p = Paths.get(file);
        BasicFileAttributes att = Files.readAttributes(p, BasicFileAttributes.class);//��ȡ�ļ�������
       // att.creationTime().toMillis();
      
        return  att.lastAccessTime().toMillis();
      
      //  att.lastModifiedTime().toMillis();
      
       } catch (Exception e1) {
      
        e1.printStackTrace();
      
       }
    return 0;
}
}
