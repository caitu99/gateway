package com.caitu99.gateway.oauth.service.impl;


import com.caitu99.gateway.oauth.dao.user.UserThirdInfoMapper;
import com.caitu99.gateway.oauth.model.UserThirdInfo;
import com.caitu99.gateway.oauth.service.UserThirdInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserThirdInfoServiceImpl implements UserThirdInfoService {

	@Autowired
	private UserThirdInfoMapper userThirdInfoMapper;

	@Override
	public int deleteByPrimaryKey(Long id) {
		return userThirdInfoMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(UserThirdInfo record) {
		return userThirdInfoMapper.insert(record);
	}

	@Override
	public int insertSelective(UserThirdInfo record) {
		return userThirdInfoMapper.insertSelective(record);
	}

	@Override
	public UserThirdInfo selectByPrimaryKey(Long id) {
		return userThirdInfoMapper.selectByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeySelective(UserThirdInfo record) {
		return userThirdInfoMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(UserThirdInfo record) {
		return userThirdInfoMapper.updateByPrimaryKey(record);
	}

	@Override
	public UserThirdInfo insertOrUpdate(UserThirdInfo userThirdInfo, Long userId) {
		UserThirdInfo userThirdInfoByUserId = userThirdInfoMapper
				.selectByUserId(userId);
		userThirdInfo.setUserId(userId);
		if (null != userThirdInfoByUserId) {
			userThirdInfoMapper.updateByPrimaryKey(userThirdInfo);
		} else {
			userThirdInfoMapper.insert(userThirdInfo);
		}

		return userThirdInfoMapper.selectByUserId(userId);
	}

	@Override
	public UserThirdInfo selectByUserId(Long userId) {
		return userThirdInfoMapper.selectByUserId(userId);
	}

	@Override
	public void updateByuserid(UserThirdInfo userThirdInfo, Long userId) {
		// TODO Auto-generated method stub
		userThirdInfo.setUserId(userId);
		userThirdInfoMapper.updateByuserid(userThirdInfo);
	}
}
