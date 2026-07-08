package com.capsule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.capsule.entity.Category;
import com.capsule.mapper.CategoryMapper;
import com.capsule.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
