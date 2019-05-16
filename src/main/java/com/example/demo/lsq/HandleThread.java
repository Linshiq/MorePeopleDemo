package com.example.demo.lsq;



/**
 * <p>
 * 文件功能说明：
 *       			
 * </p>
 * 
 * @Author linshiqin
 *         <p>
 *         <li>2018年8月2日-下午1:24:09</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>linshiqin：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class HandleThread implements Runnable{
	
	private String result = "";
	private int page;
	private int pageSize;
	private String name;
	
	HandleThread(int page,int pageSize,String name){
		this.page = page;
		this.pageSize = pageSize;
		this.name = name;
	}
	
	public String getResult(){
		return result;
	}
	
	@Override
	public void run() {
		// 测试 ThreadLocal<CommonArea>是否有效
		System.out.println(name+"赋值");
		EnvironmentContexHolder.setPageInfo(page, pageSize);
		CommonArea commonArea = EnvironmentContexHolder.getPageInfo();
		
		System.out.println(name+"取值" + commonArea.getPageNumber() + " " + commonArea.getPageSize());
	}

	public static void main(String[] args) {
		
		for (int i = 0; i < 50; i++) {
			HandleThread thread = new HandleThread(i,i,"lsq"+i);
			new Thread(thread).start();
		}
	}
}
