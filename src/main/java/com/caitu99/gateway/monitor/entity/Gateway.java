/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.gateway.monitor.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: Gateway 
 * @author ws
 * @date 2015年11月26日 下午2:24:16 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Document
public class Gateway {
	private String group;
	private String api;
	private String host;
	private String time;
	private String rt;
	private String callT;
	private String preT;
	private String redisT;
	private String isSuccess;
	private String minute;
	private String contents;
	private String exceptionName;
	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}
	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	/**
	 * @return the api
	 */
	public String getApi() {
		return api;
	}
	/**
	 * @param api the api to set
	 */
	public void setApi(String api) {
		this.api = api;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * @return the rt
	 */
	public String getRt() {
		return rt;
	}
	/**
	 * @param rt the rt to set
	 */
	public void setRt(String rt) {
		this.rt = rt;
	}
	
	/**
	 * @return the isSuccess
	 */
	public String getIsSuccess() {
		return isSuccess;
	}
	/**
	 * @param isSuccess the isSuccess to set
	 */
	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}
	/**
	 * @return the minute
	 */
	public String getMinute() {
		return minute;
	}
	/**
	 * @param minute the minute to set
	 */
	public void setMinute(String minute) {
		this.minute = minute;
	}
	/**
	 * @return the contents
	 */
	public String getContents() {
		return contents;
	}
	/**
	 * @param contents the contents to set
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}
	/**
	 * @return the callT
	 */
	public String getCallT() {
		return callT;
	}
	/**
	 * @param callT the callT to set
	 */
	public void setCallT(String callT) {
		this.callT = callT;
	}
	/**
	 * @return the preT
	 */
	public String getPreT() {
		return preT;
	}
	/**
	 * @param preT the preT to set
	 */
	public void setPreT(String preT) {
		this.preT = preT;
	}
	/**
	 * @return the redisT
	 */
	public String getRedisT() {
		return redisT;
	}
	/**
	 * @param redisT the redisT to set
	 */
	public void setRedisT(String redisT) {
		this.redisT = redisT;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuffer("group:").append(group).append(",api:").append(api)
				.append(",host:").append(host).append(",rt:").append(rt)
				.append(",isSuccess:").append(isSuccess)
				.append(",time:").append(time).toString();
	}
	/**
	 * @return the exceptionName
	 */
	public String getExceptionName() {
		return exceptionName;
	}
	/**
	 * @param exceptionName the exceptionName to set
	 */
	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}


}
