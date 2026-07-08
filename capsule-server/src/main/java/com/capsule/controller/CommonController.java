package com.capsule.controller;

import com.capsule.common.Result;
import com.capsule.exception.BusinessException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    private static final String UPLOAD_DIR = "uploads/";

    /**
     * 文件上传接口
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 确保上传目录存在
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            file.transferTo(new File(dir.getAbsolutePath() + File.separator + fileName));
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }

        // 返回可访问的URL路径
        String url = "/uploads/" + fileName;
        return Result.success("上传成功", url);
    }
}
