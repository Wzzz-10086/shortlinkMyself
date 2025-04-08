package com.wzzz.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class GroupSortReqDTO {
    /**
     * 分组号
     */
    private String gid;
    /**
     *  排序号
     */
    private Integer sortOrder;
}
