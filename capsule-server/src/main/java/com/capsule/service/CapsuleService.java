package com.capsule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.capsule.entity.Capsule;

public interface CapsuleService extends IService<Capsule> {
    Capsule getSecureCapsuleDetail(Long id);
    IPage<Capsule> getPublicSquarePage(int page, int size, String keyword, Long categoryId);
    IPage<Capsule> getMyCapsulePage(Long userId, int page, int size);
    IPage<Capsule> adminGetCapsulePage(int page, int size, String keyword);
}
