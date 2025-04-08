package com.wzzz.shortlink.project.controller;


import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wzzz.shortlink.project.common.convention.result.Result;
import com.wzzz.shortlink.project.common.convention.result.Results;
import com.wzzz.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.wzzz.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.wzzz.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.wzzz.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.wzzz.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import com.wzzz.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.wzzz.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.wzzz.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.wzzz.shortlink.project.handler.CustomBlockHandler;
import com.wzzz.shortlink.project.service.ShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 创建短链接
     * @param requestParam
     * @return
     */
    @PostMapping("/api/short-link/v1/create")
    @SentinelResource(
            value = "create_short-link",
            blockHandler = "createShortLinkBlockHandlerMethod",
            blockHandlerClass = CustomBlockHandler.class
    )
    Result<ShortLinkCreateRespDTO>  createShortLink(@RequestBody ShortLinkCreateReqDTO  requestParam){
        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 分页查询短链接
     * @param requestParam
     * @return
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }
    /**
     * 查询短链接分组内数量
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(@RequestParam("requestParam") List<String> requestParam) {
        return Results.success(shortLinkService.listGroupShortLinkCount(requestParam));
    }

    /**
     * 修改短链接
     */
    @PostMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 短链接跳转
     * @param shortUri
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/{short-uri}")
    public void restoreUrl(@PathVariable("short-uri") String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        shortLinkService.restoreUrl(shortUri, request, response);
    }

    /**
     * 批量创建短链接
     */
    @PostMapping("/api/short-link/v1/create/batch")
    public Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam) {
        return Results.success(shortLinkService.batchCreateShortLink(requestParam));
    }


}
