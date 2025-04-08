package com.wzzz.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wzzz.shortlink.admin.common.database.BaseDO;
import lombok.Data;
// Data Access Objects：数据访问对象
// 用于封装对数据库的访问
/**
 * @description t_user
 * @author BEJSON
 * @date 2025-03-15
 */
@Data
@TableName("t_user")
public class UserDO extends BaseDO {
    /**
     * ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 注销时间戳
     */
    private Long deletionTime;

}
