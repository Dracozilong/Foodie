package com.imooc.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.CommentLevel;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentLevelCountsVo;
import com.imooc.pojo.vo.ItemCommentVo;
import com.imooc.pojo.vo.SearchItemsVo;
import com.imooc.pojo.vo.ShopcartVo;
import com.imooc.service.Itemservice;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class ItemServiceImpl implements Itemservice {

    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private ItemsImgMapper itemsImgMapper;

    @Autowired
    private ItemsSpecMapper itemsSpecMapper;

    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Autowired
    private ItemsMapperCustom itemsMapperCustom;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {

        Example example = new Example(ItemsImg.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("itemId",itemId);

        List<ItemsImg> itemsImgs = itemsImgMapper.selectByExample(example);

        return itemsImgs;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {

        Example example = new Example(ItemsSpec.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("itemId",itemId);

        List<ItemsSpec> itemsSpecs = itemsSpecMapper.selectByExample(example);

        return itemsSpecs;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParam(String itemId) {

        Example example =new Example(ItemsParam.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("itemId",itemId);

        return   itemsParamMapper.selectOneByExample(example);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountsVo queryCommentCounts(String itemId) {

        //好评
        Integer goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.code);

        //中评
        Integer normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.code);

        //差评
        Integer badCounts = getCommentCounts(itemId, CommentLevel.BAD.code);

        //总共评价数
        Integer totalCount =goodCounts+normalCounts+badCounts;

        //返回CommentLevelCountsVo
        CommentLevelCountsVo countsVo = new CommentLevelCountsVo();

        countsVo.setGoodCounts(goodCounts);

        countsVo.setNormalCounts(normalCounts);

        countsVo.setBadCounts(badCounts);

        countsVo.setTotalCounts(totalCount);

        return countsVo;
    }


    /**
     * 统计评价数
     * @param itemId
     * @param level
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    Integer getCommentCounts(String itemId,Integer level){

        ItemsComments comments =new ItemsComments();

        comments.setItemId(itemId);

        if (level!=null){
            comments.setCommentLevel(level);
        }

        return itemsCommentsMapper.selectCount(comments);
    }

    /**
     * 根据商品Id和评价等级进行查询
     * @param itemId
     * @param level
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryPagedComments(String itemId, Integer level,Integer page,Integer pageSize) {

        Map<String,Object> map = new HashMap<>();
        map.put("itemId",itemId);
        map.put("level",level);

        //分页
        PageHelper.startPage(page, pageSize);

        List<ItemCommentVo> commentVoList = itemsMapperCustom.queryItemComments(map);

        commentVoList.forEach(itemCommentVo -> {
            DesensitizationUtil.commonDisplay(itemCommentVo.getNickName());});

        PagedGridResult grid = setterPagedGrid(commentVoList, page);

        return grid;

    }

      private PagedGridResult setterPagedGrid(List<?> list, Integer page){
          PageInfo<?> pageList = new PageInfo<>(list);
          PagedGridResult grid = new PagedGridResult();
          grid.setPage(page);
          grid.setRows(list);
          grid.setTotal(pageList.getPages());
          grid.setRecords(pageList.getTotal());
          return grid;
      }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {

        Map<String,Object> map = new HashMap<>();
        map.put("keywords",keywords);
        map.put("sort",sort);

        //分页
        PageHelper.startPage(page, pageSize);

        List<SearchItemsVo> searchItems = itemsMapperCustom.searchItems(map);

        PagedGridResult grid = setterPagedGrid(searchItems, pageSize);

        return grid;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize) {

        Map<String,Object> map = new HashMap<>();
        map.put("catId",catId);
        map.put("sort",sort);

        //分页
        PageHelper.startPage(page, pageSize);

        List<SearchItemsVo> searchItems = itemsMapperCustom.searchItemsByThirdCat(map);

        PagedGridResult grid = setterPagedGrid(searchItems, pageSize);

        return grid;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopcartVo> queryItemsBySpecIds(String specIds) {

        String[] split = specIds.split(",");

        List specIdList =new ArrayList();

        Collections.addAll(specIdList,split);

        List<ShopcartVo> shopcartVoList = itemsMapperCustom.queryItemsBySpecIds(specIdList);

        return shopcartVoList;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec queryItemsBySpecId(String specId) {

        ItemsSpec itemsSpec = itemsSpecMapper.selectByPrimaryKey(specId);

        return  itemsSpec;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemImg(String itemId) {

        ItemsImg itemsImg =new ItemsImg();

        itemsImg.setItemId(itemId);

        itemsImg.setIsMain(YesOrNo.YES.code);

        ItemsImg img = itemsImgMapper.selectOne(itemsImg);

        return img!= null ?img.getUrl() : "";
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void decreaseItemSpecStock(String specId, int buyCounts) {

        //synchronized不推荐使用，集群环境下无用，性能低下
        //锁数据库，不推荐，导致数据库性能低下
        //分布式锁 zookeeper,redis
        Integer result = itemsMapperCustom.decreaseItemSpecStock(specId, buyCounts);

        if (result!=1){
            throw  new RuntimeException("订单创建失败，原因:库存不足");
        }

    }
}
