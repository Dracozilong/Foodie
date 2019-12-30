package com.imooc.service.center;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.CenterUserBO;

public interface CenterUserService {

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);

    /**
     * 修改用户信息
     * @param userId
     * @param centerUserBO
     * @return
     */
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO);


    /**
     * 更新用户头像
     * @param userId
     * @param faceUrl
     * @return
     */
    public Users updateUserFace(String userId,String faceUrl);
}
