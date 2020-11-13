package com.blockchain.server.btc.rpc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blockchain.server.btc.common.enums.BtcWalletEnums;
import com.blockchain.server.btc.common.exception.BtcWalletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hugq
 * @date 2019/2/16 15:54
 */
@Component
public class BtcUtils {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private BtcOmniRpcClient client;

    /**
     * 生成新的钱包地址
     *
     * @return
     * @throws Exception
     */
    public String getNewAddress() throws Exception {
        try {
            return client.getClient().invoke("getnewaddress", null, String.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.getNewAddress(String):{} ===", e.getMessage(), e);
            throw new BtcWalletException(BtcWalletEnums.GET_NEW_ADDRESS_ERROR);
        }
    }

    /**
     * 获取地址的未花费列表
     *
     * @param address 地址
     * @return
     * @throws Exception
     */
    public JSONArray listUnspent(String address) throws Exception {
        try {
            return client.getClient().invoke("listunspent", new Object[]{1, Integer.MAX_VALUE, new Object[]{address}}, JSONArray.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.listUnspent(String):{} ===", e.getMessage(), e);
            throw new BtcWalletException(BtcWalletEnums.LIST_UNSPENT_ERROR);
        }
    }

    /**
     * 验证是否有效地址
     *
     * @param address 地址
     * @return
     * @throws Exception
     */
    public JSONObject validateAddress(String address) throws Exception {
        try {
            return client.getClient().invoke("validateaddress", new Object[]{address}, JSONObject.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.validateAddress(String):{} ===", e.getMessage(), e);
            throw new BtcWalletException(BtcWalletEnums.ADDRESS_ERROR);
        }
    }

    /**
     * 获取指定账户/整个钱包BTC余额
     *
     * @param account 账户名，为空则获取整个钱包余额
     * @return
     * @throws Exception
     */
    public double getBalance(String account) throws Exception {
        double balance;
        try {
            if (!StringUtils.isEmpty(account)) {
                balance = client.getClient().invoke("getbalance", new Object[]{account}, Double.class);
            } else {
                balance = client.getClient().invoke("getbalance", new Object[]{}, Double.class);
            }
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.getBalance(String...):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: account=%s", account));
        }
        return balance;
    }

    /**
     * 从钱包内向指定的地址发送指定数量的比特币，简单交易，钱包内随机指定或创建地址作为发送地址、找零地址
     *
     * @param toAddress 地址
     * @param amount    数量
     * @return 交易ID
     * @throws Exception
     */
    public String sendToAddress(String toAddress, double amount) throws Exception {
        try {
            return client.getClient().invoke("sendtoaddress", new Object[]{toAddress, amount}, String.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.sendToAddress({}, {}):{} ===", toAddress, amount, e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    public Object sendMany(String fromaccount, Object target) throws Exception {
        try {
            String txId = client.getClient().invoke("sendmany", new Object[]{fromaccount, target}, Object.class).toString();
            return txId;
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.signSendToAddress(String, double):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: fromaccount=%s,target=%s", fromaccount, target));
        }
    }

    public Object getRawTransaction(String txId, int verbose) throws Exception {
        try {
            return client.getClient().invoke("getrawtransaction", new Object[]{txId, verbose}, Object.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.getRawTransaction(String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: txId=%s", txId));
        }
    }

    /**
     * 获取指定钱包内交易的详细信息
     *
     * @param txId 交易ID
     * @return
     * @throws Exception
     */
    public JSONObject getTransaction(String txId) throws Exception {
        try {
            return client.getClient().invoke("gettransaction", new Object[]{txId}, JSONObject.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.getTransaction(String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: txId=%s", txId));
        }
    }

    public Object setAccount(String address, String account) throws Exception {
        try {
            return client.getClient().invoke("setaccount", new Object[]{address, account}, Object.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.setAccount(String, String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: address=%s,account=%s", address, account));
        }
    }

    public Object listReceivedByAddress(int minconf) throws Exception {
        try {
            if (1 != minconf) {
                return client.getClient().invoke("listreceivedbyaccount", new Object[]{minconf}, Object.class);
            } else {
                return client.getClient().invoke("listreceivedbyaccount", new Object[]{}, Object.class);
            }
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.listReceivedByAddress(int minconf):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: minconf=%s", minconf));
        }
    }

    public Object settxfee(double account) throws Exception {
        try {
            return client.getClient().invoke("settxfee", new Object[]{account}, Object.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.settxfee(double):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: account=%s", account));
        }
    }

    public Object encryptwallet(String passphrase) throws Exception {
        try {
            return client.getClient().invoke("encryptwallet", new Object[]{passphrase}, Object.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.encryptwallet(String) ===", e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    public Object walletpassphrase(String passphrase, int timeout) throws Exception {
        try {
            return client.getClient().invoke("walletpassphrase", new Object[]{passphrase, timeout}, Object.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.walletpassphrase(String, int):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: passphrase=%s,timeout=%s", passphrase, timeout));
        }
    }

    /**
     * 创建一个未签名的裸交易
     *
     * @param utxo      交易输入数组,成员对象的结构: UTXO的交易id, UTXO的输出序号
     * @param toAddress 交易输出对象，键为地址
     * @param amonut    交易输出对象，值为金额
     * @return 返回生成的未签名交易的序列化字符串
     * @throws Exception
     */
    public String createRawTransaction(Object utxo, String toAddress, double amonut) throws Exception {
        Map<String, Double> outputs = new HashMap();
        outputs.put(toAddress, amonut);
        try {
            return client.getClient().invoke("createrawtransaction", new Object[]{utxo, outputs}, String.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.createRawTransaction(Object):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: inputs=%s,outputs=%s", utxo.toString(), outputs.toString()));
        }
    }

    /**
     * 为裸交易交易增加输入，主要用于指定找零地址
     *
     * @param hexString 裸交易字符串
     * @param address   额外的可选项，结构: changeAddress：找零地址，如果不设置则自动从地址池中选择一个新地址
     * @return 返回更新后的交易信息
     * @throws Exception
     */
    public String fundRawTransaction(String hexString, String address) throws Exception {
        Map<String, String> outputs = new HashMap();
        outputs.put("changeAddress", address);
        try {
            JSONObject jsonObject = client.getClient().invoke("fundrawtransaction", new Object[]{hexString, outputs}, JSONObject.class);
            return jsonObject.getString("hex");
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.fundRawTransaction(Object):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: hexString=%s,outputs=%s", hexString, outputs.toString()));
        }
    }

    /**
     * 签名交易
     *
     * @param hexString 交易串
     * @return
     * @throws Exception
     */
    public String signRawTransaction(String hexString) throws Exception {
        try {
            JSONObject jsonObject = client.getClient().invoke("signrawtransaction", new Object[]{hexString}, JSONObject.class);
            return jsonObject.getString("hex");
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.signrawTransaction(String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: hexstring=%s", hexString));
        }
    }

    /**
     * 调用验证指定交易并将其广播到P2P网络中
     *
     * @param hexHash 序列化的交易码流
     * @return
     * @throws Exception
     */
    public String sendRawTransaction(String hexHash) throws Exception {
        try {
            return client.getClient().invoke("sendrawtransaction", new Object[]{hexHash}, String.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.sendRawTransaction(String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: hexHash=%s", hexHash));
        }
    }

    public Object decoderawtransaction(String hex) throws Exception {
        try {
            return client.getClient().invoke("decoderawtransaction", new Object[]{hex}, Object.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.decoderawtransaction(String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: hex=%s", hex));
        }
    }

    /**
     * 通过区块高度获取该区块hash
     *
     * @param index
     * @return
     * @throws Exception
     */
    public String getBlockHash(int index) throws Exception {
        try {
            return client.getClient().invoke("getblockhash", new Object[]{index}, String.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.getBlockHash(int):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: index=%s", index));
        }
    }

    /**
     * 获取最新区块高度
     *
     * @return
     * @throws Exception
     */
    public Integer getBlockCount() throws Exception {
        try {
            return client.getClient().invoke("getblockcount", null, Integer.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.getBlockHeader():{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 获取区块内交易id列表集合
     *
     * @param blockHash 区块hash
     * @return
     * @throws Exception
     */
    public ArrayList<String> getblock(String blockHash) throws Exception {
        try {
            JSONObject blockJson = client.getClient().invoke("getblock", new Object[]{blockHash}, JSONObject.class);
            return (ArrayList<String>) blockJson.get("tx");
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.getblock(String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: blockHash=%s", blockHash));
        }
    }

    /**
     * 通过地址获取私钥
     *
     * @param address 地址
     * @return
     * @throws Exception
     */
    public String getPrivateKeyByAddress(String address) throws Exception {
        try {
            return client.getClient().invoke("dumpprivkey", new Object[]{address}, String.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.getPrivateKeyByAddress(String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: blockHash=%s", address));
        }
    }

    /**
     * 通过区块hash获取区块高度
     *
     * @param headerHash 区块hash
     * @return
     * @throws Exception
     */
    public int getBlockHeader(String headerHash) throws Exception {
        try {
            JSONObject blockObject = client.getClient().invoke("getblockheader", new Object[]{headerHash}, JSONObject.class);
            return blockObject.getInteger("height");
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.getBlockHeader(String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: headerHash=%s", headerHash));
        }
    }

    /**
     * 调用返回指定区块之后发生的与钱包相关的所有交易
     *
     * @param headerHash 区块hash
     * @return
     * @throws Exception
     */
    public JSONArray listSinceBlock(String headerHash) throws Exception {
        try {
            JSONObject blockObject = client.getClient().invoke("listsinceblock", new Object[]{headerHash}, JSONObject.class);
            return blockObject.getJSONArray("transactions");
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.listSinceBlock(String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: headerHash=%s", headerHash));
        }
    }

    /**
     * 用裸交易方式进行转账，可以指定找零地址，没有指定则默认输出地址
     *
     * @param fromAddress   输出地址
     * @param toAddress     收入地址
     * @param amonut        数量
     * @param changeAddress 找零地址
     * @return
     * @throws Exception
     */
    public String sendWithRaw(String fromAddress, String toAddress, double amonut, String changeAddress) throws Exception {
        String txId = "";
        //如果没指定找零地址则默认输出地址
        if (StringUtils.isEmpty(changeAddress)) {
            changeAddress = fromAddress;
        }
        try {
            //获取输出地址的UTXO
            JSONArray UTXOArr = listUnspent(fromAddress);
            //创建裸交易
            String hexString = createRawTransaction(UTXOArr, toAddress, amonut);
            //指定找零地址
            String fundHexString = fundRawTransaction(hexString, changeAddress);
            //签名交易
            String signHex = signRawTransaction(fundHexString);
            //广播交易
            txId = sendRawTransaction(signHex);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.sendWithRaw(String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: fromAddress=%s", fromAddress));
        }
        return txId;
    }


    /**
     * 调用返回节点钱包中未确认总余额
     *
     * @return
     * @throws Exception
     */
    public double getUnconfirmedBalance() throws Exception {
        try {
            return client.getClient().invoke("getunconfirmedbalance", null, Double.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.getUnconfirmedBalance():{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 调用按地址分组显示余额信息
     *
     * @return
     * @throws Exception
     */
    public JSONArray listAddressGroupings() throws Exception {
        try {
            return client.getClient().invoke("listaddressgroupings", null, JSONArray.class);
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.listaddressgroupings():{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 通过地址获取BTC余额
     *
     * @param addr 地址
     * @return
     * @throws Exception
     */
    public BigDecimal getBalanceByAddr(String addr) throws Exception {
        try {
            JSONArray jsonArray = listAddressGroupings();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray groupArr = jsonArray.getJSONArray(i);
                for (int j = 0; j < groupArr.size(); j++) {
                    JSONArray addrArr = groupArr.getJSONArray(j);
                    String addrThis = addrArr.getString(0);
                    if (addr.equals(addrThis)) {
                        return addrArr.getBigDecimal(1);
                    }
                }
            }
            return BigDecimal.ZERO;

//            BigDecimal balance = BigDecimal.ZERO;
//            JSONArray jsonArray = listUnspent(addr);
//            int len = jsonArray.size();
//            for (int i = 0; i < len; i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                balance = balance.add(new BigDecimal(jsonObject.getDoubleValue("amount")));
//            }
//            balance = balance.setScale(8,BigDecimal.ROUND_DOWN);
//            return balance;
        } catch (Throwable e) {
            LOG.info("=== BtcUtils.getBalanceByAddr(String):{} ===", e.getMessage(), e);
            throw new Exception(e.getMessage() + String.format("[params]: addr=%s", addr));
        }
    }

}
