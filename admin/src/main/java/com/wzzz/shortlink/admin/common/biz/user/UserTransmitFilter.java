package com.wzzz.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.wzzz.shortlink.admin.common.convention.exception.ClientException;
import com.wzzz.shortlink.admin.common.convention.result.Results;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import static com.wzzz.shortlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;
import static com.wzzz.shortlink.admin.enums.UserErrorCode.USER_TOKEN_ERROR;

/**
 * 用户信息传输过滤器
 *
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    private List<String> urls = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username"
            );

    /**
     * 如果用户未登录，那么直接拦截该请求
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();
        if(!urls.contains(requestURI)){
            if(!(Objects.equals(requestURI, "/api/short-link/admin/v1/user") && Objects.equals(method, "POST"))){
                    String username = httpServletRequest.getHeader("username");
                    String token = httpServletRequest.getHeader("token");
                    // 如果 username 和 token 中任意一个为空，，抛出异常
                    if (!StrUtil.isAllNotBlank(username,token)){
                        try {
                            returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(USER_TOKEN_ERROR))));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                    Object userInfoJsonStr;
                    try {
                        userInfoJsonStr = stringRedisTemplate.opsForHash().get(USER_LOGIN_KEY + username, token);
                        if (userInfoJsonStr == null) {
                            throw new ClientException(USER_TOKEN_ERROR);
                        }
                    }catch (Exception ex){
                        try {
                            returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(new ClientException(USER_TOKEN_ERROR))));
                            return;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                        UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
                        UserContext.setUser(userInfoDTO);
            }

        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }


    private void returnJson(HttpServletResponse response, String json) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
            throw new ClientException(USER_TOKEN_ERROR);
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}


