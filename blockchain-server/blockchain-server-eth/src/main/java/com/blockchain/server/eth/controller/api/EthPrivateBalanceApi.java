package com.blockchain.server.eth.controller.api;

public class EthPrivateBalanceApi {
    public static final String API_NAME = "私募资金接口控制器";

    public static final String METHOD_API_PARAM = "条件参数";
    public static final String METHOD_API_PAGESIZE = "分页每页显示条数";
    public static final String METHOD_API_PAGENUM = "分页页码";
    public static final String METHOD_API_ADDR = "钱包地址";
    public static final String METHOD_API_COINNAME = "币种名称";
    public static final String METHOD_API_FREEBLANCE = "可用余额";
    public static final String METHOD_API_FREEZEBLANCE = "冻结余额";

    public static class PrivateBalance {
        public static final String METHOD_TITLE_NAME = "私募资金";
        public static final String METHOD_TITLE_NOTE = "私募资金";
    }

    public static class List {
        public static final String METHOD_TITLE_NAME = "查询私募资金列表";
        public static final String METHOD_TITLE_NOTE = "查询私募资金列表";
    }

    public static class Deduct {
        public static final String METHOD_TITLE_NAME = "扣减私募资金";
        public static final String METHOD_TITLE_NOTE = "扣减私募资金";
    }

}
