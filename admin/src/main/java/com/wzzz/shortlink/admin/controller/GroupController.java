package com.wzzz.shortlink.admin.controller;


import com.wzzz.shortlink.admin.common.convention.result.Result;
import com.wzzz.shortlink.admin.common.convention.result.Results;
import com.wzzz.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.wzzz.shortlink.admin.dto.req.GroupSortReqDTO;
import com.wzzz.shortlink.admin.dto.req.GroupUpdateDTO;
import com.wzzz.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.wzzz.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//实际上是 @Controller 和 @ResponseBody 的结合体
@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;


// uri中不能有多余空格
    @PostMapping("/api/short-link/admin/v1/group")
    Result<Void>saveGroup(@RequestBody GroupSaveReqDTO requestParam){
        groupService.saveGroup(requestParam.getName());
        return Results.success();
    }

    @GetMapping("/api/short-link/admin/v1/group")
    Result<List<ShortLinkGroupRespDTO>> listGroup(){
        return Results.success(groupService.listGroup());
    }

    @PutMapping("/api/short-link/admin/v1/group")
    Result<Void>groupUpdate(@RequestBody GroupUpdateDTO updateDTO){
        groupService.update(updateDTO);
        return Results.success();
    }

    @DeleteMapping("/api/short-link/admin/v1/group")
    Result<Void>groupDelete(@RequestParam String gid){
        groupService.deleteGroup(gid);
        return Results.success();
    }

    @PostMapping("/api/short-link/admin/v1/group/sort")
    Result<Void>sort(@RequestBody List<GroupSortReqDTO>requestParam){
        groupService.sort(requestParam);
        return Results.success();
    }
}
