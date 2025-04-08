package com.wzzz.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wzzz.shortlink.project.dao.entity.ShortLinkDO;
import com.wzzz.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.wzzz.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.wzzz.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.wzzz.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.wzzz.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.wzzz.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import com.wzzz.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.wzzz.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.wzzz.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam);

    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) throws IOException;

    /**
     * 批量创建短链接
     *
     * @param requestParam 批量创建短链接请求参数
     * @return 批量创建短链接返回参数
     */
    ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam);


    /**
     * 短链接统计
     *
     * @param fullShortUrl         完整短链接
     * @param gid                  分组标识
     * @param shortLinkStatsRecord 短链接统计实体参数
     */
    void shortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO shortLinkStatsRecord);
}