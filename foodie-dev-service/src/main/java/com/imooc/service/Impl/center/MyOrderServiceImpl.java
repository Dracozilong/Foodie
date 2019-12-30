package com.imooc.service.Impl.center;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.OrdersMapperCustom;
import com.imooc.pojo.vo.MyOrderVo;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyOrderServiceImpl implements MyOrderService {

    @Autowired
    private OrdersMapperCustom ordersMapperCustom;

    @Override
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize) {

        Map<String,Object> map = new HashMap<>();

        map.put("userId",userId);

        //分页
        PageHelper.startPage(page, pageSize);

        if (orderStatus!=null){
            map.put("orderStatus",orderStatus);
        }
        List<MyOrderVo> list = ordersMapperCustom.queryMyOrders(map);

        PagedGridResult pagedGridResult = setterPagedGrid(list, page);

        return pagedGridResult;

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
}
