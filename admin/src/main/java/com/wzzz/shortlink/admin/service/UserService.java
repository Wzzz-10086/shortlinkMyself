package com.wzzz.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzzz.shortlink.admin.dao.entity.UserDO;
import com.wzzz.shortlink.admin.dto.req.UserLoginReqDTO;
import com.wzzz.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.wzzz.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.wzzz.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.wzzz.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名返回用户信息
     * @param username 用户名
     * @return
     */
    UserRespDTO getUserByUserName(String username);

    Boolean hasUserName(String username);

    void register(UserRegisterReqDTO requestParam);

    void update(UserUpdateReqDTO userUpdateReqDTO);

    UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO);

    Boolean checkLogin(String username, String token);

    void logout(String username, String token);

    UserRespDTO getUserByUsername(String username);
}
