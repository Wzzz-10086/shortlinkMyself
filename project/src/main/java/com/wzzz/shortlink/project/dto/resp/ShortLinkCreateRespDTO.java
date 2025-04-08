package com.wzzz.shortlink.project.dto.resp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接创建响应DTO
 * 用于返回短链接创建后的相关信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkCreateRespDTO {

    /**
     * 分组编号
     */
    private String gid;

    /**
     * 原始URL
     */
    private String originUrl;

    /**
     * 完整短链接URL
     */
    private String fullShortUrl;

}
