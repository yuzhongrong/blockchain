package com.blockchain.server.cct.controller.api;

public class ConfigApi {
    public static final String CONFIG_API = "配置信息控制器";

    public static class listConfig {
        public static final String METHOD_TITLE_NAME = "查询配置列表";
        public static final String METHOD_TITLE_NOTE = "查询配置列表";
    }

    public static class updateConfig {
        public static final String METHOD_TITLE_NAME = "更新配置信息";
        public static final String METHOD_TITLE_NOTE = "更新配置信息";
        public static final String METHOD_API_TAG = "配置说明";
        public static final String METHOD_API_KEY = "配置名";
        public static final String METHOD_API_VAL = "配置值";
        public static final String METHOD_API_STATUS = "配置状态";
    }
}
