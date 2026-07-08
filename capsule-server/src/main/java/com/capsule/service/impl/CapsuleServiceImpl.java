package com.capsule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capsule.common.BaseContext;
import com.capsule.entity.Capsule;
import com.capsule.exception.BusinessException;
import com.capsule.mapper.CapsuleMapper;
import com.capsule.service.CapsuleService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;

@Service
public class CapsuleServiceImpl extends ServiceImpl<CapsuleMapper, Capsule> implements CapsuleService {

    @Override
    public Capsule getSecureCapsuleDetail(Long id) {
        Capsule capsule = this.getById(id);
        if (capsule == null) {
            throw new BusinessException("该时光胶囊不存在或已被清理");
        }

        Long currentUserId = BaseContext.getCurrentId();

        // 如果当前访问者不是胶囊的主人
        if (!capsule.getOwnerId().equals(currentUserId)) {
            // 校验1：是否到了解封开启时间
            if (capsule.getOpenTime() != null && capsule.getOpenTime().isAfter(LocalDateTime.now())) {
                throw new BusinessException("该时光胶囊还在封印中，解封时间为: " + capsule.getOpenTime());
            }
            // 校验2：是否是私密胶囊
            if ("PRIVATE".equalsIgnoreCase(capsule.getVisibility())) {
                throw new BusinessException("该时光胶囊已被设定为私密状态，无法访问");
            }
        }

        // 累加浏览量
        capsule.setViewCount(capsule.getViewCount() + 1);
        this.updateById(capsule);
        return capsule;
    }

   @Override
public IPage<Capsule> getPublicSquarePage(int page, int size, String keyword, Long categoryId) {
    LambdaQueryWrapper<Capsule> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(Capsule::getVisibility, "PUBLIC")
            .eq(Capsule::getStatus, 1)
            .isNotNull(Capsule::getOpenTime)
            .le(Capsule::getOpenTime, LocalDateTime.now());
    if (StringUtils.hasText(keyword)) {
        wrapper.and(w -> w.like(Capsule::getTitle, keyword)
                .or().like(Capsule::getSummary, keyword));
    }
    if (categoryId != null) {
        wrapper.eq(Capsule::getCategoryId, categoryId);
    }
    wrapper.orderByDesc(Capsule::getCreateTime);
    return this.page(new Page<>(page, size), wrapper);
}


    @Override
    public IPage<Capsule> getMyCapsulePage(Long userId, int page, int size) {
        LambdaQueryWrapper<Capsule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Capsule::getOwnerId, userId)
                .orderByDesc(Capsule::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }

    @Override
    public IPage<Capsule> adminGetCapsulePage(int page, int size, String keyword) {
        LambdaQueryWrapper<Capsule> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Capsule::getTitle, keyword);
        }
        wrapper.orderByDesc(Capsule::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }
}
