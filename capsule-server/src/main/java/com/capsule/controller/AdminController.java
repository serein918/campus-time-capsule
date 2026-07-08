package com.capsule.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.capsule.common.BaseContext;
import com.capsule.common.Result;
import com.capsule.entity.*;
import com.capsule.exception.BusinessException;
import com.capsule.service.*;
import com.capsule.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private CapsuleService capsuleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private CategoryService categoryService;

    // ==================== 用户管理 ====================

    /**
     * 分页查询用户列表
     */
    @GetMapping("/user/list")
    public Result<IPage<User>> userList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<User> result = userService.getUserPage(page, size, keyword);
        return Result.success(result);
    }

    /**
     * 禁用/启用用户
     */
    @PutMapping("/user/status/{userId}")
    public Result<String> updateUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        userService.updateUserStatus(userId, status);
        return Result.success(status == 1 ? "用户已启用" : "用户已禁用");
    }

    // ==================== 胶囊管理 ====================

    /**
     * 管理员查看所有胶囊（分页）
     */
    @GetMapping("/capsule/list")
    public Result<IPage<Capsule>> capsuleList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<Capsule> result = capsuleService.adminGetCapsulePage(page, size, keyword);
        return Result.success(result);
    }

    /**
     * 管理员删除胶囊
     */
    @DeleteMapping("/capsule/{id}")
    public Result<String> deleteCapsule(@PathVariable Long id) {
        capsuleService.removeById(id);
        return Result.success("胶囊已删除");
    }

    // ==================== 评论管理 ====================

    /**
     * 管理员删除评论
     */
    @DeleteMapping("/comment/{id}")
    public Result<String> deleteComment(@PathVariable Long id) {
        commentService.removeById(id);
        return Result.success("评论已删除");
    }

    // ==================== 公告管理 ====================

    /**
     * 发布公告
     */
    @PostMapping("/notice")
    public Result<String> addNotice(@RequestBody Notice notice) {
        notice.setPublisherId(BaseContext.getCurrentId());
        notice.setStatus(1);
        noticeService.save(notice);
        return Result.success("公告发布成功");
    }

    /**
     * 修改公告
     */
    @PutMapping("/notice")
    public Result<String> updateNotice(@RequestBody Notice notice) {
        noticeService.updateById(notice);
        return Result.success("公告修改成功");
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/notice/{id}")
    public Result<String> deleteNotice(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success("公告已删除");
    }

    // ==================== 分类管理 ====================

    /**
     * 添加分类
     */
    @PostMapping("/category")
    public Result<String> addCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("分类添加成功");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/category/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success("分类已删除");
    }

    // ==================== 统计分析 ====================

    /**
     * 获取系统统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.count());
        stats.put("capsuleCount", capsuleService.count());
        stats.put("commentCount", commentService.count());
        stats.put("noticeCount", noticeService.count());
        return Result.success(stats);
    }
}
