package com.capsule.controller;

import com.capsule.common.BaseContext;
import com.capsule.common.Result;
import com.capsule.entity.User;
import com.capsule.service.UserService;
import com.capsule.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        userService.register(user);
        return Result.success("注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginUser) {
        User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
        String token = JwtUtils.generateToken(user.getId(), user.getRole());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("token", token);
        responseData.put("userId", user.getId());
        responseData.put("nickname", user.getNickname());
        responseData.put("avatar", user.getAvatar());
        responseData.put("role", user.getRole());

        return Result.success("登录成功", responseData);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        Long userId = BaseContext.getCurrentId();
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/update")
    public Result<String> updateUserInfo(@RequestBody User user) {
        user.setId(BaseContext.getCurrentId());
        userService.updateUserInfo(user);
        return Result.success("个人信息修改成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        Long userId = BaseContext.getCurrentId();
        User user = userService.getById(userId);
        // 验证旧密码
        String oldMd5 = cn.hutool.crypto.digest.DigestUtil.md5Hex(oldPassword);
        if (!user.getPassword().equals(oldMd5)) {
            return Result.error("原密码错误");
        }
        user.setPassword(cn.hutool.crypto.digest.DigestUtil.md5Hex(newPassword));
        userService.updateById(user);
        return Result.success("密码修改成功");
    }
}
