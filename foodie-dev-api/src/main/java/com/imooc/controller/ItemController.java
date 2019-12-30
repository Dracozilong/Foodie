package com.imooc.controller;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountsVo;
import com.imooc.pojo.vo.ItemInfoVo;
import com.imooc.pojo.vo.ShopcartVo;
import com.imooc.service.Itemservice;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品详细页",tags = {"商品详细页相关接口"})
@RequestMapping("/items")
@RestController
public class ItemController extends BaseController {

    @Autowired
    private Itemservice itemservice;

    @ApiOperation(value = "查询商品详情",notes = "查询商品详情",httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public IMOOCJSONResult info(
            @ApiParam(name = "itemId",value = "商品Id ",required = true)
            @PathVariable String itemId){

        if(StringUtils.isBlank(itemId)){
            IMOOCJSONResult.errorMsg(null);
        }

        //获取商品信息
        Items item = itemservice.queryItemById(itemId);

        //获取商品图片列表
        List<ItemsImg> itemsImgs = itemservice.queryItemImgList(itemId);

        //获取商品规格列表
        List<ItemsSpec> itemsSpec = itemservice.queryItemSpecList(itemId);

        //获取商品参数
        ItemsParam itemsParam = itemservice.queryItemParam(itemId);

        //存入ItemInfoVo
        ItemInfoVo itemInfoVo =new ItemInfoVo();

        itemInfoVo.setItem(item);

        itemInfoVo.setItemImgList(itemsImgs);

        itemInfoVo.setItemSpecList(itemsSpec);

        itemInfoVo.setItemParams(itemsParam);

       return IMOOCJSONResult.ok(itemInfoVo);
    }

    /**
     * 商品评价数
     * @param itemId
     * @return
     */
    @ApiOperation(value = "查询商品评价数",notes = "查询商品评价数",httpMethod = "GET")
    @GetMapping("commentLevel")
    public IMOOCJSONResult commentLevel(@RequestParam String itemId){

        if(StringUtils.isBlank(itemId)){
            IMOOCJSONResult.errorMsg(null);
        }

        CommentLevelCountsVo commentList = itemservice.queryCommentCounts(itemId);

        return IMOOCJSONResult.ok(commentList);
    }


    /**
     * 查询商品评价
     * @param itemId
     * @param level
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "查询商品评价",notes = "查询商品评价",httpMethod = "GET")
    @GetMapping("comments")
    public IMOOCJSONResult comments(
            @ApiParam(name = "itemId",value = "商品Id ",required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level",value = "评价等级 ",required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page",value = "当前页 ",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "当前页显示条数 ",required = false)
            @RequestParam Integer pageSize){

        if(StringUtils.isBlank(itemId)){
            IMOOCJSONResult.errorMsg(null);
        }

        if (page == null){
            page=1;
        }
        if (pageSize==null){
            pageSize =COMMENT_PAGE_SIZE;
        }

        PagedGridResult gridResult = itemservice.queryPagedComments(itemId, level, page, pageSize);

        return IMOOCJSONResult.ok(gridResult);
    }

    @ApiOperation(value = "商品搜索",notes = "商品搜索",httpMethod = "GET")
    @GetMapping("search")
    public IMOOCJSONResult search(
            @ApiParam(name = "keywords",value = "关键字 ",required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort",value = "排序 ",required = false)
            @RequestParam String sort,
            @ApiParam(name = "page",value = "当前页 ",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "当前页显示条数 ",required = false)
            @RequestParam Integer pageSize
    ){

        if(StringUtils.isBlank(keywords)){
            IMOOCJSONResult.errorMsg(null);
        }

        if (page == null){
            page=1;
        }
        if (pageSize==null){
            pageSize =PAGE_SIZE;
        }

        PagedGridResult gridResult = itemservice.searchItems(keywords, sort, page, pageSize);

        return IMOOCJSONResult.ok(gridResult);
    }

    @ApiOperation(value = "通过分类Id搜索商品列表",notes = "通过分类Id搜索商品列表",httpMethod = "GET")
    @GetMapping("/catItems")
    public IMOOCJSONResult catItems(
            @ApiParam(name = "catId",value = "三级分类Id ",required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort",value = "排序 ",required = false)
            @RequestParam String sort,
            @ApiParam(name = "page",value = "当前页 ",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "当前页显示条数 ",required = false)
            @RequestParam Integer pageSize
    ){

        if(catId == null){
            IMOOCJSONResult.errorMsg(null);
        }

        if (page == null){
            page=1;
        }
        if (pageSize==null){
            pageSize =PAGE_SIZE;
        }

        PagedGridResult gridResult = itemservice.searchItems(catId, sort, page, pageSize);

        return IMOOCJSONResult.ok(gridResult);
    }

    @ApiOperation(value = "根据规格ids查询最新的商品购物车商品",notes = "根据规格ids查询最新的商品购物车商品",httpMethod = "GET")
    @GetMapping("refresh")
    public IMOOCJSONResult refresh(
            @ApiParam(name = "itemSpecIds",value = "拼接的规格ids ",required = true,example = "1001,1002,1003")
            @RequestParam String itemSpecIds){

        if (StringUtils.isBlank(itemSpecIds)){
            return IMOOCJSONResult.ok();
        }

        List<ShopcartVo> shopcartVoList = itemservice.queryItemsBySpecIds(itemSpecIds);

        return IMOOCJSONResult.ok(shopcartVoList);
    }
}
