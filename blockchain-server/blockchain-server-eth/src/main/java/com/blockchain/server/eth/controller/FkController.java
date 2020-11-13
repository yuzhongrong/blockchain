package com.blockchain.server.eth.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.wallet.WalletTxDTO;
import com.blockchain.common.base.dto.wallet.WalletTxParamsDTO;
import com.blockchain.server.eth.common.constants.tx.OutTxConstants;
import com.blockchain.server.eth.controller.api.EthWalletOutTxApi;
import com.blockchain.server.eth.web3j.IFkWeb3j;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fk")
public class FkController {
    @Autowired
    IFkWeb3j fkWeb3j;

    @PostMapping("/destruction")
    public ResultDTO destruction() {
        fkWeb3j.destruction();
        return ResultDTO.requstSuccess();
    }
}
