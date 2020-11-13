package com.blockchain.server.eth.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.wallet.WalletParamsDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.eth.controller.api.EthPrivateBalanceApi;
import com.blockchain.server.eth.entity.EthPrivateBalance;
import com.blockchain.server.eth.service.IEthPrivateBalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 提现记录查询接口
 *
 * @author YH
 * @date 2019年3月9日09:50:24
 */
@Api(EthPrivateBalanceApi.API_NAME)
@RestController
@RequestMapping("/privateBalance")
public class EthPrivateBalanceController extends BaseController {
    @Autowired
    private IEthPrivateBalanceService privateBalanceService;


    @ApiOperation(value = EthPrivateBalanceApi.PrivateBalance.METHOD_TITLE_NAME, notes = EthPrivateBalanceApi.PrivateBalance.METHOD_TITLE_NOTE)
    @PostMapping("/privateBalance")
    public ResultDTO privateBalance(EthPrivateBalance privateBalance) {
        privateBalanceService.insert(privateBalance);
        return ResultDTO.requstSuccess();
    }

    @ApiOperation(value = EthPrivateBalanceApi.List.METHOD_TITLE_NAME, notes = EthPrivateBalanceApi.List.METHOD_TITLE_NOTE)
    @PostMapping("/list")
    public ResultDTO list(@ApiParam(EthPrivateBalanceApi.METHOD_API_PARAM) WalletParamsDTO paramsDTO,
                          @ApiParam(EthPrivateBalanceApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                          @ApiParam(EthPrivateBalanceApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return generatePage(privateBalanceService.list(paramsDTO, pageNum, pageSize));
    }

    @ApiOperation(value = EthPrivateBalanceApi.Deduct.METHOD_TITLE_NAME, notes = EthPrivateBalanceApi.Deduct.METHOD_TITLE_NOTE)
    @PostMapping("/deduct")
    public ResultDTO deduct(EthPrivateBalance privateBalance) {
        privateBalanceService.deduct(privateBalance);
        return ResultDTO.requstSuccess();
    }

}
