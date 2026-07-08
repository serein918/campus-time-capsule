package com.capsule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.capsule.entity.Favorite;

public interface FavoriteService extends IService<Favorite> {
    boolean toggleFavorite(Long userId, Long capsuleId);
    boolean isFavorited(Long userId, Long capsuleId);
}
