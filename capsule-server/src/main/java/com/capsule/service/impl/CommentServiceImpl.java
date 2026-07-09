package com.capsule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capsule.entity.Comment;
import com.capsule.entity.User;
import com.capsule.mapper.CommentMapper;
import com.capsule.service.CommentService;
import com.capsule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    @Override
    public IPage<Comment> getCommentsByCapsuleId(Long capsuleId, int page, int size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getCapsuleId, capsuleId)
                .orderByDesc(Comment::getCreateTime);
        
        IPage<Comment> commentPage = this.page(new Page<>(page, size), wrapper);
        List<Comment> records = commentPage.getRecords();


        for (Comment comment : records) {
            
            if (comment.getUserId() != null) {
                User user = userService.getById(comment.getUserId());
                if (user != null) {
                    comment.setNickname(user.getNickname()); 
                    comment.setAvatar(user.getAvatar());     
                }
            }


            if (comment.getParentId() != null) {
                Comment parentComment = this.getById(comment.getParentId());
                if (parentComment != null) {
                    User parentUser = userService.getById(parentComment.getUserId());
                    if (parentUser != null) {
                        comment.setParentNickname(parentUser.getNickname());
                    }
                }
            }
        }

        return commentPage;
    }
}