package com.blockchain.server.ltc.controller;

import com.blockchain.common.base.constant.BaseConstant;
import com.blockchain.common.base.dto.ResultDTO;
import com.blockchain.server.base.controller.BaseController;
import com.blockchain.server.ltc.controller.api.WalletOutApi;
import com.blockchain.server.ltc.service.WalletOutService;
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
 * @create: 2019-03-27 10:07
 **/
@RestController
@RequestMapping("/walletOut")
@Api(WalletOutApi.CONTROLLER_API)
public class WalletOutController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(WalletOutController.class);

    @Autowired
    private WalletOutService walletOutService;

    @GetMapping("/list")
    @ApiOperation(value = WalletOutApi.List.METHOD_TITLE_NAME, notes = WalletOutApi.List.METHOD_TITLE_NOTE)
    public ResultDTO walletInList(@ApiParam(WalletOutApi.List.METHOD_API_COINNAME) @RequestParam(required = false, value = "coinName") String coinName,
                                  @ApiParam(WalletOutApi.List.METHOD_API_PAGENUM) @RequestParam(value = "pageNum", defaultValue = BaseConstant.PAGE_DEFAULT_INDEX, required = false) Integer pageNum,
                                  @ApiParam(WalletOutApi.List.METHOD_API_PAGESIZE) @RequestParam(value = "pageSize", defaultValue = BaseConstant.PAGE_DEFAULT_SIZE, required = false) Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return generatePage(walletOutService.list(coinName));
    }

    /** 
    * @Description: 新增充币账户 
    * @Param: [addr, tokenId, privateKey, remark, request] 
    * @return: com.blockchain.common.base.dto.ResultDTO 
    * @Author: Liu.sd 
    * @Date: 2019/3/27 
    */
    @PostMapping("/insert")
    @ApiOperation(value = WalletOutApi.Insert.METHOD_TITLE_NAME, notes = WalletOutApi.Insert.METHOD_TITLE_NOTE)
    public ResultDTO insert(@ApiParam(WalletOutApi.Insert.METHOD_API_ADDR) @RequestParam("addr") String addr, @ApiParam(WalletOutApi.Insert.METHOD_API_TOKENID) @RequestParam("tokenId") Integer tokenId, @ApiParam(WalletOutApi.Insert.METHOD_API_PRIVATEKEY) @RequestParam("privateKey") String privateKey, @ApiParam(WalletOutApi.Insert.METHOD_API_REMARK) @RequestParam(required = false, value = "remark") String remark, HttpServletRequest request) {
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
    @ApiOperation(value = WalletOutApi.Delete.METHOD_TITLE_NAME, notes = WalletOutApi.Delete.METHOD_TITLE_NOTE)
    public ResultDTO delete(@ApiParam(WalletOutApi.Delete.METHOD_API_ID) @RequestParam("id") String id,
                            HttpServletRequest request) {
        walletOutService.delete(id);
        return ResultDTO.requstSuccess();
    }
}
