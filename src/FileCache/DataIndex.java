package FileCache;
/**    
 * 文件名：DataDeleteIndex.java    
 *    
 * 版本信息：    
 * 日期：2017年7月20日    
 * Copyright 足下 Corporation 2017     
 * 版权所有    
 *    
 */


/**    
 *     
 * 项目名称：PersistDB    
 * 类名称：DataIndex    
 * 类描述：    删除文件数据，重新整理
 * 该功能需要重新整理文件数据索引
 * 现在不实现，没有意思
 * 如果需要做成数据库样式，长时间使用，则需要实现
 * 重新规划索引
 * 创建人：jinyu    
 * 创建时间：2017年7月20日 上午12:22:17    
 * 修改人：jinyu    
 * 修改时间：2017年7月20日 上午12:22:17    
 * 修改备注：    
 * @version     
 *     
 */
public class DataIndex {
public String fileid;//文件
public long position;//索引位置
public int len;//长度
public byte flage=0;//0插入，1修改，2删除
public String key;
}
