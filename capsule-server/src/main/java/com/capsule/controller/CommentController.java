package com.capsule.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.capsule.common.BaseContext;
import com.capsule.common.Result;
import com.capsule.entity.Comment;
import com.capsule.entity.User;
import com.capsule.exception.BusinessException;
import com.capsule.service.CommentService;
import com.capsule.service.UserService;
import com.capsule.utils.BaiduCensorUtil; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    // 注入百度AI内容安全审核工具
    @Autowired
    private BaiduCensorUtil baiduCensorUtil;

    /**
     * 发表评论
     */
    @PostMapping
    public Result<String> addComment(@RequestBody Comment comment) {
        // 3. 拦截校验：防止违规评论入库
        if (comment != null && comment.getContent() != null) {
            if (!baiduCensorUtil.checkText(comment.getContent())) {
                throw new BusinessException("评论失败：内容包含不合规或违规词汇，请文明发言！");
            }
        }

        comment.setUserId(BaseContext.getCurrentId());
        commentService.save(comment);
        return Result.success("评论发表成功");
    }

   /**
 * 删除评论
 * 普通用户：只能删除自己的评论
 * 管理员：可以删除所有评论
 */
@DeleteMapping("/{id}")
public Result<String> deleteComment(@PathVariable Long id) {

    // 查询评论
    Comment comment = commentService.getById(id);

    if (comment == null) {
        throw new BusinessException("评论不存在");
    }

    // 当前登录用户ID
    Long currentUserId = BaseContext.getCurrentId();

    // 查询当前登录用户
    User currentUser = userService.getById(currentUserId);

    if (currentUser == null) {
        throw new BusinessException("用户不存在");
    }

    // 如果当前用户是管理员，直接删除
    if ("ADMIN".equals(currentUser.getRole())) {
        commentService.removeById(id);
        return Result.success("评论已删除");
    }

    // 普通用户只能删除自己的评论
    if (!comment.getUserId().equals(currentUserId)) {
        throw new BusinessException("无权删除该评论");
    }

    // 删除评论
    commentService.removeById(id);

    return Result.success("评论已删除");
}

    /**
     * 查看某个胶囊的评论列表（分页）
     */
    @GetMapping("/list/{capsuleId}")
    public Result<IPage<Comment>> getComments(
            @PathVariable Long capsuleId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        IPage<Comment> result = commentService.getCommentsByCapsuleId(capsuleId, page, size);
        return Result.success(result);
    }
}