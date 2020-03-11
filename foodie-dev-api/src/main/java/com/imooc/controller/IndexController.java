package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVo;
import com.imooc.pojo.vo.NewItemsVo;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Api(value = "首页",tags = {"首页展示相关接口"})
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;


    /**
     * 查询轮播图所需要数据
     * @return
     */
    @GetMapping("/carousel")
    @ApiOperation(value = "获取首页轮播图列表",notes = "获取首页轮播图列表",httpMethod ="GET" )
    public IMOOCJSONResult carousel(){

       //查询轮播图所需要数据
        List<Carousel> list = new ArrayList<>();

        String carouselStr = redisOperator.get("carousel");
        //判断redis中是否存在carousel的键
        if (StringUtils.isBlank(carouselStr)){
            //查询的list放入redis缓存
             list = carouselService.queryAll(YesOrNo.YES.code);
             //例如 查询的key在redis中不存在，对应的id在数据库中也不存在，此时被非法用户攻击，大量的请求会打在db上，造成宕机，从而影响整个系统
             //这种现象成为缓存穿透
             //把空的数据也缓存起来，比如空字符串，空对象，空数组或者list
             if (list!=null &&list.size()>0){
                 redisOperator.set("carousel", JsonUtils.objectToJson(list));
             }else {
                 redisOperator.set("carousel",JsonUtils.objectToJson(list),6*50);
             }

        }else {
             list = JsonUtils.jsonToList(carouselStr,Carousel.class);
        }
        return IMOOCJSONResult.ok(list);
    }

    @ApiOperation(value = "获取商品分类(一级分类)",notes = "获取商品分类(一级分类)",httpMethod ="GET" )
    @GetMapping("cats")
    public IMOOCJSONResult cats(){

        //查询商品的一级分类数据
        List<Category> categoryList = new ArrayList<>();

        String categoryListStr = redisOperator.get("categoryList");
        if (StringUtils.isBlank(categoryListStr)){
            categoryList = categoryService.queryAllRootLevelCat();
            if (categoryList!=null && categoryList.size()>0){
                //查询出以及分类的数据放入redis缓存
                redisOperator.set("categoryList",JsonUtils.objectToJson(categoryList));
            }else {
                redisOperator.set("categoryList",JsonUtils.objectToJson(categoryList),6*50);
            }

        }else {
            categoryList=JsonUtils.jsonToList(categoryListStr,Category.class);
        }

        return IMOOCJSONResult.ok(categoryList);
    }

    /**
     * 获取商品子分类信息
     * @param rootCatId
     * @return
     */
    @ApiOperation(value = "获取商品子分类",notes = "获取商品子分类",httpMethod ="GET" )
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult subcats(
            @ApiParam(name = "rootCatId",value = "一级分类Id ",required = true)
            @PathVariable Integer rootCatId){

        if (rootCatId ==null){
          return IMOOCJSONResult.errorMsg("");
      }
        List<CategoryVo> categoryVoList = new ArrayList<>();

        String categoryVoStr = redisOperator.get("CategoryVo");

        if (StringUtils.isBlank(categoryVoStr)){
            categoryVoList = categoryService.getSubCatList(rootCatId);
            //商品子分类存入redis
            if (categoryVoList!=null&&categoryVoList.size()>0){
                redisOperator.set("CategoryVo",JsonUtils.objectToJson(categoryVoList));
            }else {
                redisOperator.set("CategoryVo",JsonUtils.objectToJson(categoryVoList),6*50);
            }
        }else {
            categoryVoList = JsonUtils.jsonToList(categoryVoStr,CategoryVo.class);
        }

        return IMOOCJSONResult.ok(categoryVoList);

    }

    @ApiOperation(value = "根据一级分类Id查询最近的上架的6条商品信息",notes = "根据一级分类Id查询最近的上架的6条商品信息",httpMethod ="GET" )
    @GetMapping("/sixNewItems/{rootCatId}")
    public IMOOCJSONResult sixNewItems(
            @ApiParam(name = "rootCatId",value = "一级分类Id ",required = true)
            @PathVariable Integer rootCatId){

        if (rootCatId ==null){
            return IMOOCJSONResult.errorMsg("");
        }

        List<NewItemsVo> list = categoryService.getSixNewItemsLazy(rootCatId);
        return IMOOCJSONResult.ok(list);

    }

}
