package com.capsule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capsule.entity.Comment;
import com.capsule.mapper.CommentMapper;
import com.capsule.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public IPage<Comment> getCommentsByCapsuleId(Long capsuleId, int page, int size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getCapsuleId, capsuleId)
                .orderByDesc(Comment::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }
}
