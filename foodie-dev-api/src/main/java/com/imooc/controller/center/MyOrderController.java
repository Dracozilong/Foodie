package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.imooc.controller.BaseController.PAGE_SIZE;

@Api(value = "用户订单信息",tags = "{用户订单信息相关接口}")
@RestController
@RequestMapping("myorders")
public class MyOrderController extends BaseController {

    @Autowired
    private MyOrderService myOrderService;

    @ApiOperation(value = "查询用户订单",notes = "查询用户订单",httpMethod = "POST")
    @PostMapping("query")
    public IMOOCJSONResult query(
            @ApiParam(name = "userId",value = "用户Id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderStatus",value = "订单状态",required = false)
            @RequestParam Integer orderStatus,
            @ApiParam(name = "page",value = "当前页",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "每页显示条数",required = false)
            @RequestParam Integer pageSize){

        if (userId ==null){
            return IMOOCJSONResult.errorMsg("");
        }

        if (page == null){
            page=1;
        }
        if (pageSize==null){
            pageSize =PAGE_SIZE;
        }

        PagedGridResult gridResult = myOrderService.queryMyOrders(userId, orderStatus, page, pageSize);

        return IMOOCJSONResult.ok(gridResult);

    }

}
