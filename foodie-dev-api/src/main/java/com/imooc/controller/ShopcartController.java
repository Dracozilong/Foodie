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

        //从redis中获取购物车现有数据
        String shopcartStr = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        //判断购物车是否有数据
        if (StringUtils.isNotBlank(shopcartStr)){
            //购物车中存在数据
            List<ShopcartBO> shopcartBOList = JsonUtils.jsonToList(shopcartStr, ShopcartBO.class);
            //遍历list获取商品规格id
            for (ShopcartBO bo : shopcartBOList) {
                String specId = bo.getSpecId();
                if (specId.equals(itemSpecId)){
                    //删除商品
                    shopcartBOList.remove(bo);
                    break;
                }
            }
            //覆盖购物车
            redisOperator.set(FOODIE_SHOPCART+":"+userId,JsonUtils.objectToJson(shopcartBOList));
        }
        return IMOOCJSONResult.ok();
    }
}
