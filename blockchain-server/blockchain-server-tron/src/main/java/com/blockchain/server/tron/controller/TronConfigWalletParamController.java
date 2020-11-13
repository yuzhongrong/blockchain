package com.blockchain.server.tron.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.base.security.RequiresPermissions;
import com.blockchain.server.tron.controller.api.TronConfigWalletParamControllerApi;
import com.blockchain.server.tron.service.TronConfigWalletParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @author: Liusd
 * @create: 2019-03-22 11:43
 **/
@Api(TronConfigWalletParamControllerApi.CONTROLLER_API)
@RestController
@RequestMapping("/gas")
public class TronConfigWalletParamController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(TronConfigWalletParamController.class);

    @Autowired
    private TronConfigWalletParamService tronConfigWalletParamService;

    /**
     * @Description: 提现手续费列表
     * @Param: []
     * @return: com.blockchain.common.base.dto.ResultDTO
     * @Author: Liu.sd
     * @Date: 2019/3/22
     */
    @GetMapping("/tronConfigWalletParamList")
    @ApiOperation(value = TronConfigWalletParamControllerApi.TronConfigWalletParamList.METHOD_TITLE_NAME, notes = TronConfigWalletParamControllerApi.TronConfigWalletParamList.METHOD_TITLE_NOTE)
    public ResultDTO tronConfigWalletParamList() {
        return generatePage(tronConfigWalletParamService.tronConfigWalletParamList());
    }

    /**
     * @Description: 修改提现手续费
     * @Param: [gas, id, request]
     * @return: com.blockchain.common.base.dto.ResultDTO
     * @Author: Liu.sd
     * @Date: 2019/3/22
     */
    @PostMapping("/updateConfigWallet")
    @RequiresPermissions("UpdWithdraw")
    @ApiOperation(value = TronConfigWalletParamControllerApi.UpdateConfigWallet.METHOD_TITLE_NAME, notes = TronConfigWalletParamControllerApi.UpdateConfigWallet.METHOD_TITLE_NOTE)
    public ResultDTO updateConfigWallet(@ApiParam(TronConfigWalletParamControllerApi.UpdateConfigWallet.METHOD_API_GASDTO) @RequestParam("gasPrice") BigDecimal gasPrice,
                                        @ApiParam(TronConfigWalletParamControllerApi.UpdateConfigWallet.METHOD_API_ID) @RequestParam("minWdAmount") BigDecimal minWdAmount,
                                        @ApiParam(TronConfigWalletParamControllerApi.UpdateConfigWallet.METHOD_API_ID) @RequestParam("id") Integer id,
                                        HttpServletRequest request) {
        tronConfigWalletParamService.updateConfigWallet(gasPrice, minWdAmount, id);
        return ResultDTO.requstSuccess();
    }

    @PostMapping("/getCollectionWallet")
    @ApiOperation(value = TronConfigWalletParamControllerApi.GetCollectionWallet.METHOD_TITLE_NAME, notes = TronConfigWalletParamControllerApi.GetCollectionWallet.METHOD_TITLE_NOTE)
    public ResultDTO getCollectionWallet(@ApiParam(TronConfigWalletParamControllerApi.GetCollectionWallet.METHOD_API_TOKEN_SYMBOL) @RequestParam("coinName") String tokenSymbol) {
        return generatePage(tronConfigWalletParamService.getCollectionWallet(tokenSymbol));
    }

    @PostMapping("/updateCollectionWallet")
    @ApiOperation(value = TronConfigWalletParamControllerApi.UpdateCollectionWallet.METHOD_TITLE_NAME, notes = TronConfigWalletParamControllerApi.UpdateCollectionWallet.METHOD_TITLE_NOTE)
    public ResultDTO updateCollectionWallet(@ApiParam(TronConfigWalletParamControllerApi.UpdateCollectionWallet.METHOD_API_ID) @RequestParam("id") Integer id,
                                            @ApiParam(TronConfigWalletParamControllerApi.UpdateCollectionWallet.METHOD_API_ADDR) @RequestParam("paramValue") String addr,
                                            @ApiParam(TronConfigWalletParamControllerApi.UpdateCollectionWallet.METHOD_API_DESCRIBE) @RequestParam("paramDescr") String describe) {
        return ResultDTO.requstSuccess(tronConfigWalletParamService.updateCollectionWallet(id, addr, describe));
    }

}
