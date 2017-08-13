package FileCache;
/**    
 * 文件名：FileIndexManager.java    
 *    
 * 版本信息：    
 * 日期：2017年7月22日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：FileIndexManager    
 * 类描述：    管理内存索引
 * 用于更新
 * 创建人：jinyu    
 * 创建时间：2017年7月22日 上午1:10:02    
 * 修改人：jinyu    
 * 修改时间：2017年7月22日 上午1:10:02    
 * 修改备注：    
 * @version     
 *     
 */
public class FileDBManager {
    private static FileDBManager instance=null;
    private static String Dir="";
    public  static void setDir(String dir)
    {
        Dir=dir;
    }
    private  Connection conn=null;
    private FileDBManager()
    {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e1) {
         
            e1.printStackTrace();
        }
         try {
           // conn = DriverManager.
                 //   getConnection("jdbc:h2:tcp://localhost/mem:indextable", "sa", "");
             String url="jdbc:h2:"+Dir+";AUTO_SERVER=TRUE;LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0";
             conn = DriverManager.
            getConnection(url, "sa", "");
        } catch (SQLException e) {
           
            e.printStackTrace();
        }
    }
    public static FileDBManager getObj()
    {
        if(instance==null)
        {
            instance=new FileDBManager();
        }
        return instance;
    }
    public boolean exeSql(String sql)
    {
        boolean r=true;
     Statement stmt = null;
    try {
        stmt = conn.createStatement();
    } catch (SQLException e) {
        r=false;
        e.printStackTrace();
    }
     try {
        stmt.executeUpdate(sql);
    } catch (SQLException e) {
       r=false;
        e.printStackTrace();
    }
    
     return r;
    }
    public ResultSet exeSelect(String sql)
    {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
           
            e.printStackTrace();
        }
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
           
            e.printStackTrace();
        }   
        return rs;
    }
}
