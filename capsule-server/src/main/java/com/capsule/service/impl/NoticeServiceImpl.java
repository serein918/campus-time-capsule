package com.capsule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capsule.entity.Notice;
import com.capsule.mapper.NoticeMapper;
import com.capsule.service.NoticeService;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public IPage<Notice> getNoticePage(int page, int size) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, 1)
                .orderByDesc(Notice::getCreateTime);
        return this.page(new Page<>(page, size), wrapper);
    }
}
