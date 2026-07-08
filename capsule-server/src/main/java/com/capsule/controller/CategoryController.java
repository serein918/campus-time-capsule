package com.capsule.controller;

import com.capsule.common.Result;
import com.capsule.entity.Category;
import com.capsule.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类列表（公开接口）
     */
    @GetMapping("/list")
    public Result<List<Category>> list() {
        List<Category> list = categoryService.list();
        return Result.success(list);
    }
}
