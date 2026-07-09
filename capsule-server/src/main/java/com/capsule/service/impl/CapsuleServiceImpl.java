package com.capsule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capsule.common.BaseContext;
import com.capsule.entity.Capsule;
import com.capsule.entity.User;
import com.capsule.exception.BusinessException;
import com.capsule.mapper.CapsuleMapper;
import com.capsule.service.CapsuleService;
import com.capsule.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import com.capsule.utils.BaiduCensorUtil;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CapsuleServiceImpl extends ServiceImpl<CapsuleMapper, Capsule> implements CapsuleService {

    @Autowired
    private BaiduCensorUtil baiduCensorUtil;

    @Autowired
    private UserService userService;

    @Override
    public boolean save(Capsule entity) {
        if (entity != null) {
            StringBuilder fullText = new StringBuilder();
            if (entity.getTitle() != null) fullText.append(entity.getTitle()).append(" ");
            if (entity.getSummary() != null) fullText.append(entity.getSummary()).append(" ");
            if (entity.getContent() != null) fullText.append(entity.getContent());

            if (fullText.length() > 0 && !baiduCensorUtil.checkText(fullText.toString())) {
                throw new BusinessException("投递失败：胶囊内容（标题/摘要/正文）包含不合规或违规词汇，请修改后重试！");
            }
        }
        return super.save(entity);
    }

    @Override
    public boolean updateById(Capsule entity) {
        if (entity != null) {
            StringBuilder fullText = new StringBuilder();
            if (entity.getTitle() != null) fullText.append(entity.getTitle()).append(" ");
            if (entity.getSummary() != null) fullText.append(entity.getSummary()).append(" ");
            if (entity.getContent() != null) fullText.append(entity.getContent());

            if (fullText.length() > 0 && !baiduCensorUtil.checkText(fullText.toString())) {
                throw new BusinessException("修改失败：修改后的内容包含不合规或违规词汇！");
            }
        }
        return super.updateById(entity);
    }

    @Override
    public Capsule getSecureCapsuleDetail(Long id) {
        Capsule capsule = this.getById(id);
        if (capsule == null) {
            throw new BusinessException("该时光胶囊不存在或已被清理");
        }

        Long currentUserId = BaseContext.getCurrentId();

        if (!capsule.getOwnerId().equals(currentUserId)) {
            if (capsule.getOpenTime() != null && capsule.getOpenTime().isAfter(LocalDateTime.now())) {
                throw new BusinessException("该时光胶囊还在封印中，解封时间为: " + capsule.getOpenTime());
            }
            if ("PRIVATE".equalsIgnoreCase(capsule.getVisibility())) {
                throw new BusinessException("该时光胶囊已被设定为私密状态，无法访问");
            }
        }

        // 累加浏览量
        capsule.setViewCount(capsule.getViewCount() + 1);
        super.updateById(capsule);

        // 拼接发布者信息
        fillOwnerInfo(capsule);
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
        IPage<Capsule> result = this.page(new Page<>(page, size), wrapper);

        // 为每条记录拼接发布者信息
        for (Capsule capsule : result.getRecords()) {
            fillOwnerInfo(capsule);
        }
        return result;
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

    // 根据匿名设置填充发布者展示信息
    private void fillOwnerInfo(Capsule capsule) {
        if (capsule.getIsAnonymous() != null && capsule.getIsAnonymous() == 1) {
            capsule.setOwnerNickname("匿名用户");
            capsule.setOwnerAvatar(null);
        } else {
            User owner = userService.getById(capsule.getOwnerId());
            if (owner != null) {
                capsule.setOwnerNickname(owner.getNickname());
                capsule.setOwnerAvatar(owner.getAvatar());
            }
        }
    }
}
