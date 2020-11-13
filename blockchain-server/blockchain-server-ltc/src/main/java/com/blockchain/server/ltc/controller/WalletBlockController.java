package com.blockchain.server.ltc.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.wallet.WalletBaseDTO;
import com.blockchain.common.base.dto.wallet.WalletParamsDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.ltc.controller.api.WalletBlockApi;
import com.blockchain.server.ltc.service.IWalletService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author: Liusd
 * @create: 2019-03-23 10:54
 **/
@Api(WalletBlockApi.API_NAME)
@RestController
@RequestMapping("/walletBlock")
public class WalletBlockController extends BaseController {

    @Autowired
    IWalletService walletService;


    @ApiOperation(value = WalletBlockApi.All.METHOD_TITLE_NAME, notes = WalletBlockApi.All.METHOD_TITLE_NOTE)
    @PostMapping("/all")
    public ResultDTO all(@ApiParam(WalletBlockApi.METHOD_API_PARAM) WalletParamsDTO paramsDTO,
                         @ApiParam(WalletBlockApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                         @ApiParam(WalletBlockApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<WalletBaseDTO> list = walletService.selectBlock(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = WalletBlockApi.TotalBalance.METHOD_TITLE_NAME, notes = WalletBlockApi.TotalBalance.METHOD_TITLE_NOTE)
    @GetMapping("/totalBalance")
    public ResultDTO totalBalance() {
        Map<String, String> map = walletService.totalBalance();
        return ResultDTO.requstSuccess(map);
    }

    @ApiOperation(value = WalletBlockApi.GoCoinTx.METHOD_TITLE_NAME, notes = WalletBlockApi.GoCoinTx.METHOD_TITLE_NOTE)
    @PostMapping("/goCoinTx")
    public ResultDTO goCoinTx(@RequestParam(value = "toAddr") String toAddr, @RequestParam(value = "amount") Double amount) {
        walletService.goCoinTx(toAddr, amount);
        return ResultDTO.requstSuccess();
    }

    @ApiOperation(value = WalletBlockApi.GetGasWallet.METHOD_TITLE_NAME, notes = WalletBlockApi.GetGasWallet.METHOD_TITLE_NOTE)
    @GetMapping("/getGasWallet")
    public ResultDTO getGasWallet() {
        return ResultDTO.requstSuccess(walletService.getGasWallet());
    }

    @GetMapping("/getBalance")
    public ResultDTO getBalance(@RequestParam(value = "addr") String addr) {
        return ResultDTO.requstSuccess(walletService.getBalance(addr));
    }

//    @ApiOperation(value = WalletBlockApi.GetGas.METHOD_TITLE_NAME, notes = WalletBlockApi.GetGas.METHOD_TITLE_NOTE)
//    @PostMapping("/getGas")
//    public ResultDTO getGas(@RequestParam(value = "from") String fromAddr,
//                            @RequestParam(value = "coinName") String tokenName) {
//        return ResultDTO.requstSuccess(walletService.getGas(fromAddr,fromAddr, tokenName));
//    }

//    @ApiOperation(value = WalletBlockApi.GoTx.METHOD_TITLE_NAME, notes = WalletBlockApi.GoTx.METHOD_TITLE_NOTE)
//    @PostMapping("/goTx")
//    public ResultDTO goTx(@RequestParam(value = "from") String fromAddr,
//                          @RequestParam(value = "to") String toAddr ) {
//        walletService.rpcTx(fromAddr,toAddr);
//        return ResultDTO.requstSuccess();
//    }

}
