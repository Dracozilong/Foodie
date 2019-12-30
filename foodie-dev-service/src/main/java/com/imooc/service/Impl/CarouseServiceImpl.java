package com.imooc.service.Impl;

import com.imooc.mapper.CarouselMapper;
import com.imooc.pojo.Carousel;
import com.imooc.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CarouseServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public List<Carousel> queryAll(Integer isShow) {

        //构造查询Example
        Example example =new Example(Carousel.class);

        //按照顺序排序
        example.orderBy("sort").desc();

        Example.Criteria criteria = example.createCriteria();

        //构造查询条件
        criteria.andEqualTo("isShow",isShow);

        //查询
        List<Carousel> carouselList = carouselMapper.selectByExample(example);

        return carouselList;
    }
}
