package com.blockchain.server.tron.common.constants;

/**
 * @author Harvey Luo
 * @date 2019/7/1 9:33
 */
public class TronConstant {
    public static final String TRX_TOKEN_SYMBOL = "TRX";
    public static final String BTT_TOKEN_SYMBOL = "BTT";
    public static final String TBC_TOKEN_SYMBOL = "TBC";
    public static final String ACE_TOKEN_SYMBOL = "ACE";

    public static final String USABLE = "1";

    public static class TronTokenAddr {
        public static final String TRX_TOKEN_ADDR = "0";
    }

    public static class WalletOut {
        public static final String STATUS_USEABLE = "1";
    }

    public static class TransferType {
        public static final String TRANSFER_OUT = "OUT";
    }

    public static class TransferStatus {
        public final static int OUT_LOAD1 = 2; // 待初审提币
        public final static int OUT_LOAD2 = 3; // 待复审提币
        public final static int OUT_LOAD3 = 4; // 待出币
        public final static int OUT_LOAD4 = 5; // 已出币（打包中）
        public final static int OUT_ERROR = 6; // 出币失败
        public final static int OUT_SUCCESS = 1; // 出币成功
        public final static int OUT_NOTPASS = 7; // 审核不通过
    }

    public static class WalletCreateStatus {
        public final static String USEABLE = "1"; // 可用
        public final static String DISABLE = "0"; // 不可用
    }

    /**
     * 配置信息表资金归集查询字段
     */
    public static class ConfigWallet {
        public final static String MODULE_TYPE = "tron";
        public final static String PARAM_NAME = "tron_collection_";
    }

}
