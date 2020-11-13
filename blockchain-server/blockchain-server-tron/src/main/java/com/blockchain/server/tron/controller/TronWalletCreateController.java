package com.blockchain.server.tron.controller;

import com.blockchain.common.base.constant.BaseConstant;
import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.tron.controller.api.TronWalletCreateApi;
import com.blockchain.server.tron.service.TronWalletCreateService;
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
@RequestMapping("/walletCreate")
@Api(TronWalletCreateApi.CONTROLLER_API)
public class TronWalletCreateController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(TronWalletCreateController.class);

    @Autowired
    private TronWalletCreateService walletCreateService;

    @GetMapping("/list")
    @ApiOperation(value = TronWalletCreateApi.List.METHOD_TITLE_NAME, notes = TronWalletCreateApi.List.METHOD_TITLE_NOTE)
    public ResultDTO walletInList(@ApiParam(TronWalletCreateApi.List.METHOD_API_STATUS) @RequestParam(required = false, value = "status") String status,
                                  @ApiParam(TronWalletCreateApi.List.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", defaultValue = BaseConstant.PAGE_DEFAULT_INDEX, required = false) Integer pageNum,
                                  @ApiParam(TronWalletCreateApi.List.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", defaultValue = BaseConstant.PAGE_DEFAULT_SIZE, required = false) Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return generatePage(walletCreateService.list(status));
    }

    /** 
    * @Description: 新增充币账户 
    * @Param: [addr, tokenId, privateKey, remark, request] 
    * @return: com.blockchain.common.base.dto.ResultDTO 
    * @Author: Liu.sd 
    * @Date: 2019/3/27
    */ 
    @PostMapping("/insert")
    @ApiOperation(value = TronWalletCreateApi.Insert.METHOD_TITLE_NAME, notes = TronWalletCreateApi.Insert.METHOD_TITLE_NOTE)
    public ResultDTO insert(@ApiParam(TronWalletCreateApi.Insert.METHOD_API_ADDR) @RequestParam("addr") String addr,
                            @ApiParam(TronWalletCreateApi.Insert.METHOD_API_TOKENID) @RequestParam("tokenId") String tokenId,
                            @ApiParam(TronWalletCreateApi.Insert.METHOD_API_PRIVATEKEY) @RequestParam("privateKey") String privateKey,
                            @ApiParam(TronWalletCreateApi.Insert.METHOD_API_REMARK) @RequestParam(required = false, value = "remark") String remark,
                            HttpServletRequest request) {
        walletCreateService.insert(addr, tokenId,privateKey,remark);
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
    @ApiOperation(value = TronWalletCreateApi.Delete.METHOD_TITLE_NAME, notes = TronWalletCreateApi.Delete.METHOD_TITLE_NOTE)
    public ResultDTO delete(@ApiParam(TronWalletCreateApi.Delete.METHOD_API_ID) @RequestParam("id") String id,
                            HttpServletRequest request) {
        walletCreateService.delete(id);
        return ResultDTO.requstSuccess();
    }

    /**
     * @Description: 修改充币账户
     * @Param: [id, request]
     * @return: com.blockchain.common.base.dto.ResultDTO
     * @Author: Liu.sd
     * @Date: 2019/3/27
     */
    @PostMapping("/update")
    @ApiOperation(value = TronWalletCreateApi.Update.METHOD_TITLE_NAME, notes = TronWalletCreateApi.Update.METHOD_TITLE_NOTE)
    public ResultDTO update(@ApiParam(TronWalletCreateApi.Update.METHOD_API_ADDR) @RequestParam("addr") String addr,
                            @ApiParam(TronWalletCreateApi.Update.METHOD_API_REMAKR) @RequestParam("remark") String remark,
                            @ApiParam(TronWalletCreateApi.Update.METHOD_API_STATUS) @RequestParam("status") String status,
                            HttpServletRequest request) {
        return ResultDTO.requstSuccess(walletCreateService.update(addr, remark, status));
    }
}
