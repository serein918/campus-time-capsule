package com.capsule.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.capsule.common.Result;
import com.capsule.entity.Notice;
import com.capsule.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 获取公告列表（公开接口，分页）
     */
    @GetMapping("/list")
    public Result<IPage<Notice>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<Notice> result = noticeService.getNoticePage(page, size);
        return Result.success(result);
    }
}
