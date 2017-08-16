/**    
 * 文件名：ComTools.java    
 *    
 * 版本信息：    
 * 日期：2017年8月15日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
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
 * 项目名称：FileCache    
 * 类名称：ComTools    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年8月15日 上午12:33:37    
 * 修改人：jinyu    
 * 修改时间：2017年8月15日 上午12:33:37    
 * 修改备注：    
 * @version     
 *     
 */
public class ComTools {
    
    /**
     * 获取进程ID
     */
public static String getProcessID()
{
    String name = ManagementFactory.getRuntimeMXBean().getName();  
    System.out.println(name);  
    String pid = name.split("@")[0]; 
    return pid;
}

/**
 * 获取文件服务时间
 */
public static long lastAcessTime(String file)
{
    try {
        Path p = Paths.get(file);
        BasicFileAttributes att = Files.readAttributes(p, BasicFileAttributes.class);//获取文件的属性
       // att.creationTime().toMillis();
      
        return  att.lastAccessTime().toMillis();
      
      //  att.lastModifiedTime().toMillis();
      
       } catch (Exception e1) {
      
        e1.printStackTrace();
      
       }
    return 0;
}
}
