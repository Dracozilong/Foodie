package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.enums.YesOrNo;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.service.center.MyCommentsService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品评价",tags = "{商品评价相关接口}")
@RestController
@RequestMapping("mycomments")
public class MyCommentsController extends BaseController {

    @Autowired
    private MyCommentsService myCommentsService;

    @ApiOperation(value = "查询需要评价的商品",notes = "查询需要评价的商品",httpMethod = "POST")
    @PostMapping("/pending")
    public IMOOCJSONResult pending(
            @ApiParam(name = "orderId",value = "orderId",required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId",value = "userId",required = true)
            @RequestParam String userId){

        //判断用户和订单是否关联
        IMOOCJSONResult result = checkUserOrders(userId, orderId);

        if (result.getStatus()!= HttpStatus.OK.value()){
            return result;
        }

        Orders orders = (Orders)result.getData();

        //判断该笔订单是否已经评价过了，评价过了就不在继续
        if (orders.getIsComment() == YesOrNo.YES.code){
            return IMOOCJSONResult.errorMsg("该商品已经评价过了");
        }

        List<OrderItems> itemsList = myCommentsService.queryPendingComment(orderId);

        return IMOOCJSONResult.ok(itemsList);

    }
}
