package com.blockchain.server.tron.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blockchain.common.base.util.JsonUtils;
import com.blockchain.server.tron.common.enums.TronWalletEnums;
import com.blockchain.server.tron.common.exception.TronWalletException;
import com.blockchain.server.tron.service.TronWalletUtilService;
import com.blockchain.server.tron.tron.Rpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.tron.common.utils.Base58;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.ByteUtil;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.capsule.TransactionCapsule;
import org.tron.core.services.http.JsonFormat;
import org.tron.core.services.http.Util;
import org.tron.protos.Protocol;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.apache.axis.types.NCName.isValid;

/**
 * @author Harvey Luo
 * @date 2019/6/10 15:23
 */
@Service
public class TronWalletUtilServiceImpl implements TronWalletUtilService {

    private Rpc tronRpc = new Rpc();

    // 主网地址
    @Value("${rpc_url}")
    private String RPC_URL;
    // 版本
    @Value("${version}")
    private String VERSION;
    // 账号
    @Value("${accounts}")
    private String ACCOUNTS;
    // 创建交易后缀
    @Value("${create_transaction}")
    private String CREATE_TRANSACTION;
    // 创建代币交易后缀
    @Value("${transfer_asset}")
    private String TRANSFER_ASSET;
    // 钱包
    @Value("${wallet}")
    private String WALLET;
    // 获取账号后缀
    @Value("${get_account}")
    private String GET_ACCOUNT;
    // 签名后缀
    @Value("${sign}")
    private String SIGN;
    // 广播交易后缀
    @Value("${broadcast}")
    private String BROADCAST;
    // 查询交易后缀
    @Value("${get_transaction}")
    private String GET_TRANSACTION;
    // 创建地址后缀
    @Value("${create_account_address}")
    private String CREATE_ACCOUNT_ADDRESS;
    // 创建账号后缀
    @Value("${create_account}")
    private String CREATE_ACCOUNT;
    // 触发合约
    @Value("${trigger_smart_contract}")
    private String TRIGGER_SMART_CONTRACT;


    /**
     * 查询tron出币
     *
     * @param hashId
     * @return
     */
    @Override
    public JSONObject getTransaction(String hashId) {
        String url = RPC_URL + WALLET + GET_TRANSACTION;
        Map<String, String> map = new HashMap<>();
        map.put("value", hashId);
        String param = JsonUtils.objectToJson(map);
        ResponseEntity<String> stringResponseEntity = tronRpc.postResponseString(url, param);
        try {
            if (stringResponseEntity != null) return JSONObject.parseObject(stringResponseEntity.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 生成地址
     *
     * @return
     */
    public String createAccountAddress() {
        String url = RPC_URL + WALLET + CREATE_ACCOUNT_ADDRESS;
        ResponseEntity<String> stringResponseEntity = tronRpc.postResponseString(url, null);
        return stringResponseEntity.getBody();
    }

    /**
     * 创建账号交易
     *
     * @param owner
     * @param account
     * @return
     */
    @Override
    public String createAccount(String owner, String account) {
        String url = RPC_URL + WALLET + CREATE_ACCOUNT;
        Map<String, String> map = new HashMap<>();
        map.put("owner_address", owner);
        map.put("account_address", account);
        String param = JsonUtils.objectToJson(map);
        ResponseEntity<String> stringResponseEntity = tronRpc.postResponseString(url, param);
        String body = stringResponseEntity.getBody();
        return body;
    }

    /**
     * 签名
     *
     * @param signParam
     * @return
     */
    @Override
    public String getSign(String signParam, String privateKey) throws Exception {
//        String url = RPC_URL + WALLET + SIGN;
//        ResponseEntity<String> responseEntity = tronRpc.postResponseString(url, signParam);
//        String body = responseEntity.getBody();
//        return body;
        return signTransaction(signParam, privateKey);
    }

    /**
     * 广播交易
     *
     * @param broadcastParam
     * @return
     */
    @Override
    public String getBroadcast(String broadcastParam) {
        String url = RPC_URL + WALLET + BROADCAST;
        ResponseEntity<String> responseEntity = tronRpc.postResponseString(url, broadcastParam);
        String body = responseEntity.getBody();
        return body;
    }

    /**
     * 获取账号信息
     *
     * @param hexAddr
     * @return
     */
    @Override
    public String getAccount(String hexAddr) {
        String url = RPC_URL + WALLET + GET_ACCOUNT;
        Map<String, String> map = new HashMap<>();
        map.put("address", hexAddr);
        ResponseEntity<String> stringResponseEntity = tronRpc.postResponseString(url, JsonUtils.objectToJson(map));
        if (stringResponseEntity != null) return stringResponseEntity.getBody();
        return null;
    }

    /**
     * 获取16进制地址
     *
     * @param account
     * @return
     */
    @Override
    public String getAccountByBase58(String account) {
        String url = RPC_URL + VERSION + ACCOUNTS + account;
        ResponseEntity<JSONObject> responseEntity = tronRpc.get(url);
        if (responseEntity != null) {
            JSONObject body = responseEntity.getBody();
            Boolean success = body.getBoolean("success");
            if (!success) throw new TronWalletException(TronWalletEnums.ACCOUNT_ERROR);
            JSONArray data = body.getJSONArray("data");
            if (data.size() < 1) throw new TronWalletException(TronWalletEnums.ACCOUNT_ERROR);
            return data.getJSONObject(0).getString("address");
        }
        return null;
    }

    /**
     * 交易TRX
     *
     * @param owner
     * @param privateKey
     * @param to
     * @param amount
     * @return
     */
    @Override
    public String handleTransactionTRX(String owner, String privateKey, String to, BigInteger amount) {
        // 创建交易
        String url = RPC_URL + WALLET + CREATE_TRANSACTION;
        Map<String, Object> map = new HashMap<>();
        map.put("to_address", to);
        map.put("owner_address", owner);
        map.put("amount", amount);
        String param = JsonUtils.objectToJson(map);
        ResponseEntity<String> stringResponseEntity = tronRpc.postResponseString(url, param);
        return signAndBroadcast(stringResponseEntity.getBody(), privateKey);
    }

    /**
     * 交易TRC10
     *
     * @param owner
     * @param privateKey
     * @param to
     * @param amount
     * @param tokenHexAddr
     * @return
     */
    @Override
    public String handleTransactionBTT(String owner, String privateKey, String to, BigInteger amount, String tokenHexAddr) {
        String url = RPC_URL + WALLET + TRANSFER_ASSET;
        Map<String, Object> map = new HashMap<>();
        map.put("owner_address", owner);
        map.put("to_address", to);
        map.put("amount", amount);
        map.put("asset_name", tokenHexAddr);
        String param = JsonUtils.objectToJson(map);
        ResponseEntity<String> stringResponseEntity = tronRpc.postResponseString(url, param);
        System.out.println(stringResponseEntity.getBody());
        JSONObject body = JSONObject.parseObject(stringResponseEntity.getBody());
        body.remove("visible");
        return signAndBroadcast(body.toString(), privateKey);
    }

    /**
     * 交易TRC20
     *
     * @param owner
     * @param privateKey
     * @param to
     * @param amount
     * @param tokenHexAddr
     * @return
     */
    @Override
    public String handleTransactionACE(String owner, String privateKey, String to, BigInteger amount, String tokenHexAddr) {
        String url = RPC_URL + WALLET + TRIGGER_SMART_CONTRACT;
        // 格式化参数
        String parameter = formatParameter(to) + formatParameter(amount.toString(16));
        Map<String, Object> map = new HashMap<>();
        map.put("contract_address", tokenHexAddr);
        map.put("function_selector", "transfer(address,uint256)");
        map.put("parameter", parameter);
        map.put("owner_address", owner);
        ResponseEntity<String> stringResponseEntity = tronRpc.postResponseString(url, JsonUtils.objectToJson(map));
        JSONObject data = JSONObject.parseObject(stringResponseEntity.getBody());
        JSONObject transaction = data.getJSONObject("transaction");
        transaction.remove("visible");
        transaction.remove("raw_data_hex");
        return signAndBroadcast(JsonUtils.objectToJson(transaction), privateKey);
    }

    /**
     * 获取TRC20链上余额
     *
     * @param hexAddr
     * @param tokenHexAddr
     * @return
     */
    @Override
    public String getAccountTRC20Balance(String hexAddr, String tokenHexAddr) {
        String url = RPC_URL + WALLET + TRIGGER_SMART_CONTRACT;
        Map<String, String> map = new HashMap<>();
        map.put("contract_address", tokenHexAddr);
        map.put("function_selector", "balanceOf(address)");
        hexAddr = formatParameter(hexAddr);
        map.put("parameter", hexAddr);
        map.put("owner_address", hexAddr);
        ResponseEntity<String> stringResponseEntity = tronRpc.postResponseString(url, JsonUtils.objectToJson(map));
        if (stringResponseEntity == null) throw new TronWalletException(TronWalletEnums.SERVER_IS_TOO_BUSY);
        try {
            return JSONObject.parseObject(stringResponseEntity.getBody()).getJSONArray("constant_result").getString(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 交易并广播
     *
     * @param transaction
     * @param privateKey
     * @return
     */
    private String signAndBroadcast(String transaction, String privateKey) {
        JSONObject transactionJson = JSONObject.parseObject(transaction);
        transactionJson.remove("raw_data_hex");
        String hash = transactionJson.getString("txID");
        String SignParam = JsonUtils.objectToJson(transactionJson);
        String broadcast = null;
        try {
            // 签名
            String signBody = getSign(SignParam, privateKey);
            JSONObject jsonObject = JSONObject.parseObject(signBody);
            jsonObject.remove("raw_data_hex");
            String broadcastParam = JsonUtils.objectToJson(jsonObject);
            // 广播
            broadcast = getBroadcast(broadcastParam);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Boolean result = JSONObject.parseObject(broadcast).getBoolean("result");
        if (result != null && result) return hash;
        return null;
    }

    /**
     * 离线签名
     *
     * @param trans
     * @param privateKey
     * @return
     * @throws Exception
     */
    private String signTransaction(String trans, String privateKey) throws Exception {
        String transactionStr = "{\"transaction\":" + trans + ",\"privateKey\":\"" + privateKey + "\"}";
        com.alibaba.fastjson.JSONObject input = com.alibaba.fastjson.JSONObject.parseObject(transactionStr);
        String strTransaction = input.getJSONObject("transaction").toJSONString();
        Protocol.Transaction transaction = Util.packTransaction(strTransaction);
        com.alibaba.fastjson.JSONObject jsonTransaction = com.alibaba.fastjson.JSONObject.parseObject(JsonFormat.printToString(transaction));
        input.put("transaction", jsonTransaction);
        Protocol.TransactionSign.Builder build = Protocol.TransactionSign.newBuilder();
        JsonFormat.merge(input.toJSONString(), build);
        TransactionCapsule reply = getTransactionSign(build.build());
        return Util.printTransaction(reply.getInstance());
    }

    private TransactionCapsule getTransactionSign(Protocol.TransactionSign transactionSign) {
        byte[] privateKey = transactionSign.getPrivateKey().toByteArray();
        TransactionCapsule trx = new TransactionCapsule(transactionSign.getTransaction());
        trx.sign(privateKey);
        return trx;
    }

    /**
     * post共用方法
     *
     * @param url
     * @param param
     * @return
     */
    private JSONObject getPost(String url, String param) {
        ResponseEntity<String> stringResponseEntity = tronRpc.postResponseString(url, param);
        if (stringResponseEntity == null) return null;
        return JSONObject.parseObject(stringResponseEntity.getBody());
    }

    /**
     * 补全64位参数
     *
     * @param param
     * @return
     */
    private String formatParameter(String param) {
        while (64 > param.length()) {
            StringBuilder builder = new StringBuilder();
            builder.append("0").append(param);
            param = builder.toString();
        }
        return param;
    }

    public String tryToHexAddr(String addr) {
        if (isValid(addr)) {
            addr = addr.replace("0x", "");
            String regPattern = "[0-9a-fA-F]{42}";
            if (Pattern.matches(regPattern, addr)) {
                return addr;
            }
            return decodeBase58Address(addr);

        }
        return null;
    }

    public String tryToBase58Addr(String hexAddr) {
        if (isValid(hexAddr)) {
//            String base58 = "[123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ]{34}";
            hexAddr = hexAddr.replace("0x", "");
            String regPattern = "[0-9a-fA-F]{42}";
            if (Pattern.matches(regPattern, hexAddr)) {
                return encode58Check(ByteArray.fromHexString(hexAddr));
            }
            return hexAddr;
        }
        return null;
    }

    public String decodeBase58Address(String addr) {
        if (isValid(addr) && addr.length() >= 4) {
            byte[] bytes = decode58Check(addr);
            return ByteUtil.toHexString(bytes);
        }
        return null;
    }

    private String encode58Check(byte[] input) {
        byte[] hash0 = Sha256Hash.hash(input);
        byte[] hash1 = Sha256Hash.hash(hash0);
        byte[] inputCheck = new byte[input.length + 4];
        System.arraycopy(input, 0, inputCheck, 0, input.length);
        System.arraycopy(hash1, 0, inputCheck, input.length, 4);
        return Base58.encode(inputCheck);
    }

    private byte[] decode58Check(String input) {
        byte[] decodeCheck = Base58.decode(input);
        if (decodeCheck.length <= 4) {
            return null;
        }
        byte[] decodeData = new byte[decodeCheck.length - 4];
        System.arraycopy(decodeCheck, 0, decodeData, 0, decodeData.length);
        byte[] hash0 = Sha256Hash.hash(decodeData);
        byte[] hash1 = Sha256Hash.hash(hash0);
        if (hash1[0] == decodeCheck[decodeData.length] &&
                hash1[1] == decodeCheck[decodeData.length + 1] &&
                hash1[2] == decodeCheck[decodeData.length + 2] &&
                hash1[3] == decodeCheck[decodeData.length + 3]) {
            return decodeData;
        }
        return null;
    }

}
