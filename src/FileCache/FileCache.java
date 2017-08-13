/**    
 * 文件名：FileCache.java    
 *    
 * 版本信息：    
 * 日期：2017年8月12日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */
package FileCache;

import java.util.ArrayList;

/**    
 *     
 * 项目名称：FileCache    
 * 类名称：FileCache    
 * 类描述：    
 * 创建人：jinyu    
 * 创建时间：2017年8月12日 下午9:44:24    
 * 修改人：jinyu    
 * 修改时间：2017年8月12日 下午9:44:24    
 * 修改备注：    
 * @version     
 *     
 */
public class FileCache<K,V> {
    FileManager<K,V> manager=new FileManager<K,V>();
    public void init(String  dir)
    {
        manager.setDir(dir);
        manager.initDB();
        
    }
public void addCache(K key,V val)
{
    manager.put(key, val);
}
public V get(K key)
{
    return manager.get(key);
}
public void deleteByKey(K key)
{
    manager.delete(key);
}
public void clear()
{
    manager.clear();
}
public void deleteArray(ArrayList<K> list)
{
    manager.deleteByKeys(list);
}
public long getLen()
{
    return manager.getLen();
}
}
