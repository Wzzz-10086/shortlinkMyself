package com.wzzz.shortlink.admin.dto.req;


import lombok.Data;

@Data
public class GroupUpdateDTO {
    /**
     * 分组ID
     */
    String gid;


    /**
     * 分组名
     */
    String name;

}
