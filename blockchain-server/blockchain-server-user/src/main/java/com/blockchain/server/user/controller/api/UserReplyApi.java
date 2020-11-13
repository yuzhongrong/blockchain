package com.blockchain.server.user.controller.api;

public class UserReplyApi {

    public static final String API = "用户建议反馈回复控制器";

    public static final String PAGE_NUM = "当前页数";
    public static final String PAGE_SIZE = "当前页面内容数";

    public static class List {
        public static final String METHOD_TITLE_NAME = "获取建议反馈回复列表";
        public static final String METHOD_TITLE_NOTE = "获取建议反馈回复列表";
        public static final String USER_NAME = "账户";
    }

    public static class Insert {
        public static final String METHOD_TITLE_NAME = "新增回复";
        public static final String METHOD_TITLE_NOTE = "新增回复";
    }

}
