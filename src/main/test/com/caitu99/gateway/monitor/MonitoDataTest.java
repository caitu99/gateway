/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.gateway.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.AbstractJunit;
import com.caitu99.gateway.monitor.constants.GatewayConstant;
import com.caitu99.gateway.monitor.entity.CountResult;
import com.caitu99.gateway.monitor.entry.MonitorDataController;
import com.caitu99.gateway.monitor.mapreduce.MapReduceJs;
import com.caitu99.gateway.monitor.mongodb.MapReduceDao;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MonitoDataTest 
 * @author ws
 * @date 2015年12月21日 上午11:44:23 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class MonitoDataTest extends AbstractJunit{

	@Autowired
	MapReduceDao mapReduceDao;
	@Autowired
	MonitorDataController monitorDataController;
	
	@Test
	public void test() {
		List<Map<String,List<Map<String,String>>>> response = new ArrayList<Map<String,List<Map<String,String>>>>();
		
		Map<String,List<Map<String,String>>> resMap = new HashMap<String, List<Map<String,String>>>();
		
		List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		
		String beginTime = "20151121113000";
		String endTime = "20151222113000";
		String group = "";
		String api = "";
		String host = "";
		
		Query query = creatQueryConditions(beginTime, endTime, group, api, host);
		
		/*String inputCollectionName = GatewayConstant.DB_NAME;
		String mapFunction = MapReduceJs.MAP_RT_COUNT;
		String reduceFunction = MapReduceJs.REDUCE_RT_COUNT;*/
		
		/*String inputCollectionName = GatewayConstant.DB_NAME;
		String mapFunction = MapReduceJs.MAP_RT_MAX;
		String reduceFunction = MapReduceJs.REDUCE_RT_MAX;*/
		
		/*******失败次数********/
		String inputCollectionName = GatewayConstant.DB_NAME;
		String mapFunction = MapReduceJs.MAP_FAILURE_COUNT;
		String reduceFunction = MapReduceJs.REDUCE_FAILURE_COUNT;
		
		MapReduceResults<CountResult> rtResult = mapReduceDao.command(query
				, inputCollectionName, mapFunction, reduceFunction);
		
		for (CountResult countResult : rtResult) {
			Map<String, String> res = new HashMap<String, String>();
			res.put("key", countResult.get_id());
			res.put("value", countResult.getValue());
			result.add(res);
		}
		resMap.put("failure", result);
		response.add(resMap);
		
		/*******最大rt时间********/
		inputCollectionName = GatewayConstant.DB_NAME;
		mapFunction = MapReduceJs.MAP_RT_MAX;
		reduceFunction = MapReduceJs.REDUCE_RT_MAX;
		rtResult = mapReduceDao.command(query
				, inputCollectionName, mapFunction, reduceFunction);
		
		for (CountResult countResult : rtResult) {
			Map<String, String> res = new HashMap<String, String>();
			res.put("key", countResult.get_id());
			res.put("value", countResult.getValue());
			result.add(res);
		}
		resMap.put("maxResponseTime", result);
		response.add(resMap);
		
		/*******调用次数********/
		inputCollectionName = GatewayConstant.DB_NAME;
		mapFunction = MapReduceJs.MAP_REQUEST_COUNT;
		reduceFunction = MapReduceJs.REDUCE_REQUEST_COUNT;
		rtResult = mapReduceDao.command(query
				, inputCollectionName, mapFunction, reduceFunction);
		
		for (CountResult countResult : rtResult) {
			Map<String, String> res = new HashMap<String, String>();
			res.put("key", countResult.get_id());
			res.put("value", countResult.getValue());
			result.add(res);
		}
		resMap.put("callCount", result);
		response.add(resMap);
		
		System.out.println(JSON.toJSONString(response));
		
	}
	
	@Test
	public void testMonitor(){
		String result = monitorDataController.getMonitoData("1450800000", "1450858740"
				, "", "", "", "");
		System.out.println(result);
	}
	
	
	/**
	 * 构造查询条件
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: greatQueryConditions 
	 * @param beginTime
	 * @param endTime
	 * @param group
	 * @param api
	 * @param host
	 * @return
	 * @date 2015年11月27日 下午2:19:57  
	 * @author ws
	 */
	private Query creatQueryConditions(String beginTime, String endTime,
			String group, String api, String host) {
		Query query = new Query(Criteria.where("minute").gte(beginTime).lte(endTime));
		if(StringUtils.isNotBlank(group)){
			query.addCriteria(Criteria.where("host").is(group));
		}
		if(StringUtils.isNotBlank(api)){
			query.addCriteria(Criteria.where("api").is(api));
		}
		if(StringUtils.isNotBlank(host)){
			query.addCriteria(Criteria.where("host").is(host));
		}
		//query.with(new Sort(new Order(Direction.DESC, "minute")));//升序
		return query;
	}
	

}
