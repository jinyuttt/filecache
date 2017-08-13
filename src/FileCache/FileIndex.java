package FileCache;
/**    
 * 文件名：FileIndex.java    
 *    
 * 版本信息：    
 * 日期：2017年7月19日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */


import java.io.Serializable;

/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：FileIndex    
 * 类描述：    文件对应的数据索引
 * 创建人：jinyu    
 * 创建时间：2017年7月19日 下午9:28:45    
 * 修改人：jinyu    
 * 修改时间：2017年7月19日 下午9:28:45    
 * 修改备注：    
 * @version     
 *     
 */
public class FileIndex<K> implements Serializable  {
/**    
     * serialVersionUID:TODO（用一句话描述这个变量表示什么）    
     *    
     * @since Ver 1.1    
     */    
    
    private static final long serialVersionUID = 1L;
public K key;
public long position;
public int len;
public String fileid;
}
