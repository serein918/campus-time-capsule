package com.capsule.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.capsule.common.BaseContext;
import com.capsule.common.Result;
import com.capsule.entity.Capsule;
import com.capsule.entity.Favorite;
import com.capsule.service.CapsuleService;
import com.capsule.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private CapsuleService capsuleService;

    // 收藏/取消收藏
    @PostMapping("/{capsuleId}")
    public Result<Map<String, Object>> toggleFavorite(@PathVariable Long capsuleId) {
        Long userId = BaseContext.getCurrentId();
        boolean favorited = favoriteService.toggleFavorite(userId, capsuleId);
        Map<String, Object> data = new HashMap<>();
        data.put("favorited", favorited);
        data.put("message", favorited ? "收藏成功" : "已取消收藏");
        return Result.success(data);
    }

    // 查询是否已收藏
    @GetMapping("/status/{capsuleId}")
    public Result<Boolean> isFavorited(@PathVariable Long capsuleId) {
        Long userId = BaseContext.getCurrentId();
        return Result.success(favoriteService.isFavorited(userId, capsuleId));
    }

    // 我的收藏列表（返回胶囊详情）
    @GetMapping("/my")
    public Result<List<Capsule>> myFavorites() {
        Long userId = BaseContext.getCurrentId();
        List<Favorite> favList = favoriteService.list(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreateTime)
        );
        List<Capsule> capsules = new ArrayList<>();
        for (Favorite fav : favList) {
            Capsule capsule = capsuleService.getById(fav.getCapsuleId());
            if (capsule != null && capsule.getStatus() == 1) {
                capsules.add(capsule);
            }
        }
        return Result.success(capsules);
    }
}
