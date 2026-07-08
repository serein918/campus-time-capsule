package com.capsule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.capsule.entity.User;

public interface UserService extends IService<User> {
    boolean register(User user);
    User login(String username, String password);
    User getUserInfo(Long userId);
    boolean updateUserInfo(User user);
    IPage<User> getUserPage(int page, int size, String keyword);
    boolean updateUserStatus(Long userId, Integer status);
}
