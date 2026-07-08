package com.capsule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capsule.entity.Capsule;
import com.capsule.entity.Favorite;
import com.capsule.mapper.FavoriteMapper;
import com.capsule.service.CapsuleService;
import com.capsule.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private CapsuleService capsuleService;

    @Override
    public boolean toggleFavorite(Long userId, Long capsuleId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getCapsuleId, capsuleId);
        Favorite existing = this.getOne(wrapper);

        Capsule capsule = capsuleService.getById(capsuleId);
        if (capsule == null) return false;

        if (existing != null) {
            // 取消收藏
            this.removeById(existing.getId());
            capsule.setFavoriteCount(Math.max(0, capsule.getFavoriteCount() - 1));
            capsuleService.updateById(capsule);
            return false; // 返回false表示取消
        } else {
            // 添加收藏
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setCapsuleId(capsuleId);
            this.save(favorite);
            capsule.setFavoriteCount(capsule.getFavoriteCount() + 1);
            capsuleService.updateById(capsule);
            return true; // 返回true表示收藏成功
        }
    }

    @Override
    public boolean isFavorited(Long userId, Long capsuleId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getCapsuleId, capsuleId);
        return this.count(wrapper) > 0;
    }
}
