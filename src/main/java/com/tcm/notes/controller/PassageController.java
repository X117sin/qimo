package com.tcm.notes.controller;

import com.tcm.notes.entity.Passage;
import com.tcm.notes.service.PassageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 条文控制器，处理条文的CRUD操作
 */
@RestController
@RequestMapping("/api/passages")
public class PassageController {

    @Autowired
    private PassageService passageService;

    /**
     * 创建条文
     * @param passage 条文对象
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Passage> createPassage(@RequestBody Passage passage) {
        Passage newPassage = passageService.savePassage(passage);
        return new ResponseEntity<>(newPassage, HttpStatus.CREATED);
    }

    /**
     * 获取所有条文（分页）
     * @param page 页码
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param direction 排序方向
     * @return 分页条文结果
     */
    @GetMapping
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
     * 根据ID获取条文
     * @param id 条文ID
     * @return 条文对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<Passage> getPassageById(@PathVariable Long id) {
        return passageService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 搜索条文
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/search")
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

    /**
     * 更新条文
     * @param id 条文ID
     * @param passageDetails 条文详情
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Passage> updatePassage(@PathVariable Long id, @RequestBody Passage passageDetails) {
        try {
            Passage updatedPassage = passageService.updatePassage(id, passageDetails);
            return ResponseEntity.ok(updatedPassage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除条文
     * @param id 条文ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<HttpStatus> deletePassage(@PathVariable Long id) {
        try {
            passageService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取条文总数量
     * @return 条文总数量
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getPassageCount() {
        long count = passageService.count();
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}