package com.caitu99.gateway.oauth.service.impl;


import com.caitu99.gateway.cache.RedisOperate;
import com.caitu99.gateway.oauth.Constants;
import com.caitu99.gateway.oauth.dao.user.IOauthUserDAO;
import com.caitu99.gateway.oauth.model.OauthUser;
import com.caitu99.gateway.oauth.service.IOauthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserService 
 * @author yang
 * @date 2015年10月21日 上午11:12:28 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Service
public class UserService implements IOauthUserService {

    @Autowired
    private IOauthUserDAO userDAO;

    @Autowired
    private RedisOperate redis;


	public IOauthUserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(IOauthUserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	@Override
	public OauthUser getUserByUserId(OauthUser oauthUser) {
		return userDAO.getUserByUserId(oauthUser);
	}

	@Override
	public OauthUser getUserByMobile(OauthUser oauthUser) {
		return userDAO.getUserByMobile(oauthUser);
	}

	@Override
	public OauthUser getUserByQQ(OauthUser oauthUser) {
		return userDAO.getUserByQQ(oauthUser);
	}

	@Override
	public void insertUserByMobile(OauthUser user) {
		userDAO.insertUserByMobile(user);
	}

	@Override
	public void insertUserByQQ(OauthUser user) {
		userDAO.insertUserByQQ(user);
	}

	@Override
	public OauthUser getUserByWeChatOpenId(OauthUser oauthUser) {
		return userDAO.getUserByWeChatOpenId(oauthUser);
	}
	
	@Override
	public OauthUser getUserByWeChatOpenIdBinding(OauthUser oauthUser) {
		return userDAO.getUserByWeChatOpenIdBinding(oauthUser);
	}

	@Override
	public void insertUserByWeChatOpenId(OauthUser user) {
		userDAO.insertUserByWeChatOpenId(user);
	}

	@Override
	public OauthUser getUserByWeibo(OauthUser user) {
		return userDAO.getUserByWeibo(user);
	}

	@Override
	public void insertUserByWeibo(OauthUser user) {
		userDAO.insertUserByWeibo(user);
	}

    @Override
    public void updateUserByQQ(OauthUser user) {
        userDAO.updateUserByQQ(user);

        String key = String.format(Constants.USER_USER_BY_ID_KEY, userDAO.getUserIdByQQ(user));

        redis.del(key);
    }

    @Override
    public void updateUserByWeiBo(OauthUser user) {
        userDAO.updateUserByWeiBo(user);
        String key = String.format(Constants.USER_USER_BY_ID_KEY, userDAO.getUserIdByWeiBo(user));
        redis.del(key);
    }

    @Override
    public void updateUserByWeChat(OauthUser user) {
        userDAO.updateUserByWeChat(user);
        String key = String.format(Constants.USER_USER_BY_ID_KEY, userDAO.getUserIdByWeChat(user));
        redis.del(key);
    }

    @Override
    public Integer getUserIdByQQ(OauthUser user) {
        return userDAO.getUserIdByQQ(user);
    }

    @Override
    public Integer getUserIdByWeiBo(OauthUser user) {
        return userDAO.getUserIdByWeiBo(user);
    }

    @Override
    public Integer getUserIdByWeChat(OauthUser user) {
        return userDAO.getUserIdByWeChat(user);
    }

	@Override
	public void updateUserLoginCntByMobile(OauthUser user) {
		userDAO.updateUserLoginCntByMobile(user);
	}


}
