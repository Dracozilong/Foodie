package com.imooc.controller;


import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "商品详细页",tags = {"商品详细页相关接口"})
@RestController
@RequestMapping("/shopcart")
public class ShopcartController extends BaseController {

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "添加商品到购物车",notes = "添加商品到购物车",httpMethod = "POST")
    @PostMapping("add")
    public IMOOCJSONResult add(@RequestParam  String userId, @RequestBody ShopcartBO shopcartBO, HttpServletRequest request, HttpServletResponse response){
        if (StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("");
        }
        //TODO 前端用户在登录情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        //TODO 获取购物车中的商品的规格id,如果添加重复的商品 则添加数量
        //判断redis中是否有键为shopcart的值
        String shopcartStr = redisOperator.get(FOODIE_SHOPCART+ ":" +userId);
        List<ShopcartBO> shopcartList=null;
        if (StringUtils.isNotBlank(shopcartStr)){
            //说明原来redis中存在已保存的商品
             shopcartList = JsonUtils.jsonToList(shopcartStr, ShopcartBO.class);
            boolean flag =false;
            for (ShopcartBO sc : shopcartList) {
                //获取商品specId
                String specId = sc.getSpecId();
                if (specId.equals(shopcartBO.getSpecId())){
                   sc.setBuyCounts(sc.getBuyCounts()+shopcartBO.getBuyCounts());
                   flag=true;
                }
            }
            if (!flag){
                //没有同类的商品，把新的商品添加到redis缓存
                shopcartList.add(shopcartBO);
            }

        }else {
            //原来购物车中没有数据,把前端传递的shopcart放入redis缓存
            shopcartList=new ArrayList<>();
            shopcartList.add(shopcartBO);
        }
        //覆盖现有购物车
        redisOperator.set(FOODIE_SHOPCART+":"+userId,JsonUtils.objectToJson(shopcartList));

        return IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "从购物车中删除商品",notes = "从购物车中删除商品",httpMethod = "POST")
    @PostMapping("del")
    public IMOOCJSONResult del(@RequestParam  String userId, @RequestParam String itemSpecId){
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)){
            return IMOOCJSONResult.errorMsg("参数不能为空");
        }

        //TODO 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除后端购物车中的商品

        return IMOOCJSONResult.ok();
    }
}
