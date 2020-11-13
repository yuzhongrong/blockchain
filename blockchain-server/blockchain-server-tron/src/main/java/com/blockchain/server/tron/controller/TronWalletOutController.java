package com.blockchain.server.tron.controller;

import com.blockchain.common.base.constant.BaseConstant;
import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.tron.controller.api.TronWalletOutApi;
import com.blockchain.server.tron.service.TronWalletOutService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: Liusd
 * @create: 2019-03-27 09:23
 **/
@RestController
@RequestMapping("/walletOut")
@Api(TronWalletOutApi.CONTROLLER_API)
public class TronWalletOutController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(TronWalletOutController.class);

    @Autowired
    private TronWalletOutService walletOutService;

    @GetMapping("/list")
    @ApiOperation(value = TronWalletOutApi.List.METHOD_TITLE_NAME, notes = TronWalletOutApi.List.METHOD_TITLE_NOTE)
    public ResultDTO walletInList(@ApiParam(TronWalletOutApi.List.METHOD_API_STATUS) @RequestParam(required = false, value = "status", defaultValue = "1") String status,
                                  @ApiParam(TronWalletOutApi.List.METHOD_API_COIN_NAME) @RequestParam(required = false, value = "coinName") String coinName,
                                  @ApiParam(TronWalletOutApi.List.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", defaultValue = BaseConstant.PAGE_DEFAULT_INDEX, required = false) Integer pageNum,
                                  @ApiParam(TronWalletOutApi.List.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", defaultValue = BaseConstant.PAGE_DEFAULT_SIZE, required = false) Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return generatePage(walletOutService.list(coinName, status));
    }

    /** 
    * @Description: 新增充币账户 
    * @Param: [addr, tokenId, privateKey, remark, request] 
    * @return: com.blockchain.common.base.dto.ResultDTO 
    * @Author: Liu.sd 
    * @Date: 2019/3/27
    */ 
    @PostMapping("/insert")
    @ApiOperation(value = TronWalletOutApi.Insert.METHOD_TITLE_NAME, notes = TronWalletOutApi.Insert.METHOD_TITLE_NOTE)
    public ResultDTO insert(@ApiParam(TronWalletOutApi.Insert.METHOD_API_ADDR) @RequestParam("addr") String addr, @ApiParam(TronWalletOutApi.Insert.METHOD_API_TOKENID) @RequestParam("tokenId") String tokenId, @ApiParam(TronWalletOutApi.Insert.METHOD_API_PRIVATEKEY) @RequestParam("privateKey") String privateKey, @ApiParam(TronWalletOutApi.Insert.METHOD_API_REMARK) @RequestParam(required = false, value = "remark") String remark,HttpServletRequest request) {
        walletOutService.insert(addr, tokenId,privateKey,remark);
        return ResultDTO.requstSuccess();
    }

    /**
     * @Description: 删除充币账户
     * @Param: [id, request]
     * @return: com.blockchain.common.base.dto.ResultDTO
     * @Author: Liu.sd
     * @Date: 2019/3/27
     */
    @PostMapping("/delete")
    @ApiOperation(value = TronWalletOutApi.Delete.METHOD_TITLE_NAME, notes = TronWalletOutApi.Delete.METHOD_TITLE_NOTE)
    public ResultDTO delete(@ApiParam(TronWalletOutApi.Delete.METHOD_API_ID) @RequestParam("id") String id,
                            HttpServletRequest request) {
        walletOutService.delete(id);
        return ResultDTO.requstSuccess();
    }
}
