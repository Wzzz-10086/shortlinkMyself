package com.wzzz.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wzzz.shortlink.project.dao.entity.ShortLinkDO;
import com.wzzz.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.wzzz.shortlink.project.dto.req.RecycleBinRemoveReqDTO;
import com.wzzz.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.wzzz.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.wzzz.shortlink.project.dto.resp.ShortLinkPageRespDTO;


/**
 * 回收站管理接口层
 */
public interface RecycleBinService extends IService<ShortLinkDO> {

    /**
     * 保存回收站
     *
     * @param requestParam 请求参数
     */
    void saveRecycleBin(RecycleBinSaveReqDTO requestParam);

    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam);

    void recoverRecycleBin(RecycleBinRecoverReqDTO requestParam);

    void removeRecycleBin(RecycleBinRemoveReqDTO requestParam);
}
