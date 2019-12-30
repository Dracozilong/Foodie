package com.imooc.service.Impl;


import com.imooc.mapper.CategoryMapper;
import com.imooc.mapper.CategoryMapperCustom;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVo;
import com.imooc.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryMapperCustom categoryMapperCustom;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> queryAllRootLevelCat() {

        //构造查询Example
        Example example =new Example(Category.class);

        Example.Criteria criteria = example.createCriteria();

        //构造查询条件
        criteria.andEqualTo("type",1);

        //查询
        List<Category> categoryList = categoryMapper.selectByExample(example);

        return categoryList;
    }

    /**
     * 根据一级ID查询子分类
     * @param rootCatId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVo> getSubCatList(Integer rootCatId) {
        return categoryMapperCustom.getSubCatList(rootCatId);
    }

    /**
     * 根据一级分类Id查询最近的上架的6条商品信息
     * @param rootCatId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List getSixNewItemsLazy(Integer rootCatId) {

        Map<String,Object> map = new HashMap<>();

        map.put("rootCatId",rootCatId);

        List list = categoryMapperCustom.getSixNewItemsLazy(map);

        return list;
    }
}
