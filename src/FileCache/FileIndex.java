package FileCache;
/**    
 * �ļ�����FileIndex.java    
 *    
 * �汾��Ϣ��    
 * ���ڣ�2017��7��19��    
 * Copyright ���� Corporation 2017     
 * ��Ȩ����    
 *    
 */


import java.io.Serializable;

/**    
 *     
 * ��Ŀ���ƣ�PersistDB    
 * �����ƣ�FileIndex    
 * ��������    �ļ���Ӧ����������
 * �����ˣ�jinyu    
 * ����ʱ�䣺2017��7��19�� ����9:28:45    
 * �޸��ˣ�jinyu    
 * �޸�ʱ�䣺2017��7��19�� ����9:28:45    
 * �޸ı�ע��    
 * @version     
 *     
 */
public class FileIndex<K> implements Serializable  {
/**    
     * serialVersionUID:TODO����һ�仰�������������ʾʲô��    
     *    
     * @since Ver 1.1    
     */    
    
    private static final long serialVersionUID = 1L;
public K key;
public long position;
public int len;
public String fileid;
}
