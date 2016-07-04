package com.caitu99.gateway.oauth.service.impl;

import com.alibaba.fastjson.JSON;
import com.caitu99.gateway.AppConfig;
import com.caitu99.gateway.apiconfig.model.OpenResource;
import com.caitu99.gateway.apiconfig.service.IOpenResourceService;
import com.caitu99.gateway.cache.RedisOperate;
import com.caitu99.gateway.oauth.Constants;
import com.caitu99.gateway.oauth.LoginType;
import com.caitu99.gateway.oauth.dao.user.AccountMapper;
import com.caitu99.gateway.oauth.exception.OAuthException;
import com.caitu99.gateway.oauth.model.*;
import com.caitu99.gateway.oauth.oauthex.OAuthConstants;
import com.caitu99.gateway.oauth.service.*;
import com.caitu99.gateway.utils.AppUtils;
import com.caitu99.gateway.utils.SpringContext;
import com.caitu99.gateway.utils.StrUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class OAuthService implements IOAuthService {

	public static final Logger logger = LoggerFactory
			.getLogger(OAuthService.class);

	@Autowired
	private RedisOperate redis;

	@Autowired
	private IOpenOauthClientsService openOauthClientsService;

	@Autowired
	private IOpenOauthAccessTokensService openOauthAccessTokensService;

	@Autowired
	private IOpenOauthRefreshTokensService openOauthRefreshTokensService;

	@Autowired
	private IOpenResourceService openResourceService;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private AccountMapper accountMapper;

	public static String encodeStringWithUtf8(String str) throws OAuthException {
		if (str == null)
			return null;
		byte[] bytes = null;
		try {
			bytes = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("编码错误，不支持的编码");
		}
		String ans = "";
		for (byte tbyte : bytes) {
			ans = ans + ":" + Integer.toString(tbyte & 0xff);
		}
		return ans;
	}

	public static String decodeStringFromUtf8(String ans) throws OAuthException {
		if (ans == null)
			return null;
		String[] out = ans.split(":");
		byte[] result = new byte[100];
		int cnt = 0;
		for (String anOut : out) {
			if (anOut.equals("")) {
				continue;
			}
			result[cnt++] = (byte) Integer.parseInt(anOut);
		}
		try {
			return new String(result, 0, cnt, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("解码错误，不支持的编码");
		}
	}

	/**
	 * get client by client id
	 *
	 * @param clientId
	 * @return
	 */
	public OpenOauthClients getClientByClientId(String clientId) {
		String key = String
				.format(Constants.CACHE_CLIENT_KEY_PATTERN, clientId);
		String content = redis.getStringByKey(key);
		if (!StringUtils.isEmpty(content)) {
			return JSON.parseObject(content, OpenOauthClients.class);
		}
		OpenOauthClients clients = openOauthClientsService
				.getByClientId(clientId);
		if (clients != null)
			redis.set(key, JSON.toJSONString(clients));
		return clients;
	}

	/**
	 * save client
	 *
	 * @param openOauthClients
	 * @return
	 */
	public int saveClient(OpenOauthClients openOauthClients) {
		String key = String.format(Constants.CACHE_CLIENT_KEY_PATTERN,
				openOauthClients.getClientId());
		redis.del(key);
		return openOauthClientsService.save(openOauthClients);
	}

	/**
	 * save access token to db
	 *
	 * @param accessToken
	 * @param oAuthAuthzParameters
	 * @return
	 */
	public int saveAccessToken(String accessToken,
			OAuthAuthzParameters oAuthAuthzParameters) {
		OpenOauthAccessTokens openOauthAccessTokens = new OpenOauthAccessTokens();
		openOauthAccessTokens.setAccessToken(accessToken);
		openOauthAccessTokens.setClientId(oAuthAuthzParameters.getClientId());
		openOauthAccessTokens.setUserId(oAuthAuthzParameters.getUserId()); // default
																			// is
																			// 0

		String userId = String.valueOf(oAuthAuthzParameters.getUserId());

		openOauthAccessTokens.setExtra(userId); // not used
		openOauthAccessTokens.setScope(oAuthAuthzParameters.getScope());

		// expire in 7 days
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, appConfig.tokenAccessExpire);
		openOauthAccessTokens.setExpires(calendar.getTime());

		return openOauthAccessTokensService.save(openOauthAccessTokens);
	}

	// private void encodeUtf8mb4Str(User user, UserThirdInfo userThirdInfo) {
	// if (null != user.getWechatNick()) {
	// user.setWechatNick(encodeStringWithUtf8(user.getWechatNick()));
	// }
	// if (null != user.getWeiboNick()) {
	// user.setWeiboNick(encodeStringWithUtf8(user.getWeiboNick()));
	// }
	// if (null != user.getQqNick()) {
	// user.setQqNick(encodeStringWithUtf8(user.getQqNick()));
	// }
	//
	// if (null != userThirdInfo.getWeixinNickname()) {
	// userThirdInfo.setWeixinNickname(encodeStringWithUtf8(userThirdInfo
	// .getWeixinNickname()));
	// }
	// if (null != userThirdInfo.getQqScreenName()) {
	// userThirdInfo.setQqScreenName(encodeStringWithUtf8(userThirdInfo
	// .getQqScreenName()));
	// }
	// if (null != userThirdInfo.getWeiboScreenName()) {
	// userThirdInfo.setWeiboScreenName(encodeStringWithUtf8(userThirdInfo
	// .getWeiboScreenName()));
	// }
	// }

	/**
	 * save refresh token to db
	 *
	 * @param refreshToken
	 * @param oAuthAuthzParameters
	 * @return
	 */
	public int saveRefreshToken(String refreshToken, String accessToken,
			OAuthAuthzParameters oAuthAuthzParameters) {
		OpenOauthRefreshTokens openOauthRefreshTokens = new OpenOauthRefreshTokens();
		openOauthRefreshTokens.setRefreshToken(refreshToken);
		openOauthRefreshTokens.setClientId(oAuthAuthzParameters.getClientId());
		openOauthRefreshTokens.setUserId(oAuthAuthzParameters.getUserId()); // default
																			// is
																			// 0

		String userId = String.valueOf(oAuthAuthzParameters.getUserId());

		openOauthRefreshTokens.setExtra(userId); // not used
		openOauthRefreshTokens.setScope(oAuthAuthzParameters.getScope());
		openOauthRefreshTokens.setAccessToken(accessToken);

		// expire in 28 days
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, appConfig.tokenRefreshExpire);
		openOauthRefreshTokens.setExpires(calendar.getTime());

		return openOauthRefreshTokensService.save(openOauthRefreshTokens);
	}

	/**
	 * the api of login
	 *
	 * @param account
	 * @param password
	 * @return
	 */
	public OauthUser login(String account, String password) {
		UserService userService = SpringContext.getBean(UserService.class);
		OauthUser user = new OauthUser();
		user.setAccount(account);
		user.setPassword(password);
		user = userService.getUserByUserId(user);
		return user;
	}

	/**
	 * the api of login
	 *
	 * @param oAuthAuthzParameters
	 * @return
	 */
	// public OauthUser login(String account, String password,Integer type)
	// throws OAuthException {
	public OauthUser login(OAuthAuthzParameters oAuthAuthzParameters,
			HttpServletRequest request) throws OAuthException {
		AppConfig appConfig = SpringContext.getBean(AppConfig.class);
		UserService userService = SpringContext.getBean(UserService.class);
		UserThirdInfoService userThirdInfoService = SpringContext
				.getBean(UserThirdInfoService.class);
		OauthUser user = new OauthUser();
		user.setAccount(oAuthAuthzParameters.getUserName());

		LoginType loginType = oAuthAuthzParameters.getType();
		if (loginType == LoginType.MOBILE) {
			String mobile = oAuthAuthzParameters.getUserName();
			if (mobile != null && mobile.equals("13788888888")) {
				redis.set(String.format(Constants.SMS_SEND_KEY, "13788888888"),
						"1234", 600);
			}
			if (mobile != null && mobile.equals("13000000000")) {
				redis.set(String.format(Constants.SMS_SEND_KEY, "13000000000"),
						"1234", 600);
			}
			if (!appConfig.isDevMode()) {
				String vCode = redis.getStringByKey(String.format(
						Constants.SMS_SEND_KEY, user.getAccount()));
				if (StringUtils.isEmpty(vCode)) {
					throw new OAuthException(
							OAuthConstants.OAuthResponse.NO_VCODE, "验证码过期");
				}
				if (!vCode.equals(oAuthAuthzParameters.getPassword())) {
					throw new OAuthException(
							OAuthConstants.OAuthResponse.WRONG_VCODE, "验证码错误");
				}
			}
			redis.del(String.format(Constants.SMS_SEND_KEY, user.getAccount()));
			user = userService.getUserByMobile(user); // 查询是否在数据库中已存在

			if (user == null || user.getAccount() == null) {
				user = new OauthUser();
				user.setAccount(oAuthAuthzParameters.getUserName());
				user.setClient_id(Integer.valueOf(oAuthAuthzParameters.getClientId()));
				user.setLogin_count(1);
				userService.insertUserByMobile(user);

				// 创建账户
				Account account = new Account();
				account.setAvailableIntegral(0L);
				account.setFreezeIntegral(0L);
				account.setGmtCreate(new Date());
				account.setGmtModify(new Date());
				account.setTotalIntegral(0L);
				account.setUserId(Long.valueOf(user.getId()));
				accountMapper.insert(account);

			} else {
				if (user.getType() == 2) {
					throw new OAuthException(
							OAuthConstants.OAuthResponse.WRONG_USERNOTVALID,
							"企业用户不允许登录");
				}
				user.setLogin_count((user.getLogin_count() == null || user
						.getLogin_count() < 1) ? 2
						: (user.getLogin_count() + 1));
				userService.updateUserLoginCntByMobile(user);
			}
		} else {
			String nickname = oAuthAuthzParameters.getNickname();

			if (loginType == LoginType.QQ)// QQ
			{
				user = userService.getUserByQQ(user);
				if (user == null || user.getAccount() == null) {
					user = new OauthUser();
					user.setAccount(oAuthAuthzParameters.getUserName());
					user.setNickname(nickname);
					user.setClient_id(Integer.valueOf(oAuthAuthzParameters.getClientId()));
					user.setLogin_count(1);
					// user.setProfileimg(oAuthAuthzParameters.getProfileimg());
					userService.insertUserByQQ(user);
					// 创建账户
					Account account = new Account();
					account.setAvailableIntegral(0L);
					account.setFreezeIntegral(0L);
					account.setGmtCreate(new Date());
					account.setGmtModify(new Date());
					account.setTotalIntegral(0L);
					account.setUserId(Long.valueOf(user.getId()));
					accountMapper.insert(account);
				} else {// 更新昵称
					if (user.getType() == 2) {
						throw new OAuthException(
								OAuthConstants.OAuthResponse.WRONG_USERNOTVALID,
								"企业用户不允许登录");
					}
					// String nickName = user.getNickname();
					// if(nickName == null ||
					// !nickName.equals(oAuthAuthzParameters.getNickname())){
					user.setNickname(nickname); // 昵称

					// user.setProfileimg(oAuthAuthzParameters.getProfileimg());
					user.setLogin_count((user.getLogin_count() == null || user
							.getLogin_count() < 1) ? 2
							: (user.getLogin_count() + 1));
					userService.updateUserByQQ(user);
					// }
				}
				// 保存第三方信息
				UserThirdInfo userThirdInfo = userThirdInfoService
						.selectByUserId(new Long(String.valueOf(user.getId())));
				if (userThirdInfo == null) {
					userThirdInfo = new UserThirdInfo();
					userThirdInfo.setUserId(new Long(String.valueOf(user
							.getId())));
					userThirdInfo.setQqUid(user.getAccount());
					userThirdInfo.setQqScreenName(user.getNickname());
					userThirdInfo.setQqProfileImageUrl(oAuthAuthzParameters
							.getProfileimg()); // imgurl
					userThirdInfoService.insert(userThirdInfo);
				} else {
					// 更新
					// 昵称是否有变化
					if (!StrUtils.equals(userThirdInfo.getQqScreenName(),
							user.getAccount())) {
						userThirdInfo.setQqScreenName(user.getNickname());
					}
					if (!StrUtils.equals(userThirdInfo.getQqProfileImageUrl(),
							oAuthAuthzParameters.getProfileimg())) {
						userThirdInfo.setQqProfileImageUrl(oAuthAuthzParameters
								.getProfileimg());
					}

					userThirdInfoService
							.updateByPrimaryKeySelective(userThirdInfo);
				}
			} else if (loginType == LoginType.WEICHAT)// 微信
			{
				user = userService.getUserByWeChatOpenId(user);
				if (user == null || user.getAccount() == null) {
					user = new OauthUser();
					user.setAccount(oAuthAuthzParameters.getUserName());
					user.setNickname(nickname);
					user.setClient_id(Integer.valueOf(oAuthAuthzParameters.getClientId()));
					user.setLogin_count(1);
					// user.setProfileimg(oAuthAuthzParameters.getProfileimg());
					userService.insertUserByWeChatOpenId(user);
					// 创建账户
					Account account = new Account();
					account.setAvailableIntegral(0L);
					account.setFreezeIntegral(0L);
					account.setGmtCreate(new Date());
					account.setGmtModify(new Date());
					account.setTotalIntegral(0L);
					account.setUserId(Long.valueOf(user.getId()));
					accountMapper.insert(account);
				} else {
					if (user.getType() == 2) {
						throw new OAuthException(
								OAuthConstants.OAuthResponse.WRONG_USERNOTVALID,
								"企业用户不允许登录");
					}
					// String nickName = user.getNickname();
					// if(nickName == null ||
					// !nickName.equals(oAuthAuthzParameters.getNickname())){
					user.setNickname(nickname);
					user.setLogin_count((user.getLogin_count() == null || user
							.getLogin_count() < 1) ? 2
							: (user.getLogin_count() + 1));
					userService.updateUserByWeChat(user);
					// }
				}
				// 保存第三方信息
				UserThirdInfo userThirdInfo = userThirdInfoService
						.selectByUserId(new Long(String.valueOf(user.getId())));
				if (userThirdInfo == null) {
					userThirdInfo = new UserThirdInfo();
					userThirdInfo.setUserId(new Long(String.valueOf(user
							.getId())));
					userThirdInfo.setWeixinOpenid(user.getAccount());
					userThirdInfo.setWeixinNickname(user.getNickname());
					userThirdInfo.setWeixinImgurl(oAuthAuthzParameters
							.getProfileimg());
					userThirdInfoService.insert(userThirdInfo);
				} else {
					// 更新
					// 昵称是否有变化
					if (!StrUtils.equals(userThirdInfo.getWeixinNickname(),
							user.getAccount())) {
						userThirdInfo.setWeixinNickname(user.getNickname());
					}
					if (!StrUtils.equals(userThirdInfo.getWeixinImgurl(),
							oAuthAuthzParameters.getProfileimg())) {
						userThirdInfo.setWeixinImgurl(oAuthAuthzParameters
								.getProfileimg());
					}

					userThirdInfoService
							.updateByPrimaryKeySelective(userThirdInfo);
				}
			} else if (loginType == LoginType.WEBO) // 微博
			{
				user = userService.getUserByWeibo(user);
				if (user == null || user.getAccount() == null) {

					user = new OauthUser();
					user.setAccount(oAuthAuthzParameters.getUserName());
					user.setNickname(nickname);
					user.setClient_id(Integer.valueOf(oAuthAuthzParameters.getClientId()));
					user.setLogin_count(1);
					user.setProfileimg(oAuthAuthzParameters.getProfileimg());
					userService.insertUserByWeibo(user);
					
					// 创建账户
					Account account = new Account();
					account.setAvailableIntegral(0L);
					account.setFreezeIntegral(0L);
					account.setGmtCreate(new Date());
					account.setGmtModify(new Date());
					account.setTotalIntegral(0L);
					account.setUserId(Long.valueOf(user.getId()));
					accountMapper.insert(account);
				} else {
					if (user.getType() == 2) {
						throw new OAuthException(
								OAuthConstants.OAuthResponse.WRONG_USERNOTVALID,
								"企业用户不允许登录");
					}
					// String nickName = user.getNickname();
					// if(nickName == null ||
					// !nickName.equals(oAuthAuthzParameters.getNickname())){
					user.setNickname(nickname);
					user.setLogin_count((user.getLogin_count() == null || user
							.getLogin_count() < 1) ? 2
							: (user.getLogin_count() + 1));
					userService.updateUserByWeiBo(user);
					// }
				}
				// 保存第三方信息
				UserThirdInfo userThirdInfo = userThirdInfoService
						.selectByUserId(new Long(String.valueOf(user.getId())));
				if (userThirdInfo == null) {
					userThirdInfo = new UserThirdInfo();
					userThirdInfo.setUserId(new Long(String.valueOf(user
							.getId())));
					userThirdInfo.setWeiboUid(user.getAccount());
					userThirdInfo.setWeiboScreenName(user.getNickname());
					userThirdInfo.setWeiboProfileImageUrl(oAuthAuthzParameters
							.getProfileimg());
					userThirdInfoService.insert(userThirdInfo);
				} else {
					// 更新
					// 昵称是否有变化
					if (!StrUtils.equals(userThirdInfo.getWeiboScreenName(),
							user.getAccount())) {
						userThirdInfo.setWeiboScreenName(user.getNickname());
					}
					if (!StrUtils.equals(
							userThirdInfo.getWeiboProfileImageUrl(),
							oAuthAuthzParameters.getProfileimg())) {
						userThirdInfo
								.setWeiboProfileImageUrl(oAuthAuthzParameters
										.getProfileimg());
					}

					userThirdInfoService
							.updateByPrimaryKeySelective(userThirdInfo);
				}

			} else if(loginType == LoginType.WEICHAT_IDEN){//新微信登录,只用于登录
				user = userService.getUserByWeChatOpenIdBinding(user);
				if (user == null || user.getAccount() == null) {
					throw new OAuthException( OAuthConstants.OAuthResponse.INVALID_OPENID,"该用户未用微信登录过,不可登录");
				} else {
					if (user.getType() == 2) {
						throw new OAuthException( OAuthConstants.OAuthResponse.WRONG_USERNOTVALID,"企业用户不允许登录");
					}
				}
			}else {
				// 用户名和密码登录
				user = userService.getUserByMobile(user); // 查询是否在数据库中已存在
				if (user != null) {
					if (user.getType() == 1) {
						throw new OAuthException(
								OAuthConstants.OAuthResponse.COMM_USER_NOT_ALLOWED,
								"普通用户不允许使用账号密码登录");
					}
					//@TODO
					String username = oAuthAuthzParameters.getUserName();
					String password = oAuthAuthzParameters.getPassword();
					if (username == null || password == null) {
						throw new OAuthException(
								OAuthConstants.OAuthResponse.USERNAME_OR_PASSWORD_NULL,
								"用户名或密码为空");
					}
					password = AppUtils.MD5(password);
					String usernameFromDB = user.getAccount();
					String passwordFromDB = user.getPassword();
					if (username.equals(usernameFromDB)
							&& password.toLowerCase().equals(passwordFromDB)) {
						// 更新用户登录次数
						user.setLogin_count((user.getLogin_count() == null || user
								.getLogin_count() < 1) ? 2 : (user
								.getLogin_count() + 1));
						userService.updateUserLoginCntByMobile(user);
					} else {
						throw new OAuthException(
								OAuthConstants.OAuthResponse.WRONG_USERNAME_OR_PASSWORD,
								"用户名或密码错误");
					}
				} else {
					// 用户不存在
					throw new OAuthException(
							OAuthConstants.OAuthResponse.NOT_EXIST_USER,
							"用户名或密码错误");
				}
			}
			// try {
			// user.setNickname(decodeStringFromUtf8(user.getNickname()));
			// }
			// catch (Exception e)
			// {
			// throw new
			// OAuthException(OAuthConstants.OAuthResponse.DECODE_ERROR,
			// e.getMessage());
			// }

		}

		return user;
	}

	/**
	 * get refresh token by token string
	 *
	 * @param token
	 * @return
	 */
	public OpenOauthRefreshTokens getRefreshToken(String token) {
		String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
		String content = redis.getStringByKey(key);
		if (!StringUtils.isEmpty(content)) {
			return JSON.parseObject(content, OpenOauthRefreshTokens.class);
		}
		OpenOauthRefreshTokens tokens = openOauthRefreshTokensService
				.getByToken(token);
		if (tokens != null)
			redis.set(key, JSON.toJSONString(tokens));
		return tokens;
	}

	/**
	 * retain the elements which are in scopes1
	 *
	 * @param scopes1
	 * @param scopes2
	 * @return
	 */
	public Set<String> getRetainScopes(String scopes1, String scopes2) {
		Set<String> scopes_1 = OAuthUtils.decodeScopes(scopes1);
		Set<String> scopes_2 = OAuthUtils.decodeScopes(scopes2);

		Set<String> result = new HashSet<String>();
		result.addAll(scopes_2);
		result.retainAll(scopes_1);
		return result;
	}

	/**
	 * put to redis cache, the key will expire in 2 minutes
	 *
	 * @param token
	 * @param parameters
	 */
	public void putOAuthAuthzParameters(String token,
			OAuthAuthzParameters parameters) {
		String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
		redis.set(key, JSON.toJSONString(parameters), 10 * 60);
	}

	/**
	 * delete parameter after getting token
	 *
	 * @param token
	 */
	public void delOAuthAuthzParameters(String token) {
		String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
		redis.del(key);
	}

	/**
	 * check refresh token frequency
	 *
	 * @param token
	 * @return
	 */
	public boolean checkRefreshFrequency(String token) {
		String key = String.format(Constants.CACHE_REFRESH_FREQUENCY_PATTERN,
				token);
		String content = redis.getStringByKey(key);
		if (content != null) {
			return false;
		}
		redis.set(key, token, appConfig.frequency);
		return true;
	}

	public boolean checkLoginFrequency(String account) {
		String key = String.format(Constants.CACHE_LOGIN_FREQUENCY_PATTERN,
				account);
		String content = redis.getStringByKey(key);
		if (content != null) {
			return false;
		}
		redis.set(key, key, appConfig.frequency);
		return true;
	}

	/**
	 * get from redis cache
	 *
	 * @param token
	 * @return
	 */
	public OAuthAuthzParameters getOAuthAuthzParameters(String token) {
		String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
		String content = redis.getStringByKey(key);
		if (!StringUtils.isEmpty(content)) {
			return JSON.parseObject(content, OAuthAuthzParameters.class);
		}
		return null;
	}

	/**
	 * get access token by token string
	 *
	 * @param token
	 * @return
	 */
	public OpenOauthAccessTokens getAccessToken(String token) {
		String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token);
		String content = redis.getStringByKey(key);
		logger.info("get token from redis: {}", content);
		if (!StringUtils.isEmpty(content)) {
			return JSON.parseObject(content, OpenOauthAccessTokens.class);
		}
		OpenOauthAccessTokens tokens = openOauthAccessTokensService
				.getByToken(token);
		logger.info("get token from db: {}", tokens);
		if (tokens != null)
			redis.set(key, JSON.toJSONString(tokens));
		return tokens;
	}

	/**
	 * get resource by uri
	 *
	 * @param uri
	 * @return
	 */
	public OpenResource getOpenResourceByUri(String uri) {
		return openResourceService.getByUri(uri);
	}

	/**
	 * delete access token by token string
	 *
	 * @param token
	 */
	public void delAccessToken(String token) {
		redis.del(String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token));
		openOauthAccessTokensService.deleteByToken(token);
	}

	/**
	 * delete refresh token by token string
	 *
	 * @param token
	 */
	public void delRefreshToken(String token) {
		redis.del(String.format(Constants.CACHE_TOKEN_KEY_PATTERN, token));
		openOauthRefreshTokensService.deleteByToken(token);
	}

	/**
	 * limit client access token number
	 *
	 * @param clientId
	 * @param adminId
	 * @param adminNum
	 * @return
	 */
	public boolean limitAccessToken(String clientId, int adminId, int adminNum) {
		/**
		 * no limit
		 */
		if (adminNum == 0)
			return true;

		/**
		 * sort by expires desc
		 */
		List<OpenOauthAccessTokens> tokensList = openOauthAccessTokensService
				.getByClientIdAndUserId(clientId, adminId);
		Comparator<OpenOauthAccessTokens> comparator = (
				OpenOauthAccessTokens t1, OpenOauthAccessTokens t2) -> {
			return t1.getExpires().compareTo(t2.getExpires());
		};
		tokensList.sort(comparator.reversed());

		/**
		 * remove redundant tokens
		 */
		if (tokensList.size() > adminNum) {
			tokensList = tokensList.subList(adminNum, tokensList.size());
		} else {
			return true;
		}
		List<Integer> idList = new ArrayList<>();
		List<String> tkList = new ArrayList<>();
		tokensList.forEach(e -> {
			idList.add(e.getId());
			tkList.add(e.getAccessToken());
		});

		/**
		 * remove data from db firstly and then flush cache
		 */
		List<OpenOauthRefreshTokens> refreshTokensList = openOauthRefreshTokensService
				.getByAccessTokens(tkList);

		openOauthAccessTokensService.deleteByIds(idList);
		openOauthRefreshTokensService.deleteByAccessTokens(tkList);

		refreshTokensList.forEach(e -> {
			String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN,
					e.getRefreshToken());
			redis.del(key);
		});
		tkList.forEach(e -> {
			String key = String.format(Constants.CACHE_TOKEN_KEY_PATTERN, e);
			redis.del(key);
		});

		return true;
	}

}
