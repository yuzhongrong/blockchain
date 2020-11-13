package com.blockchain.server.tron.controller;

import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.common.base.dto.wallet.WalletTxDTO;
import com.blockchain.common.base.dto.wallet.WalletTxParamsDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.tron.common.constants.tx.OutTxConstants;
import com.blockchain.server.tron.controller.api.TronWalletOutTxApi;
import com.blockchain.server.tron.service.ITronWalletTransferService;
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
@Api(TronWalletOutTxApi.API_NAME)
@RestController
@RequestMapping("/outTx")
public class TronWalletOutTransferController extends BaseController {
    @Autowired
    ITronWalletTransferService tronWalletTransferService;

    @ApiOperation(value = TronWalletOutTxApi.Check.METHOD_TITLE_NAME, notes = TronWalletOutTxApi.Check.METHOD_TITLE_NOTE)
    @PostMapping("/check")
    public ResultDTO check(@ApiParam(TronWalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                           @ApiParam(TronWalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                           @ApiParam(TronWalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        paramsDTO.setTxStatus(OutTxConstants.CHECK);
        List<WalletTxDTO> list = tronWalletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    /**
     * 查询内部充值记录
     * @param paramsDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = TronWalletOutTxApi.FindInternalRecord.METHOD_TITLE_NAME, notes =
            TronWalletOutTxApi.FindInternalRecord.METHOD_TITLE_NOTE)
    @PostMapping("/findInternalRecord")
    public ResultDTO findInternalRecord( @ApiParam(TronWalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                                         @ApiParam(TronWalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                         @ApiParam(TronWalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        return tronWalletTransferService.findInternalRecord(paramsDTO);

    }

    @ApiOperation(value = TronWalletOutTxApi.Recheck.METHOD_TITLE_NAME, notes =
            TronWalletOutTxApi.Recheck.METHOD_TITLE_NOTE)
    @PostMapping("/recheck")
    public ResultDTO recheck(@ApiParam(TronWalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                             @ApiParam(TronWalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                             @ApiParam(TronWalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        paramsDTO.setTxStatus(OutTxConstants.RECHECK);
        List<WalletTxDTO> list = tronWalletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = TronWalletOutTxApi.Pack.METHOD_TITLE_NAME, notes = TronWalletOutTxApi.Pack.METHOD_TITLE_NOTE)
    @PostMapping("/pack")
    public ResultDTO pack(@ApiParam(TronWalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                          @ApiParam(TronWalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                          @ApiParam(TronWalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        paramsDTO.setTxStatus(OutTxConstants.PACK);
        List<WalletTxDTO> list = tronWalletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = TronWalletOutTxApi.Success.METHOD_TITLE_NAME, notes =
            TronWalletOutTxApi.Success.METHOD_TITLE_NOTE)
    @PostMapping("/success")
    public ResultDTO success(@ApiParam(TronWalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                             @ApiParam(TronWalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                             @ApiParam(TronWalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        paramsDTO.setTxStatus(OutTxConstants.SUCCESS);
        List<WalletTxDTO> list = tronWalletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = TronWalletOutTxApi.Error.METHOD_TITLE_NAME, notes = TronWalletOutTxApi.Error.METHOD_TITLE_NOTE)
    @PostMapping("/error")
    public ResultDTO error(@ApiParam(TronWalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                           @ApiParam(TronWalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                           @ApiParam(TronWalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        paramsDTO.setTxStatus(OutTxConstants.ERROR);
        List<WalletTxDTO> list = tronWalletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = TronWalletOutTxApi.Reject.METHOD_TITLE_NAME, notes =
            TronWalletOutTxApi.Reject.METHOD_TITLE_NOTE)
    @PostMapping("/reject")
    public ResultDTO reject(@ApiParam(TronWalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                            @ApiParam(TronWalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                            @ApiParam(TronWalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        paramsDTO.setTxStatus(OutTxConstants.REJECT);
        List<WalletTxDTO> list = tronWalletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = TronWalletOutTxApi.All.METHOD_TITLE_NAME, notes = TronWalletOutTxApi.All.METHOD_TITLE_NOTE)
    @PostMapping("/all")
    public ResultDTO all(@ApiParam(TronWalletOutTxApi.METHOD_API_PARAM) WalletTxParamsDTO paramsDTO,
                         @ApiParam(TronWalletOutTxApi.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                         @ApiParam(TronWalletOutTxApi.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<WalletTxDTO> list = tronWalletTransferService.selectOutTransfer(paramsDTO);
        return generatePage(list);
    }

    @ApiOperation(value = TronWalletOutTxApi.SelectById.METHOD_TITLE_NAME,
            notes = TronWalletOutTxApi.SelectById.METHOD_TITLE_NOTE)
    @GetMapping("/selectById")
    public ResultDTO selectById(@ApiParam(TronWalletOutTxApi.METHOD_API_ID) @RequestParam("id") String id) {
        WalletTxDTO inTransfer = tronWalletTransferService.findOutTransfer(id);
        return ResultDTO.requstSuccess(inTransfer);
    }

    @ApiOperation(value = TronWalletOutTxApi.CheckPass.METHOD_TITLE_NAME,
            notes = TronWalletOutTxApi.CheckPass.METHOD_TITLE_NOTE)
    @PostMapping("/checkPass")
    public ResultDTO checkPass(@ApiParam(TronWalletOutTxApi.METHOD_API_ID) @RequestParam("id") String id){
        tronWalletTransferService.updateStatus(id,OutTxConstants.RECHECK);
        return  ResultDTO.requstSuccess();
    }

    @ApiOperation(value = TronWalletOutTxApi.RecheckPass.METHOD_TITLE_NAME,
            notes = TronWalletOutTxApi.RecheckPass.METHOD_TITLE_NOTE)
    @PostMapping("/recheckPass")
    public ResultDTO recheckPass(@ApiParam(TronWalletOutTxApi.METHOD_API_ID) @RequestParam("id") String id){
        tronWalletTransferService.handleOut(id,OutTxConstants.PACK);
        return  ResultDTO.requstSuccess();
    }

    @ApiOperation(value = TronWalletOutTxApi.GoReject.METHOD_TITLE_NAME,
            notes = TronWalletOutTxApi.GoReject.METHOD_TITLE_NOTE)
    @PostMapping("/goReject")
    public ResultDTO goReject(@ApiParam(TronWalletOutTxApi.METHOD_API_ID) @RequestParam("id") String id){
        tronWalletTransferService.handReject(id);
        return  ResultDTO.requstSuccess();
    }

}
