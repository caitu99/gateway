/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.gateway.monitor.api;

import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.monitor.entity.Gateway;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IMonitorService 
 * @author ws
 * @date 2015年11月27日 上午10:06:02 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface IMonitorService {
	
	/**
	 * 网关数据存储
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveGatewayInfo 
	 * @param group 分组
	 * @param api	api名称
	 * @param host	主机地址
	 * @param rt	rt用时（毫秒）
	 * @param isSuccess	调用是否成功（无异常）see GatewayConstant
	 * @param contents 其他内容
	 * @date 2015年11月27日 上午10:08:43  
	 * @author ws
	 */
	void saveGatewayInfo(String group, String api, String host, String rt, String isSuccess, String contents);
	
	/**
	 * 网关数据存储
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveGatewayInfo 
	 * @param group 分组
	 * @param api	api名称
	 * @param host	主机地址
	 * @param rt	rt用时（毫秒）
	 * @param isSuccess	调用是否成功（无异常）see GatewayConstant
	 * @param contents 其他内容
	 * @date 2015年11月27日 上午10:08:43  
	 * @author ws
	 */
	void saveGatewayInfo(Gateway gateway);
	
	public void saveMonitorData(RequestEvent event, long all, long call,
			long pre, long redis, String isSuccess, String exceptionName);
	
}
