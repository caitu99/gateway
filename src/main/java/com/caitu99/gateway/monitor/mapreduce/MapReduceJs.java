/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.gateway.monitor.mapreduce;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MapReduceJs 
 * @author ws
 * @date 2015年11月27日 上午9:50:21 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public interface MapReduceJs {
	
	public static final String MAP_FAILURE_COUNT = "function (){emit(this.minute,{isSuccess:this.isSuccess});}";
	public static final String REDUCE_FAILURE_COUNT = "function (key,values){var count=0;values.forEach(function(val){if('1'==val.isSuccess){count +=1;}});return count;}";
	
	public static final String MAP_RT_MAX = "function (){emit(this.minute,{rt:this.rt});}";
	public static final String REDUCE_RT_MAX = "function (key,values){var ctmax=0; values.forEach(function(val){if(ctmax < val.rt){ctmax = val.rt;}});return ctmax ;}";

	public static final String MAP_REQUEST_COUNT = "function (){emit(this.minute,{count:1});}";
	public static final String REDUCE_REQUEST_COUNT = "function (key,values){var cnt=0;values.forEach(function(val){ cnt+=val.count;});return cnt;}";

	public static final String MAP_RT_COUNT = "function (){emit(this.minute,{rt:this.rt});}";
	public static final String REDUCE_RT_COUNT = "function (key,values){var cnt=1;var rtall=0;values.forEach(function(val){cnt+=1;rtall+=parseInt(val.rt);});return (rtall/cnt).toFixed(2);}";
	
}
