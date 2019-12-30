package com.imooc.service;

import com.imooc.pojo.Carousel;

import java.util.List;

public interface CarouselService {

    /**
     * 查询轮播图所需要的数据
     * @param isShow
     * @return
     */
    public List<Carousel> queryAll(Integer isShow);
}
