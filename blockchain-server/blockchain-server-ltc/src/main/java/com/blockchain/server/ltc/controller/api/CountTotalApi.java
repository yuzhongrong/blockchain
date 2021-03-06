package com.blockchain.server.ltc.controller.api;

public class CountTotalApi {
    public static final String API_NAME = "LTC统计记录控制器";

    public static class All {
        public static final String METHOD_TITLE_NAME = "查询所有统计记录的详情信息";
        public static final String METHOD_TITLE_NOTE = "查询所有统计记录的详情信息";
        public static final String METHOD_API_USERID = "用户ID";
    }

    public static class Total {
        public static final String METHOD_TITLE_NAME = "统计当前的详情信息";
        public static final String METHOD_TITLE_NOTE = "统计当前的详情信息";
        public static final String METHOD_API_USERID = "统计记录的标识";
    }
}
