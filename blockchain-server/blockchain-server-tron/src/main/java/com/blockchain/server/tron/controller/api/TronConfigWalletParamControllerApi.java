package com.blockchain.server.tron.controller.api;

/**
 * @author: Liusd
 * @create: 2019-03-22 11:45
 **/
public class TronConfigWalletParamControllerApi {

    public static final String CONTROLLER_API = "TRON提现手续费控制器";

    public static class TronConfigWalletParamList {
        public static final String METHOD_TITLE_NAME = "TRON提现手续费列表";
        public static final String METHOD_TITLE_NOTE = "TRON提现手续费列表";
    }

    public class UpdateConfigWallet {
        public static final String METHOD_TITLE_NAME = "TRON提现手续费列表";
        public static final String METHOD_TITLE_NOTE = "TRON提现手续费列表";
        public static final String METHOD_API_GASDTO = "Gas对象";
        public static final String METHOD_API_ID = "记录id";
    }

    public class GetCollectionWallet {
        public static final String METHOD_TITLE_NAME = "根据币种类型获取归集钱包";
        public static final String METHOD_TITLE_NOTE = "根据币种类型获取归集钱包";
        public static final String METHOD_API_TOKEN_SYMBOL = "币种类型";
    }

    public class UpdateCollectionWallet {
        public static final String METHOD_TITLE_NAME = "根据币种类型获取归集钱包";
        public static final String METHOD_TITLE_NOTE = "根据币种类型获取归集钱包";
        public static final String METHOD_API_ID = "数据Id";
        public static final String METHOD_API_ADDR = "归集地址";
        public static final String METHOD_API_DESCRIBE = "描述";
        public static final String METHOD_API_STATUS = "状态";
    }
}
