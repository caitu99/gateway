package com.caitu99.gateway.oauth.dao.user;

import com.caitu99.gateway.oauth.model.OauthUser;
import com.caitu99.platform.dao.base.func.IEntityDAO;

public interface IOauthUserDAO extends IEntityDAO<OauthUser, OauthUser> {
    OauthUser getUserByUserId(OauthUser oauthUser);

    OauthUser getUserByQQ(OauthUser oauthUser);

    OauthUser getUserByMobile(OauthUser oauthUser);

    void insertUserByMobile(OauthUser user);

    void updateUserLoginCntByMobile(OauthUser user);

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
}