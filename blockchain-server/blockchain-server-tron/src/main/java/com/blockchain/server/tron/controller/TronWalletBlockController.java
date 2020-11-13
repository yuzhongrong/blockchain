package com.blockchain.server.tron.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.wallet.WalletBaseDTO;
import com.blockchain.common.base.dto.wallet.WalletParamsDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.tron.controller.api.TronWalletBlockApi;
import com.blockchain.server.tron.service.ITronWalletService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 提现记录查询接口
 *
 * @author YH
 * @date 2019年3月9日09:50:24
 */
@Api(TronWalletBlockApi.API_NAME)
@RestController
@RequestMapping("/walletBlock")
public class TronWalletBlockController extends BaseController {
    @Autowired
    ITronWalletService walletService;


    @ApiOperation(value = TronWalletBlockApi.All.METHOD_TITLE_NAME, notes = TronWalletBlockApi.All.METHOD_TITLE_NOTE)
    @PostMapping("/all")
    public ResultDTO all(@ApiParam(TronWalletBlockApi.METHOD_API_PARAM) WalletParamsDTO paramsDTO,
                         @ApiParam(TronWalletBlockApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                         @ApiParam(TronWalletBlockApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<WalletBaseDTO> list = walletService.selectBlock(paramsDTO);
        return generatePage(list);
    }

    /*@ApiOperation(value = TronWalletBlockApi.AddGas.METHOD_TITLE_NAME, notes = TronWalletBlockApi.AddGas.METHOD_TITLE_NOTE)
    @PostMapping("/addGas")
    public ResultDTO addGas(@RequestParam(value = "userId") String userId,
                            @RequestParam(value = "walletType") String walletType,
                            @RequestParam(value = "coinName") String coinName,
                            @RequestParam(value = "amount") String amount) {
        walletService.addGas(userId,walletType,coinName,amount);
        return ResultDTO.requstSuccess();
    }*/

    @ApiOperation(value = TronWalletBlockApi.GetGas.METHOD_TITLE_NAME, notes = TronWalletBlockApi.GetGas.METHOD_TITLE_NOTE)
    @PostMapping("/getGas")
    public ResultDTO getGas(@RequestParam(value = "from") String fromAddr,
                            @RequestParam(value = "coinName") String tokenName) {
        return ResultDTO.requstSuccess(walletService.getGas(fromAddr,fromAddr, tokenName));
    }

//    @ApiOperation(value = TronWalletBlockApi.GetGasWallet.METHOD_TITLE_NAME, notes = TronWalletBlockApi.GetGasWallet.METHOD_TITLE_NOTE)
//    @GetMapping("/getGasWallet")
//    public ResultDTO getGasWallet() {
//        return ResultDTO.requstSuccess(gasWalletService.select());
//    }

    @ApiOperation(value = TronWalletBlockApi.GetGasWallet.METHOD_TITLE_NAME, notes = TronWalletBlockApi.GetGasWallet.METHOD_TITLE_NOTE)
    @GetMapping("/addGasWallet")
    public ResultDTO addGasWallet(@RequestParam(value = "addr") String addr,
                                  @RequestParam(value = "pk") String pk) {
        // gasWalletService.inser(addr,pk);
        return ResultDTO.requstSuccess();
    }

    @ApiOperation(value = TronWalletBlockApi.GoTx.METHOD_TITLE_NAME, notes = TronWalletBlockApi.GoTx.METHOD_TITLE_NOTE)
    @PostMapping("/goTx")
    public ResultDTO goTx(@RequestParam(value = "from") String fromAddr,
                          @RequestParam(value = "to") String toAddr,
                          @RequestParam(value = "coinName") String tokenName) {
        walletService.collection(fromAddr, toAddr, tokenName);
        return ResultDTO.requstSuccess();
    }


}
