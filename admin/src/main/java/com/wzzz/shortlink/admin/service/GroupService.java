package com.wzzz.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzzz.shortlink.admin.dao.entity.GroupDO;
import com.wzzz.shortlink.admin.dto.req.GroupSortReqDTO;
import com.wzzz.shortlink.admin.dto.req.GroupUpdateDTO;
import com.wzzz.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO>{
    void saveGroup(String groupName);


    void saveGroup(String username,String groupName);

    List<ShortLinkGroupRespDTO> listGroup();

    void update(GroupUpdateDTO updateDTO);

    void deleteGroup(String gid);

    void sort(List<GroupSortReqDTO> requestParam);
}
