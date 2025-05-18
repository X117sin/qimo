package com.tcm.notes.controller;

import com.tcm.notes.entity.Passage;
import com.tcm.notes.service.PassageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共访问控制器，提供无需认证即可访问的端点
 */
@SuppressWarnings("unused")
@Controller
@RequestMapping("/api/public")
public class PublicController {

    /**
     * 首页信息
     * @return 系统基本信息
     */
    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "中医方证速查与笔记系统");
        response.put("version", "1.0.0");
        response.put("status", "运行中");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 重定向到首页
     * @return 重定向视图名
     */
    @GetMapping("/home")
    public String home() {
        return "redirect:/index.html";
    }
    
    /**
     * 健康检查
     * @return 系统状态
     */
    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("database", "Connected");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @Autowired
    private PassageService passageService;
    
    /**
     * 获取所有条文（分页）- 公开访问
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param direction 排序方向
     * @return 分页条文结果
     */
    @GetMapping("/passages")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllPassages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Passage> passages = passageService.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("passages", passages.getContent());
        response.put("currentPage", passages.getNumber());
        response.put("totalItems", passages.getTotalElements());
        response.put("totalPages", passages.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取条文 - 公开访问
     * @param id 条文ID
     * @return 条文对象
     */
    @GetMapping("/passages/{id}")
    @ResponseBody
    public ResponseEntity<Passage> getPassageById(@PathVariable Long id) {
        return passageService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 搜索条文 - 公开访问
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/passages/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchPassages(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Passage> passages = passageService.searchByKeyword(keyword, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("passages", passages.getContent());
        response.put("currentPage", passages.getNumber());
        response.put("totalItems", passages.getTotalElements());
        response.put("totalPages", passages.getTotalPages());

        return ResponseEntity.ok(response);
    }
}