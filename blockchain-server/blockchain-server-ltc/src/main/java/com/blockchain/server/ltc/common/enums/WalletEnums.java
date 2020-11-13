package com.blockchain.server.ltc.common.enums;

public enum WalletEnums {
    SUCCESS(200, "请求成功", "Request success", ""),
    NO_LOGIN(201, "未登录", "No login", ""),
    NOT_PERFECT(500, "接口未完善", "Not perfect", ""),
    NOT_WALLETPASSWORD(800, "你还没有设置资金密码", "You have not set the fund password", "妳還沒有設置資金密碼"),
    //
    ADDRESS_ERROR(13000, "请输入有效的地址", "Please enter a valid address.", "請輸入有效的地址"),
    AMOUNT_NULL(13000, "请输入数量", "Please input quantity.", "請輸入數量"),
    SENDTOADDRESS_ERROR(13000, "交易失败", "Transaction failure.", "交易失敗"),
    GET_NEW_ADDRESS_ERROR(13000, "生成地址失败", "Request success", "生成地址失敗"),
    LIST_UNSPENT_ERROR(13000, "获取UTXO失败", "Failed to get UTXO", "獲取UTXO失敗"),
    CREATE_WALLET_ERROR(13000, "创建钱包失败", "Failed to create wallet.", "創建錢包失敗"),

    // 关于为空的提示
    NULL_WALLETS(12500, "没有找到钱包信息", "No wallet information found", "沒有找到錢包資訊"),
    NULL_ERROR(12500, "没有接收到参数", "No arguments were received", "沒有接收到參數"),
    NULL_USEROPENID(12500, "用户标识不能为空", "The user id cannot be empty", "用戶標識不能為空"),
    NULL_ADDR(12500, "钱包地址不能为空", "The wallet address cannot be empty", "錢包地址不能為空"),
    NULL_TOKENADDR(12500, "货币地址不能为空", "The currency address cannot be empty", "貨幣地址不能為空"),
    NULL_TOKENALL(12500, "统计结果未找到", "The statistical results were not found", "統計結果未找到"),
    NULL_TOTALID(12500, "记录标识不能为空", "Record id cannot be empty", "記錄標識不能為空"),
    NULL_WALLETTYPE(12500, "钱包类型不能为空", "The wallet type cannot be empty", "錢包類型不能為空"),
    NULL_PASSWORD(12500, "钱包密码不能为空", "The wallet password cannot be empty", "錢包密碼不能為空"),
    NULL_AMOUNT(12500, "转账金额不能为空", "The transfer amount cannot be empty", "轉賬金額不能為空"),
    NULL_TXIN(12500, "充值记录不能为空", "The recharge record cannot be empty", "充值記錄不能為空"),
    NULL_OUT_TOADDR(12500, "提现的钱包地址不能为空", "The withdrawal address cannot be empty", "提現地址不能為空"),
    NULL_FREEBLANCE(12500, "可用余额不能为空", "The available balance cannot be empty", "可用余額不能為空"),
    NULL_FREEZEBLANCE(12500, "冻结余额不能为空", "The frozen balance cannot be empty", "凍結余額不能為空"),
    NULL_TXID(12500, "操作失败,记录标识不能为空", "The record id cannot be empty", "操作失敗,記錄標識不能為空"),
    NULL_TX(12500, "操作失败，该记录不存在", "The record does not exist", "操作失敗，該記錄不存在"),
    // 关于查询不到的提示
    INEXISTENCE_TOKENADDR(12600, "该币种暂不支持此操作", "This operation is not currently supported in this currency",
            "該幣種暫不支持此操作"),
    IEXIST_TOKENADDRS(12600, "该币种存在多个", "There are multiple versions of this currency",
            "該幣種存在多個"),
    INEXISTENCE_WALLET(12600, "钱包信息未找到", "Wallet information not found", "錢包信息未找到"),
    INEXISTENCE_WALLETTYPE(12600, "该钱包类型不存在", "The wallet type does not exist", "該錢包類型不存在"),
    INEXISTENCE_TX(12500, "该记录未找到", "The record was not found", "該記錄未找到"),
    INEXISTENCE_BLOCKTX(12500,"该币种还没存在发币账户","There is no issuing account in this currency","該幣種還沒存在發幣賬戶"),

    // 关于校验错误的提示
    DATA_EXCEPTION_ERROR(12700, "操作失败，数据异常", "Operation failed, data abnormal", "操作失败，数据异常"),
    OUT_TOADDR_ERROR(12700, "操作失败，提现钱包的钱包地址格式有误", "Operation failed, the wallet address format of the withdrawal " +
            "wallet is wrong", "操作失敗，提現錢包的錢包地址格式有誤"),
    PASSWORD_ERROR(12700, "操作失败，密码错误", "Operation failed, password error", "操作失敗，密碼錯誤"),
    INITWALLERT_ERROR(12700, "操作失败，钱包不能重复初始化", "Operation failed. Wallet cannot be initialized again",
            "操作失敗，錢包不能重復初始化"),
    EQWALLERTTYPE_ERROR(12700, "操作失败，不支持同钱包类型划转", "Operation failed, do not support the same wallet type transfer",
            "操作失敗，不支持同錢包類型劃轉"),
    NUMBER_TYPE_ERROR(12700, "操作失败，转账金额格式有误", "Operation failed, the transfer amount format is wrong", "操作失敗，轉賬金額格式有誤"),
    NUMBER_COUNT_ERROR(12700, "操作失败，转账金额必须大于0", "Operation failed, transfer amount must be greater than 0",
            "操作失敗，轉賬金額必須大於0"),
    TRANSFER_ERROR(12700, "交易失败", "Failed to transact.", "交易失敗"),
    NUMBER_INSUFFICIENT_ERROR(12700, "操作失败，该数字货币可用余额不足", "Operation failed, the available balance of the digital " +
            "currency is insufficient", "操作失敗，該數字貨幣可用余額不足"),
    NUMBER_INSUFFICIENTZE_ERROR(12700, "操作失败，该数字货币冻结余额不足", "Operation failed, the digital currency freeze balance is " +
            "insufficient", "操作失敗，該數字貨幣凍結余額不足"),
    NUMBER_INSUFFICIENT_GAS_ERROR(12700, "操作失败，提现手续费不足", "Operation failure, insufficient withdrawal charge",
            "操作失敗，提現手續費不足"),
    NUMBER_MINWDAMOUNT_ERROR(12700, "提现余额不得小于最小提现额度", "The withdrawal balance shall not be less than the minimum withdrawal limit",
            "提現余額不得小於最小提現額度"),
    // 数据的增删改报错的提示
    INSERT_INITWALLERT(12800, "钱包初始化失败，请稍后重试", "Wallet initialization failed. Please try again later", "錢包初始化失敗，請稍後重試"),
    INSERT_ADDWALLERT(12800, "该币种钱包已经创建", "The currency wallet has been created", "該幣種錢包已經創建"),
    // 关于连接失败，服务器繁忙的提示
    SERVER_IS_TOO_BUSY(15000, "服务器繁忙,请稍后重试", "The server is busy, please try again later", "服務器繁忙,請稍後重試"),

    ;
    private int code;
    private String hkmsg;
    private String enMsg;
    private String cnmsg;

    WalletEnums(int code, String cnmsg, String enMsg, String hkmsg) {
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
