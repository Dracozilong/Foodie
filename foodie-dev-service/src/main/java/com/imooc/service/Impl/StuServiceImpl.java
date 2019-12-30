package com.imooc.service.Impl;

import com.imooc.mapper.StuMapper;
import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StuServiceImpl implements StuService {

    @Autowired
    private StuMapper stuMapper;

    @Override
    public Stu getStuInfo(Integer id) {
        return stuMapper.selectByPrimaryKey(id);
    }

    @Override
    public void saveStu() {
      Stu stu = new Stu();
      stu.setName("dynames");
      stu.setAge(19);
      stuMapper.insert(stu);
    }

    @Override
    public void updateStu(Integer id) {
      Stu stu = new Stu();
      stu.setId(id);
      stu.setAge(20);
      stu.setName("exia");
      stuMapper.updateByPrimaryKey(stu);
    }

    @Override
    public void deleteStu(Integer id) {
      stuMapper.deleteByPrimaryKey(id);
    }
}
