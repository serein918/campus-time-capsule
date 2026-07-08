package com.capsule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capsule.entity.Capsule;
import com.capsule.entity.LikeRecord;
import com.capsule.mapper.LikeRecordMapper;
import com.capsule.service.CapsuleService;
import com.capsule.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord> implements LikeService {

    @Autowired
    private CapsuleService capsuleService;

    @Override
    public boolean toggleLike(Long userId, Long capsuleId) {
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getUserId, userId).eq(LikeRecord::getCapsuleId, capsuleId);
        LikeRecord existing = this.getOne(wrapper);

        Capsule capsule = capsuleService.getById(capsuleId);
        if (capsule == null) return false;

        if (existing != null) {
            // 取消点赞
            this.removeById(existing.getId());
            capsule.setLikeCount(Math.max(0, capsule.getLikeCount() - 1));
            capsuleService.updateById(capsule);
            return false;
        } else {
            // 点赞
            LikeRecord likeRecord = new LikeRecord();
            likeRecord.setUserId(userId);
            likeRecord.setCapsuleId(capsuleId);
            this.save(likeRecord);
            capsule.setLikeCount(capsule.getLikeCount() + 1);
            capsuleService.updateById(capsule);
            return true;
        }
    }

    @Override
    public boolean isLiked(Long userId, Long capsuleId) {
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getUserId, userId).eq(LikeRecord::getCapsuleId, capsuleId);
        return this.count(wrapper) > 0;
    }
}
