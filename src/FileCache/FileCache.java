/**    
 * �ļ�����FileCache.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��8��12��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */
package FileCache;

import java.util.ArrayList;

/**    
 *     
 * ��Ŀ���ƣ�FileCache    
 * �����ƣ�FileCache    
 * ��������    
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��8��12�� ����9:44:24    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��8��12�� ����9:44:24    
 * �޸ı�ע��    
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
