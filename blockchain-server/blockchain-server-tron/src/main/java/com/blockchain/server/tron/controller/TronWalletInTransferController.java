package com.blockchain.server.tron.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.wallet.WalletTxDTO;
import com.blockchain.common.base.dto.wallet.WalletTxParamsDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.tron.common.constants.tx.InTxConstants;
import com.blockchain.server.tron.controller.api.TronWalletInTxApi;
import com.blockchain.server.tron.service.ITronWalletTransferService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 充值记录查询接口
 *
 * @author YH
 * @date 2019年3月9日09:50:24
 */
@Api(TronWalletInTxApi.API_NAME)
@RestController
@RequestMapping("/inTx")
public class TronWalletInTransferController extends BaseController {
    @Autowired
    ITronWalletTransferService tronWalletTransferService;

    @ApiOperation(value = TronWalletInTxApi.Success.METHOD_TITLE_NAME, notes = TronWalletInTxApi.Success.METHOD_TITLE_NOTE)
    @PostMapping("/success")
    public ResultDTO success(@ApiParam(TronWalletInTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                             @ApiParam(TronWalletInTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                             @ApiParam(TronWalletInTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        paramsDTO.setTxStatus(InTxConstants.SUCCESS);
        List<WalletTxDTO> list = tronWalletTransferService.selectInTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = TronWalletInTxApi.Error.METHOD_TITLE_NAME, notes = TronWalletInTxApi.Error.METHOD_TITLE_NOTE)
    @PostMapping("/error")
    public ResultDTO error(@ApiParam(TronWalletInTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                           @ApiParam(TronWalletInTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                           @ApiParam(TronWalletInTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        paramsDTO.setTxStatus(InTxConstants.ERROR);
        List<WalletTxDTO> list = tronWalletTransferService.selectInTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = TronWalletInTxApi.All.METHOD_TITLE_NAME, notes = TronWalletInTxApi.All.METHOD_TITLE_NOTE)
    @PostMapping("/all")
    public ResultDTO all(@ApiParam(TronWalletInTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                         @ApiParam(TronWalletInTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                         @ApiParam(TronWalletInTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = tronWalletTransferService.selectInTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = TronWalletInTxApi.selectById.METHOD_TITLE_NAME,
            notes = TronWalletInTxApi.selectById.METHOD_TITLE_NOTE)
    @GetMapping("/selectById")
    public ResultDTO selectById(@ApiParam(TronWalletInTxApi.selectById.METHOD_API_ID) @RequestParam("id") String id) {
        WalletTxDTO inTransfer = tronWalletTransferService.findInTransfer(id);
        return ResultDTO.requstSuccess(inTransfer);
    }

}
