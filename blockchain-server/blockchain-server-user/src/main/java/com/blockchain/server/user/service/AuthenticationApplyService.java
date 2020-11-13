package com.blockchain.server.user.service;

import com.blockchain.server.user.entity.AuthenticationApply;

/**
 * @author Harvey
 * @date 2019/3/7 11:09
 * @user WIN10
 */
public interface AuthenticationApplyService {

    /**
     * 查询用户初级审核申请
     * @param userId
     * @return
     */
    AuthenticationApply selectAuthenticationApplyByUserId(String userId);

    /**
     * 查询用户状态初级申请
     * @param userId
     * @param status
     * @return
     */
    AuthenticationApply selectAuthenticationApplyByUserIdAndStatus(String userId, String status);

    /**
     * 修改用户初级审核状态
     * @param authenticationApply
     * @return
     */
    Integer updateAuthenticationApplyStatus(AuthenticationApply authenticationApply);
}
