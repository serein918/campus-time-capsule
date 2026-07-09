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
import com.capsule.utils.BaiduCensorUtil;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CapsuleServiceImpl extends ServiceImpl<CapsuleMapper, Capsule> implements CapsuleService {

    // 注入百度AI内容安全审核工具类
    @Autowired
    private BaiduCensorUtil baiduCensorUtil;

    /**
     * 重写 MyBatis-Plus 自带的 save(T entity) 方法
     * 在用户“创建胶囊”进入数据库前强制进行安全审核
     */
    @Override
    public boolean save(Capsule entity) {
        if (entity != null) {
            // 审核标题
            if (!baiduCensorUtil.checkText(entity.getTitle())) {
                throw new BusinessException("投递失败：胶囊标题包含不合规或违规词汇，请修改后重试！");
            }
            // 审核摘要
            if (!baiduCensorUtil.checkText(entity.getSummary())) {
                throw new BusinessException("投递失败：胶囊摘要包含不合规或违规词汇，请修改后重试！");
            }
            // 审核正文内容
            if (!baiduCensorUtil.checkText(entity.getContent())) {
                throw new BusinessException("投递失败：胶囊正文包含不合规或违规词汇，请修改后重试！");
            }
        }
        return super.save(entity); // 审核通过，执行原有的保存逻辑
    }

    /**
     * 重写 MyBatis-Plus 自带的 updateById(T entity) 方法
     * 防止用户在“修改胶囊”时填入违规内容
     */
    @Override
    public boolean updateById(Capsule entity) {
        if (entity != null) {
            // 如果用户修改了标题，则进行审核
            if (entity.getTitle() != null && !baiduCensorUtil.checkText(entity.getTitle())) {
                throw new BusinessException("修改失败：修改后的标题包含不合规或违规词汇！");
            }
            // 如果用户修改了摘要，则进行审核
            if (entity.getSummary() != null && !baiduCensorUtil.checkText(entity.getSummary())) {
                throw new BusinessException("修改失败：修改后的摘要包含不合规或违规词汇！");
            }
            // 如果用户修改了正文，则进行审核
            if (entity.getContent() != null && !baiduCensorUtil.checkText(entity.getContent())) {
                throw new BusinessException("修改失败：修改后的正文包含不合规或违规词汇！");
            }
        }
        return super.updateById(entity); // 审核通过，执行原有的更新逻辑
    }

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
