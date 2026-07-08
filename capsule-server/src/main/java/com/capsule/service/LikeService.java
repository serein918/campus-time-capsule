package com.capsule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.capsule.entity.LikeRecord;

public interface LikeService extends IService<LikeRecord> {
    boolean toggleLike(Long userId, Long capsuleId);
    boolean isLiked(Long userId, Long capsuleId);
}
