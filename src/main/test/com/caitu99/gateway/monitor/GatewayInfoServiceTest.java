/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.gateway.monitor;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.caitu99.gateway.AbstractJunit;
import com.caitu99.gateway.monitor.constants.GatewayConstant;
import com.caitu99.gateway.monitor.entity.Gateway;
import com.caitu99.gateway.monitor.mongodb.MongoBaseDao;
import com.caitu99.gateway.monitor.service.MonitorService;
import com.caitu99.gateway.utils.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: GatewayInfoServiceTest 
 * @author ws
 * @date 2015年11月26日 下午3:03:10 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class GatewayInfoServiceTest extends AbstractJunit{

	@Autowired
	MonitorService service;
	@Autowired
	MongoBaseDao dao;
	
	/**
	 * Test method for {@link com.caitu99.service.GatewayInfoService#saveGatewayInfo(com.caitu99.entity.Gateway)}.
	 */
	@Test
	public void testSaveGatewayInfo() {

		for(int i = 0;i<9;i++){
			Gateway info = new Gateway();
			String time = DateUtil.getTimeNumber();
			Integer rand = RandomUtils.nextInt(100);
			Integer bol = RandomUtils.nextInt(2);
			info.setApi("spider/airChina");
			info.setGroup("spider");
			info.setHost("192.168.1.1");
			info.setIsSuccess(bol.toString());
			info.setMinute(time.substring(0,time.length()-2));//记录到分
			info.setRt(rand.toString());//毫秒
			info.setTime(time);
			info.setContents("test");
			dao.add(info);
		}
		/*Integer rand = RandomUtils.nextInt(100);
		String group = "spider";
		String api = "spider/taobao";
		String host = "192.168.1.1";
		String rt = rand.toString();
		String isSuccess = GatewayConstant.TRUE;
		String contents = "test";
		service.saveGatewayInfo(group, api, host, rt, isSuccess, contents);*/
		
	}

}
