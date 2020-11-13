package com.blockchain.server.user.common.enums;

/**
 * @author huangxl
 * @data 2019/2/21 20:53
 */
public enum UserEnums {
    USER_LIST_EXISTS(1100, "该用户已存在名单列表", "", "该用户已存在名单列表"),
    USER_NOT_EXISTS(1101, "不存在该用户", "There is no such user", "不存在該用戶"),
    AUTHENTICATION_APPLY_NOT_EXISTS(1102, "该用户没有初级审核申请表", "This user does not have a preliminary review application form", "該用戶沒有初級審核申請表"),
    LOGIN_PASSWORD_ERROR(1102, "用户名密码错误", "username or password is wrong", "用戶名密碼錯誤"),
    LOGIN_FORBIDDEN(1103, "你被列入黑名单，禁止登录", "You are blacklisted and login is forbidden", "你被列入黑名單，禁止登錄"),
    PASSWORD_EXIST(1127, "设置失败，你已经设置过密码了", "Setup failed, you have already set a password", "設置失敗，你已經設置過密碼了"),
    PASSWORD_NOT_MATCH(1129, "密码不匹配", "Passwords do not match", "密碼不匹配"),
    AUTHENTICATION_EXISTS(1130, "已经存在认证信息", "Authentication information already exists", "已经存在认证信息"),
    ;


    private int code;
    private String hkmsg;
    private String enMsg;
    private String cnmsg;

    UserEnums(int code, String cnmsg, String enMsg, String hkmsg) {
        this.code = code;
        this.cnmsg = cnmsg;
        this.enMsg = enMsg;
        this.hkmsg = hkmsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getHkmsg() {
        return hkmsg;
    }

    public void setHkmsg(String hkmsg) {
        this.hkmsg = hkmsg;
    }

    public String getEnMsg() {
        return enMsg;
    }

    public void setEnMsg(String enMsg) {
        this.enMsg = enMsg;
    }

    public String getCnmsg() {
        return cnmsg;
    }

    public void setCnmsg(String cnmsg) {
        this.cnmsg = cnmsg;
    }
}
