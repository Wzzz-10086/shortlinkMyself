package com.wzzz.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzzz.shortlink.admin.common.biz.user.UserContext;
import com.wzzz.shortlink.admin.common.convention.exception.ClientException;
import com.wzzz.shortlink.admin.common.convention.exception.ServiceException;
import com.wzzz.shortlink.admin.dao.entity.UserDO;
import com.wzzz.shortlink.admin.dao.mapper.UserMapper;
import com.wzzz.shortlink.admin.dto.req.UserLoginReqDTO;
import com.wzzz.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.wzzz.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.wzzz.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.wzzz.shortlink.admin.dto.resp.UserRespDTO;
import com.wzzz.shortlink.admin.enums.UserErrorCode;
import com.wzzz.shortlink.admin.service.GroupService;
import com.wzzz.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.wzzz.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.wzzz.shortlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;
import static com.wzzz.shortlink.admin.enums.UserErrorCode.USER_EXIST;
import static com.wzzz.shortlink.admin.enums.UserErrorCode.USER_REGISTER_FAIL;

/**
 * 用户接口层实现层
 */
@Service
/**
 * 构造器注入
 */
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final GroupService groupService;

    /**
     * 根据用户名返回用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public UserRespDTO getUserByUserName(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        UserRespDTO result = new UserRespDTO();
        if (userDO == null) {
            throw new ServiceException(UserErrorCode.USER_NULL);
        }
        BeanUtils.copyProperties(userDO,result);
        return result;

    }

    /**
     * 查询用户名是否存在
     * 存在返回true，不存在返回false
     * @param username
     * @return
     */
    @Override
    public Boolean hasUserName(String username) {
//        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
//        UserDO userDO = baseMapper.selectOne(queryWrapper);
//        return userDO != null;
        return userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register( UserRegisterReqDTO requestParam) {
        if (hasUserName(requestParam.getUsername())) {
            throw new ClientException(USER_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        if (!lock.tryLock()) {
            throw new ClientException(USER_EXIST);
        }
        try{
            int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
            if (inserted < 1) {
                throw new ClientException(USER_REGISTER_FAIL);
            }
            groupService.saveGroup(requestParam.getUsername(), "默认分组");
            userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
        }catch(DuplicateKeyException e){
            throw new ClientException(USER_EXIST);
        }
        finally {
            lock.unlock();
        }

    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        if (!Objects.equals(requestParam.getUsername(), UserContext.getUsername())) {
            throw new ClientException("当前登录用户修改请求异常");
        }
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam,UserDO.class),queryWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO userLoginReqDTO) {
        //查询是否有该用户
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, userLoginReqDTO.getUsername())
                .eq(UserDO::getPassword, userLoginReqDTO.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        // 用户查讯为空
        if (userDO == null) {
            throw new ClientException(UserErrorCode.USER_LOGIN_ERROR);
        }
        Map<Object ,Object> hasLoginMap = stringRedisTemplate.opsForHash().entries(USER_LOGIN_KEY + userLoginReqDTO.getUsername());
        // 已经登录
        if (CollUtil.isNotEmpty(hasLoginMap)) {
            stringRedisTemplate.expire(USER_LOGIN_KEY + userLoginReqDTO.getUsername(),30L, TimeUnit.MINUTES);
            String token = hasLoginMap.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() -> new ClientException("用户登录错误"));
            return new UserLoginRespDTO(token);
        }

        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY + userLoginReqDTO.getUsername(),uuid, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(USER_LOGIN_KEY + userLoginReqDTO.getUsername(),30L, TimeUnit.MINUTES);

        return new UserLoginRespDTO(uuid);
    }


    /**
     * 用户是否登陆成功 true则已登录，false则未登录
     * @param username
     * @param token
     * @return
     */
    @Override
    public Boolean checkLogin(String username, String token) {
        boolean flag = stringRedisTemplate.opsForHash().get(USER_LOGIN_KEY + username, token) != null;
        return flag;
    }

    @Override
    public void logout(String username, String token) {
        // 用户已登录
        if (checkLogin(username,token)){
            stringRedisTemplate.delete(USER_LOGIN_KEY + username);
            return;
        }
        //用户未登录或者token不存在
        throw new ClientException(UserErrorCode.USER_NOT_LOGIN);
    }

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(UserErrorCode.USER_NULL);
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

}
