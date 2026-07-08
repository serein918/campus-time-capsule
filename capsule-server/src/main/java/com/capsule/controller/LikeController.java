package com.capsule.controller;

import com.capsule.common.BaseContext;
import com.capsule.common.Result;
import com.capsule.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * 点赞/取消点赞（切换）
     */
    @PostMapping("/{capsuleId}")
    public Result<Map<String, Object>> toggleLike(@PathVariable Long capsuleId) {
        Long userId = BaseContext.getCurrentId();
        boolean liked = likeService.toggleLike(userId, capsuleId);
        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        data.put("message", liked ? "点赞成功" : "已取消点赞");
        return Result.success(data);
    }

    /**
     * 查询是否已点赞
     */
    @GetMapping("/status/{capsuleId}")
    public Result<Boolean> isLiked(@PathVariable Long capsuleId) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(likeService.isLiked(userId, capsuleId));
    }
}
