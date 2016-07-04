/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.gateway.monitor.service;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.gateway.gateway.entry.AcceptorController;
import com.caitu99.gateway.gateway.model.RequestEvent;
import com.caitu99.gateway.monitor.api.IMonitorService;
import com.caitu99.gateway.monitor.constants.GatewayConstant;
import com.caitu99.gateway.monitor.entity.Gateway;
import com.caitu99.gateway.monitor.mongodb.MongoBaseDao;
import com.caitu99.gateway.utils.AppUtils;
import com.caitu99.gateway.utils.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MonitorService 
 * @author ws
 * @date 2015年11月27日 上午10:10:45 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class MonitorService implements IMonitorService {
	
	@Autowired
	MongoBaseDao dao;
	
	private static final Logger logger = LoggerFactory.getLogger(MonitorService.class);

	
	/* (non-Javadoc)
	 * @see com.caitu99.gateway.monitor.api.IMonitorService#saveGatewayInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void saveGatewayInfo(String group, String api, String host,
			String rt, String isSuccess, String contents) {
		
		Gateway gateway = new Gateway();
		try{
			String time = DateUtil.getTimeNumber();
			//Integer rand = RandomUtils.nextInt(100);
			gateway.setGroup(group);
			gateway.setApi(api);
			gateway.setHost(host);
			gateway.setRt(rt);//毫秒
			gateway.setIsSuccess(isSuccess);
			gateway.setContents(contents);
			gateway.setMinute(time.substring(0,time.length()-2));//记录到分
			gateway.setTime(time);
			
			dao.add(gateway);
		}catch(Exception ex){
			//为了不影响网关的正常使用，这里将异常内吞，并记录error日志
			logger.error("网关数据存储异常：{}",ex);
			logger.error("网关数据存储异常,网关数据：{}",gateway.toString());
		}
	}


	/* (non-Javadoc)
	 * @see com.caitu99.gateway.monitor.api.IMonitorService#saveGatewayInfo(com.caitu99.gateway.monitor.entity.Gateway)
	 */
	@Override
	public void saveGatewayInfo(Gateway gateway) {
		try{
			String time = DateUtil.getTimeNumber();

			gateway.setMinute(time.substring(0,time.length()-2));//记录到分
			gateway.setTime(time);
			
			dao.add(gateway);
		}catch(Exception ex){
			//为了不影响网关的正常使用，这里将异常内吞，并记录error日志
			logger.error("网关数据存储异常：{}",ex);
			logger.error("网关数据存储异常,网关数据：{}",gateway.toString());
		}
		
	}
	

	public void saveMonitorData(RequestEvent event, long all, long call,
			long pre, long redis, String isSuccess, String exceptionName) {
		Gateway gateway = new Gateway();
		try {
			gateway.setApi(event.getNamespace()+event.getMethod());
			gateway.setCallT(String.valueOf(call));
			gateway.setContents(event.getResultStr());
			gateway.setGroup(event.getNamespace());
			gateway.setHost(AppUtils.getHostName());
			gateway.setIsSuccess(isSuccess);
			gateway.setPreT(String.valueOf(pre));
			gateway.setRedisT(String.valueOf(redis));
			gateway.setRt(String.valueOf(all));
			gateway.setExceptionName(exceptionName);
			
			String time = DateUtil.getTimeNumber();
			gateway.setMinute(time.substring(0,time.length()-2));//记录到分
			gateway.setTime(time);
			
			dao.add(gateway);
		} catch (Exception e) {
			//为了不影响网关的正常使用，这里将异常内吞，并记录error日志
			logger.error("网关数据存储异常：{}",e);
			logger.error("网关数据存储异常,网关数据：{}",gateway.toString());
		}
		
	}

}
