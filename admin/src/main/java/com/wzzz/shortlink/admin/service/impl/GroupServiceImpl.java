package com.wzzz.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzzz.shortlink.admin.common.biz.user.UserContext;
import com.wzzz.shortlink.admin.common.convention.exception.ClientException;
import com.wzzz.shortlink.admin.common.convention.result.Result;
import com.wzzz.shortlink.admin.dao.entity.GroupDO;
import com.wzzz.shortlink.admin.dao.mapper.GroupMapper;
import com.wzzz.shortlink.admin.dto.req.GroupSortReqDTO;
import com.wzzz.shortlink.admin.dto.req.GroupUpdateDTO;
import com.wzzz.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.wzzz.shortlink.admin.remote.ShortLinkRemoteService;
import com.wzzz.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.wzzz.shortlink.admin.service.GroupService;
import com.wzzz.shortlink.admin.toolkit.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.wzzz.shortlink.admin.common.constant.RedisCacheConstant.LOCK_GROUP_CREATE_KEY;


@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    private final RedissonClient redissonClient;

    @Value("${short-link.group.max-num}")
    private Integer groupMaxNum;

    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @Override
    public void saveGroup(String groupName) {
       this.saveGroup(UserContext.getUsername(),groupName);
    }

    @Override
    public void saveGroup(String username, String groupName) {
        // 检查是否GID有重名
        RLock lock = redissonClient.getLock(String.format(LOCK_GROUP_CREATE_KEY, username));
        lock.lock();
        try {
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, 0);
            List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(groupDOList) && groupDOList.size() >= groupMaxNum) {
                throw new ClientException(String.format("已超出最大分组数：%d", groupMaxNum));
            }
            String gid;
            do {
                gid = RandomUtil.generateRandom();
            } while (!hasGid(username, gid));
            GroupDO groupDO = GroupDO.builder()
                    .gid(gid)
                    .sortOrder(0)
                    .username(username)
                    .name(groupName)
                    .build();
            baseMapper.insert(groupDO);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 查询该用户名下的所有分组
     * @return List<ShortLinkGroupRespDTO> 返回当前用户的所有分组信息，包含每个分组的短链接数量
     */


    //TODO 不是很懂
    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        // 构建查询条件：查询当前用户、未删除的分组，并按排序顺序和更新时间降序排列
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0)
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);

        // 执行查询，获取分组列表
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);


        List<String> mylist = groupDOList.stream().map(GroupDO::getGid).toList();
        // 调用远程服务，获取每个分组的短链接数量统计
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkRemoteService
                .listGroupShortLinkCount(mylist);

        // 将分组实体列表转换为DTO对象列表
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOList = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);

        // 为每个分组DTO设置对应的短链接数量
        shortLinkGroupRespDTOList.forEach(each -> {
            // 在当前分组的统计结果中查找匹配的统计信息
            Optional<ShortLinkGroupCountQueryRespDTO> first = listResult.getData().stream()
                    .filter(item -> Objects.equals(item.getGid(), each.getGid()))
                    .findFirst();
            // 如果找到统计信息，则设置到DTO中
            first.ifPresent(item -> each.setShortLinkCount(first.get().getShortLinkCount()));
        });

        // 返回包含完整信息的分组DTO列表
        return shortLinkGroupRespDTOList;
    }

    @Override
    public void update(GroupUpdateDTO updateDTO) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, updateDTO.getGid())
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        System.out.println(UserContext.getUsername());
        GroupDO groupDO = new GroupDO();
        groupDO.setName(updateDTO.getName());
        baseMapper.update(groupDO,queryWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO,queryWrapper);
    }

    @Override
    public void sort(List<GroupSortReqDTO> requestParam) {
        requestParam.forEach(each -> {
            GroupDO groupDO = GroupDO.builder()
                    .gid(each.getGid())
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                   .eq(GroupDO::getGid, each.getGid())
                   .eq(GroupDO::getUsername, UserContext.getUsername())
                   .eq(GroupDO::getDelFlag, 0);
            baseMapper.update(groupDO,queryWrapper);
        });
    }


    /**
     * 检查表中是否含有重复的gid
     * @param gid
     * @return
     */
    public Boolean hasGid(String username,String gid){
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername,Optional.ofNullable(username).orElse(UserContext.getUsername()));
        GroupDO hasGidFlag = baseMapper.selectOne(queryWrapper);
        return  hasGidFlag != null;

    }
}
