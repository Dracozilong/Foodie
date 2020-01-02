package com.imooc.controller.center;

import com.imooc.controller.BaseController;
import com.imooc.enums.YesOrNo;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.service.center.MyCommentsService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
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

    @ApiOperation(value = "保存商品评价列表",notes = "保存商品评价列表",httpMethod = "POST")
    @PostMapping("saveList")
    public IMOOCJSONResult saveList(
            @ApiParam(name = "orderId",value = "orderId",required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId",value = "userId",required = true)
            @RequestParam String userId,
            @RequestBody List<OrderItemsCommentBO> commentsList){

        //判断用户和订单是否关联
        IMOOCJSONResult result = checkUserOrders(userId, orderId);

        if (result.getStatus()!= HttpStatus.OK.value()){
            return result;
        }

        //判断list是否为空
        if(commentsList== null || commentsList.isEmpty() || commentsList.size()==0){

            return IMOOCJSONResult.errorMsg("评价列表为空");
        }

        myCommentsService.saveComments(userId,orderId,commentsList);

        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "查询用户商品历史评价",notes = "查询用户商品历史评价",httpMethod = "POST")
    @PostMapping("query")
    public IMOOCJSONResult query(
            @ApiParam(name = "userId",value = "用户Id",required = true)
            @RequestParam String userId,
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

        PagedGridResult gridResult = myCommentsService.queryMyComments(userId, page, pageSize);

        return IMOOCJSONResult.ok(gridResult);

    }

}
