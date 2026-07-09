package com.capsule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long capsuleId;
    private Long userId;
    private String content;
    private Long parentId;      // 回复的评论ID，顶级评论为null
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String nickname; // 评论人的昵称

    @TableField(exist = false)
    private String avatar;   // 评论人的头像URL

    @TableField(exist = false)
    private String parentNickname; // 被回复人的昵称
}
