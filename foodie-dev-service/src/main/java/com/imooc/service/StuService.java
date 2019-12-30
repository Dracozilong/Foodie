package com.imooc.service;

import com.imooc.pojo.Stu;

public interface StuService {

    //根据ID查找一个Stu信息
    public Stu getStuInfo(Integer id);

    //新增一个Stu信息
    public void saveStu();

    //修改一个Stu信息
    public void updateStu(Integer id);

    //删除一个Stu信息
    public void deleteStu(Integer id);

}
