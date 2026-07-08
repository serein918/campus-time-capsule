package com.capsule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capsule.entity.User;
import com.capsule.mapper.UserMapper;
import com.capsule.service.UserService;
import com.capsule.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import cn.hutool.crypto.digest.DigestUtil;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public boolean register(User user) {
        // 校验用户名是否已存在
        Long count = this.baseMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername())
        );
        if (count > 0) {
            throw new BusinessException("该用户名已被注册");
        }
        // 密码加密存储（MD5）
        user.setPassword(DigestUtil.md5Hex(user.getPassword()));
        user.setRole("USER");
        user.setStatus(1);
        if (!StringUtils.hasText(user.getNickname())) {
            user.setNickname("用户" + System.currentTimeMillis() % 100000);
        }
        return this.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = this.baseMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        if (user == null || !user.getPassword().equals(DigestUtil.md5Hex(password))) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("该账号已被管理员禁用");
        }
        return user;
    }

    @Override
    public User getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null); // 不返回密码
        return user;
    }

    @Override
    public boolean updateUserInfo(User user) {
        // 不允许通过此接口修改密码和角色
        user.setPassword(null);
        user.setRole(null);
        return this.updateById(user);
    }

    @Override
    public IPage<User> getUserPage(int page, int size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        IPage<User> result = this.page(new Page<>(page, size), wrapper);
        // 清除密码字段
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        return this.updateById(user);
    }
}
