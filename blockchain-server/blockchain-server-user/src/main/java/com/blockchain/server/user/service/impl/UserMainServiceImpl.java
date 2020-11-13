package com.blockchain.server.user.service.impl;

import com.blockchain.common.base.dto.user.UserBaseInfoDTO;
import com.blockchain.common.base.util.ExceptionPreconditionUtils;
import com.blockchain.server.user.common.enums.UserEnums;
import com.blockchain.server.user.common.exception.UserException;
import com.blockchain.server.user.mapper.UserMainMapper;
import com.blockchain.server.user.service.UserMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Harvey
 * @date 2019/3/9 12:07
 * @user WIN10
 */
@Service
public class UserMainServiceImpl implements UserMainService {

    @Autowired
    private UserMainMapper userMainMapper;

    /**
     * 查询单个用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public UserBaseInfoDTO selectUserInfo(String userId) {
        ExceptionPreconditionUtils.notEmpty(userId);
        UserBaseInfoDTO userInfoDto = userMainMapper.selectUserInfoByUserId(userId);
        if (userInfoDto == null) throw new UserException(UserEnums.USER_NOT_EXISTS);
        return userInfoDto;
    }

    /**
     * 查询多个用户信息
     *
     * @param userIds
     * @return
     */
    @Override
    public Map<String, UserBaseInfoDTO> listUserInfo(Set<String> userIds) {
        ExceptionPreconditionUtils.notEmpty(userIds);
        Map<String, UserBaseInfoDTO> map = new HashMap<>();
        for (String userId : userIds) {
            UserBaseInfoDTO userInfoDto = userMainMapper.selectUserInfoByUserId(userId);
            if (userInfoDto == null) continue;
            map.put(userId, userInfoDto);
        }
        return map;
    }

    @Override
    public UserBaseInfoDTO selectUserInfoByUserName(String userName) {
        return userMainMapper.selectUserInfoByUserName(userName);
    }
}
