package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVo;
import com.imooc.pojo.vo.NewItemsVo;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "首页",tags = {"首页展示相关接口"})
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 查询轮播图所需要数据
     * @return
     */
    @GetMapping("/carousel")
    @ApiOperation(value = "获取首页轮播图列表",notes = "获取首页轮播图列表",httpMethod ="GET" )
    public IMOOCJSONResult carousel(){

       //查询轮播图所需要数据
        List<Carousel> list = carouselService.queryAll(YesOrNo.YES.code);

        return IMOOCJSONResult.ok(list);
    }

    @ApiOperation(value = "获取商品分类(一级分类)",notes = "获取商品分类(一级分类)",httpMethod ="GET" )
    @GetMapping("cats")
    public IMOOCJSONResult cats(){

        //查询商品的一级分类数据
        List<Category> categoryList = categoryService.queryAllRootLevelCat();

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

        List<CategoryVo> list = categoryService.getSubCatList(rootCatId);
        return IMOOCJSONResult.ok(list);

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
