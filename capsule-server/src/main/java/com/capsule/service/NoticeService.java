package com.capsule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.capsule.entity.Notice;

public interface NoticeService extends IService<Notice> {
    IPage<Notice> getNoticePage(int page, int size);
}
