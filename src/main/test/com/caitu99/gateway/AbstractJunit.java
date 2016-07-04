/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.gateway;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AbstractJunit 
 * @author ws
 * @date 2015年11月3日 上午11:03:58 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring.xml"})
@TransactionConfiguration(defaultRollback=true,transactionManager="txManagerCommon")
@ActiveProfiles("dev")
public abstract class AbstractJunit {

}
