package com.imooc.mapper;

import com.imooc.pojo.vo.CategoryVo;
import com.imooc.pojo.vo.NewItemsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CategoryMapperCustom {

    /**
     * 根据一级分类Id查询子分类
     * @param rootCatId
     * @return
     */
    public List<CategoryVo> getSubCatList(Integer rootCatId);

    /**
     * 根据一级分类Id查询最近的上架的6条商品信息
     * @param map
     * @return
     */
    public List<NewItemsVo> getSixNewItemsLazy(@Param("paramMap") Map<String ,Object> map);

}
