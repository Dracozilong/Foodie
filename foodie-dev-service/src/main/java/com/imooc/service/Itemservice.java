package com.imooc.service;

import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountsVo;
import com.imooc.pojo.vo.ItemCommentVo;
import com.imooc.pojo.vo.ShopcartVo;
import com.imooc.utils.PagedGridResult;
import io.swagger.models.auth.In;

import java.util.List;

public interface Itemservice {

    /**
     * 根据商品Id查询详情
     * @param itemId
     * @return
     */
    public Items queryItemById(String itemId);


    /**
     * 根据商品Id查询图片
     * @param itemId
     * @return
     */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品Id查询规格
     * @param itemId
     * @return
     */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品Id查询参数
     * @param itemId
     * @return
     */
    public ItemsParam queryItemParam(String itemId);

    /**
     * 根据商品Id查询评价数量
     * @param itemId
     * @return
     */
    public CommentLevelCountsVo queryCommentCounts(String itemId);

    /**
     * 根据商品Id和评价等级进行查询
     * @param itemId
     * @param level
     * @return
     */
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize);

    /**
     * 根据关键字和排序搜索
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(String keywords,String sort,Integer page, Integer pageSize);

    /**
     * 根据分类Id查询
     * @param CatId
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(Integer CatId, String sort, Integer page, Integer pageSize);

    /**
     * 根据规格ids查询最新的商品购物车商品
     * @param specIds
     * @return
     */
    public List<ShopcartVo> queryItemsBySpecIds(String specIds);

    /**
     * 根据specId查询商品
     * @param specId
     * @return
     */
    public ItemsSpec queryItemsBySpecId(String specId);

    /**
     * 根据itemId查询商品主图
     * @param itemId
     * @return
     */
    public String  queryItemImg(String itemId);

    /**
     * 根据规格specId和购买数量扣减库存
     * @param specId
     * @param buyCounts
     * @return
     */
    public void decreaseItemSpecStock(String specId,int buyCounts);
}
