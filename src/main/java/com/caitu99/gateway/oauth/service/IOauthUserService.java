package com.caitu99.gateway.oauth.service;


import com.caitu99.gateway.oauth.model.OauthUser;

/**
 * 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IUserService 
 * @author yang
 * @date 2015年10月21日 上午11:11:39 
 * @Copyright (c) 2015-2020 by caitu99
 */
public interface IOauthUserService {

	OauthUser getUserByUserId(OauthUser oauthUser);
	OauthUser getUserByMobile(OauthUser oauthUser);
	OauthUser getUserByQQ(OauthUser oauthUser);

	void insertUserByMobile(OauthUser user);

	void insertUserByQQ(OauthUser user);

	OauthUser getUserByWeChatOpenId(OauthUser oauthUser);
	OauthUser getUserByWeChatOpenIdBinding(OauthUser oauthUser);
	void insertUserByWeChatOpenId(OauthUser user);

	OauthUser getUserByWeibo(OauthUser user);

	void insertUserByWeibo(OauthUser user);

    void updateUserByQQ(OauthUser user);
    void updateUserByWeiBo(OauthUser user);
    void updateUserByWeChat(OauthUser user);

    Integer getUserIdByQQ(OauthUser user);
    Integer getUserIdByWeiBo(OauthUser user);
    Integer getUserIdByWeChat(OauthUser user);
	void updateUserLoginCntByMobile(OauthUser user);


}
