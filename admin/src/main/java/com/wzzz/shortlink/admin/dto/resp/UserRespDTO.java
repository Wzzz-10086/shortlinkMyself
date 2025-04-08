package com.wzzz.shortlink.admin.dto.resp;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wzzz.shortlink.admin.common.serializer.PhoneDesensitizationSerializer;
import lombok.Data;
// Data Transfer Object：数据传输对象，DTO用于在不同层之间传输数据
/**
 * 用户返回参数实体
 */

@Data
public class UserRespDTO {
    /**
     * ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;


    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    // 手机号脱敏
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

}
