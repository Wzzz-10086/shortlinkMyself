package com.wzzz.shortlink.project.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzzz.shortlink.project.dao.entity.ShortLinkDO;
import lombok.Data;

@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO> {
    private String gid;

    /**
     * 排序标识
     */
    private String orderTag;
}
