package com.wzzz.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wzzz.shortlink.admin.common.biz.user.UserContext;
import com.wzzz.shortlink.admin.common.convention.exception.ServiceException;
import com.wzzz.shortlink.admin.common.convention.result.Result;
import com.wzzz.shortlink.admin.dao.entity.GroupDO;
import com.wzzz.shortlink.admin.dao.mapper.GroupMapper;
import com.wzzz.shortlink.admin.remote.ShortLinkRemoteService;
import com.wzzz.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.wzzz.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.wzzz.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * URL 回收站接口实现层
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    private final GroupMapper groupMapper;

    /**
     * 后续重构为 SpringCloud Feign 调用
     */
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    /**
     * 查询当前用户下的所有分组gid信息
     * @param requestParam 请求参数
     * @return
     */
    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        //查询当前用户的所有分组信息
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户无分组信息");
        }
        requestParam.setGidList(groupDOList.stream().map(GroupDO::getGid).toList());
        return shortLinkRemoteService.pageRecycleBinShortLink(requestParam);
    }
}
