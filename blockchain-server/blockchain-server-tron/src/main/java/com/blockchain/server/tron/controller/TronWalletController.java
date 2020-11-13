package com.blockchain.server.tron.controller;

import com.blockchain.common.base.constant.WalletConstant;
import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.wallet.WalletBaseDTO;
import com.blockchain.common.base.dto.wallet.WalletParamsDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.tron.controller.api.TronWalletApi;
import com.blockchain.server.tron.service.ITronWalletService;
import com.blockchain.server.tron.controller.api.TronWalletApi;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 提现记录查询接口
 *
 * @author YH
 * @date 2019年3月9日09:50:24
 */
@Api(TronWalletApi.API_NAME)
@RestController
@RequestMapping("/wallet")
public class TronWalletController extends BaseController {
    @Autowired
    ITronWalletService walletService;


    @ApiOperation(value = TronWalletApi.CCT.METHOD_TITLE_NAME, notes = TronWalletApi.CCT.METHOD_TITLE_NOTE)
    @PostMapping("/cct")
    public ResultDTO cct(@ApiParam(TronWalletApi.METHOD_API_PARAM) WalletParamsDTO paramsDTO,
                         @ApiParam(TronWalletApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                         @ApiParam(TronWalletApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        paramsDTO.setWalletType(WalletConstant.WALLET_CCT);
        List<WalletBaseDTO> list = walletService.select(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = TronWalletApi.WALLET.METHOD_TITLE_NAME, notes = TronWalletApi.WALLET.METHOD_TITLE_NOTE)
    @PostMapping("/wallet")
    public ResultDTO wallet(@ApiParam(TronWalletApi.METHOD_API_PARAM) WalletParamsDTO paramsDTO,
                         @ApiParam(TronWalletApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                         @ApiParam(TronWalletApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
//        paramsDTO.setWalletType(WalletConstant.WALLET_WALLET);
        List<WalletBaseDTO> list = walletService.select(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = TronWalletApi.All.METHOD_TITLE_NAME, notes = TronWalletApi.All.METHOD_TITLE_NOTE)
    @PostMapping("/all")
    public ResultDTO all(@ApiParam(TronWalletApi.METHOD_API_PARAM) WalletParamsDTO paramsDTO,
                         @ApiParam(TronWalletApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                         @ApiParam(TronWalletApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<WalletBaseDTO> list = walletService.select(paramsDTO);
        return generatePage(list);
    }
    @ApiOperation(value = TronWalletApi.Add.METHOD_TITLE_NAME, notes = TronWalletApi.Add.METHOD_TITLE_NOTE)
    @PostMapping("/add")
    public ResultDTO add(@ApiParam(TronWalletApi.METHOD_API_ADDR) @RequestParam(value = "addr", required = false) String addr,
                         @ApiParam(TronWalletApi.METHOD_API_COINNAME) @RequestParam(value = "coinName", required = false) String coinName,
                         @ApiParam(TronWalletApi.METHOD_API_FREEBLANCE) @RequestParam(value = "freeBlance", required = false)String freeBlance,
                         @ApiParam(TronWalletApi.METHOD_API_FREEZEBLANCE) @RequestParam(value = "freezeBlance", required = false)String freezeBlance) {
         walletService.updateBlance(addr,coinName,freeBlance,freezeBlance, WalletConstant.AMOUNT_ADD);
         return ResultDTO.requstSuccess();
    }
    @ApiOperation(value = TronWalletApi.Sud.METHOD_TITLE_NAME, notes = TronWalletApi.Sud.METHOD_TITLE_NOTE)
    @PostMapping("/sud")
    public ResultDTO sud(@ApiParam(TronWalletApi.METHOD_API_ADDR) @RequestParam(value = "addr", required = false) String addr,
                         @ApiParam(TronWalletApi.METHOD_API_COINNAME) @RequestParam(value = "coinName", required = false) String coinName,
                         @ApiParam(TronWalletApi.METHOD_API_FREEBLANCE) @RequestParam(value = "freeBlance", required = false)String freeBlance,
                         @ApiParam(TronWalletApi.METHOD_API_FREEZEBLANCE) @RequestParam(value = "freezeBlance", required = false)String freezeBlance) {
        walletService.updateBlance(addr,coinName,freeBlance,freezeBlance, WalletConstant.AMOUNT_SUD);
        return ResultDTO.requstSuccess();
    }


}
