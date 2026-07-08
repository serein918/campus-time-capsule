package com.capsule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.capsule.entity.Comment;

public interface CommentService extends IService<Comment> {
    IPage<Comment> getCommentsByCapsuleId(Long capsuleId, int page, int size);
}
