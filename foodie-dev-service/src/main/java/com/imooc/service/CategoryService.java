package com.imooc.service;

import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVo;
import com.imooc.pojo.vo.NewItemsVo;

import java.util.List;

public interface CategoryService {

    /**
     * 查询以及分类目录
     * @return
     */
    public List<Category> queryAllRootLevelCat();

    /**
     * 根据一级分类Id查询二级分类
     * @param rootCatId
     * @return
     */
    public List<CategoryVo> getSubCatList(Integer rootCatId);

    /**
     * 根据一级分类Id查询最近的上架的6条商品信息
     * @param rootCatId
     * @return
     */
    public List<NewItemsVo> getSixNewItemsLazy(Integer rootCatId);
}
