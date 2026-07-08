package com.capsule.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.capsule.common.BaseContext;
import com.capsule.common.Result;
import com.capsule.entity.Capsule;
import com.capsule.exception.BusinessException;
import com.capsule.service.CapsuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/capsule")
public class CapsuleController {

    @Autowired
    private CapsuleService capsuleService;

    /**
     * 创建时光胶囊
     */
    @PostMapping
    public Result<String> createCapsule(@RequestBody Capsule capsule) {
        capsule.setOwnerId(BaseContext.getCurrentId());
        capsule.setLikeCount(0);
        capsule.setViewCount(0);
        capsule.setFavoriteCount(0);
        capsule.setStatus(1);
        capsuleService.save(capsule);
        return Result.success("时光胶囊投递成功");
    }

    /**
     * 根据ID查看胶囊详情
     */
    @GetMapping("/{id}")
    public Result<Capsule> getCapsuleById(@PathVariable Long id) {
        Capsule capsule = capsuleService.getSecureCapsuleDetail(id);
        return Result.success(capsule);
    }

    /**
     * 修改胶囊
     */
    @PutMapping
    public Result<String> updateCapsule(@RequestBody Capsule capsule) {
        Capsule existing = capsuleService.getById(capsule.getId());
        if (existing == null || !existing.getOwnerId().equals(BaseContext.getCurrentId())) {
            throw new BusinessException("无权修改该胶囊");
        }
        capsule.setOwnerId(null); // 不允许修改所有者
        capsuleService.updateById(capsule);
        return Result.success("胶囊修改成功");
    }

    /**
     * 删除胶囊
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCapsule(@PathVariable Long id) {
        Capsule capsule = capsuleService.getById(id);
        if (capsule == null || !capsule.getOwnerId().equals(BaseContext.getCurrentId())) {
            throw new BusinessException("无权删除该胶囊");
        }
        capsuleService.removeById(id);
        return Result.success("胶囊已删除");
    }

    /**
     * 查询我的胶囊（分页）
     */
    @GetMapping("/my")
    public Result<IPage<Capsule>> getMyCapsules(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = BaseContext.getCurrentId();
        IPage<Capsule> result = capsuleService.getMyCapsulePage(userId, page, size);
        return Result.success(result);
    }

    /**
     * 公开广场（分页+搜索+分类筛选）
     */
    @GetMapping("/square")
    public Result<IPage<Capsule>> getSquareCapsules(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        IPage<Capsule> result = capsuleService.getPublicSquarePage(page, size, keyword, categoryId);
        return Result.success(result);
    }
}
