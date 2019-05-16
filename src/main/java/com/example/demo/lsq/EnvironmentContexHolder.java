package com.example.demo.lsq;


import org.springframework.stereotype.Component;


/**
 * <p>
 * 文件功能说明：
 *       			
 * </p>
 * 
 * @Author linshiqin
 *         <p>
 *         <li>2019年5月15日-下午3:43:14</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>linshiqin：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
@Component
public class EnvironmentContexHolder {

	private static ThreadLocal<CommonArea> commonInfo = new ThreadLocal<>();
	
	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年5月15日-下午4:54:42</li>
	 *         <li>功能说明：设置当前线程翻页信息</li>
	 *         </p>
	 * @param pageNumber
	 * @param pageSize
	 */
	public static void setPageInfo(int pageNumber,int pageSize){
		
		if(commonInfo.get() == null){
			CommonArea common = new CommonArea();
			common.setPageNumber(pageNumber);
			common.setPageSize(pageSize);
			common.setPage(true);
			commonInfo.set(common);
		}else{
			CommonArea common = commonInfo.get();
			common.setPageNumber(pageNumber);
			common.setPageSize(pageSize);
		}
	}
	
	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年5月15日-下午4:54:58</li>
	 *         <li>功能说明：获取当前线程翻页信息</li>
	 *         </p>
	 * @return
	 */
	public static CommonArea getPageInfo(){
		
		if(commonInfo.get() == null){
			return new CommonArea();
		}else{
			return commonInfo.get();
		}
	}
	
	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年5月15日-下午4:55:24</li>
	 *         <li>功能说明：设置当前线程翻页查询总数</li>
	 *         </p>
	 * @param total
	 */
	public static void setPageTotal(int total){
		
		if(commonInfo.get() == null){
			CommonArea common = new CommonArea();
			common.setTotal(total);
			commonInfo.set(common);
		}else{
			CommonArea common = commonInfo.get();
			common.setTotal(total);
		}
	}
	
	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年5月15日-下午4:55:39</li>
	 *         <li>功能说明：获取当前线程翻页查询总数</li>
	 *         </p>
	 * @return
	 */
	public static int getPageTotal(){
		
		if(commonInfo.get() == null){
			return 1;
		}else{
			return commonInfo.get().getTotal();
		}
	}
}
