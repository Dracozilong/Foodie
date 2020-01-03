package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.OrderStatusCountsVo;
import com.imooc.service.OrderService;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.imooc.controller.BaseController.PAGE_SIZE;

@Api(value = "用户订单信息",tags = "{用户订单信息相关接口}")
@RestController
@RequestMapping("myorders")
public class MyOrderController extends BaseController {

    @Autowired
    private MyOrderService myOrderService;

    @Autowired
    private OrderService orderService;


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

    @ApiOperation(value = "修改订单状态",notes = "修改订单状态",httpMethod = "GET")
    @GetMapping("/deliver")
    public IMOOCJSONResult deliver(
            @ApiParam(name = "orderId",value = "订单Id",required = true)
            @RequestParam String orderId){

        if (StringUtils.isBlank(orderId)){
            return IMOOCJSONResult.errorMsg("订单Id不能为空");
        }

        myOrderService.updateDeliverOrdersStatus(orderId);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户确认收货",notes = "用户确认收货",httpMethod = "POST")
    @PostMapping("/confirmReceive")
    public IMOOCJSONResult confirmReceive(
            @ApiParam(name = "orderId",value = "订单Id",required = true)
            @RequestParam String orderId,
               @ApiParam(name = "userId",value = "用户订单Id",required = true)
            @RequestParam String userId
            ){

        IMOOCJSONResult result = checkUserOrders(userId, orderId);

        if (result.getStatus()!= HttpStatus.OK.value()){
            return result;
        }

        boolean flag = myOrderService.updateReceiveOrderStatus(orderId);

        if (!flag){
            return IMOOCJSONResult.errorMsg("订单确认收货失败");
        }
        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "删除订单",notes = "删除订单",httpMethod = "POST")
    @PostMapping("/delete")
    public IMOOCJSONResult delete(
            @ApiParam(name = "orderId",value = "订单Id",required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId",value = "用户Id",required = true)
            @RequestParam String userId){

        IMOOCJSONResult result = checkUserOrders(userId, orderId);

        if (result.getStatus()!= HttpStatus.OK.value()){
            return result;
        }

        boolean flag = myOrderService.deleteOrder(userId, orderId);
        if (!flag){
            return IMOOCJSONResult.errorMsg("订单逻辑删除失败");
        }

        return IMOOCJSONResult.ok();
    }
    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     * @param userId
     * @param orderId
     * @return
     */
    public IMOOCJSONResult checkUserOrders(String userId,String orderId){

        Orders order = myOrderService.queryMyorder(userId, orderId);

        if (order == null){
            IMOOCJSONResult.errorMsg("订单不存在");
        }
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "获得订单概况数概况",notes = "获得订单概况数概况",httpMethod = "POST")
    @PostMapping("statusCounts")
    public IMOOCJSONResult statusCounts(
            @ApiParam(name = "userId",value = "用户Id",required = true)
            @RequestParam  String userId){

        if (StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("用户Id不能为空");
        }

        OrderStatusCountsVo counts = myOrderService.getOrderStatusCounts(userId);

        return IMOOCJSONResult.ok(counts);
    }

    @ApiOperation(value = "获得订单趋势",notes = "获得订单趋势",httpMethod = "POST")
    @PostMapping("trend")
    public IMOOCJSONResult trend(
            @ApiParam(name = "userId",value = "用户Id",required = true)
            @RequestParam String userId,
            @ApiParam(name = "page",value = "当前页",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "每页显示条数",required = false)
            @RequestParam Integer pageSize
            ){

        if (StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("订单Id不能为空");
        }

        if (userId ==null){
            return IMOOCJSONResult.errorMsg("");
        }

        if (page == null){
            page=1;
        }
        if (pageSize==null){
            pageSize =PAGE_SIZE;
        }

        PagedGridResult trend = myOrderService.getMyOrderTrend(userId, page, pageSize);

        return IMOOCJSONResult.ok(trend);

    }
}
