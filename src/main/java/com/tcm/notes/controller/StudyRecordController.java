package com.tcm.notes.controller;

import com.tcm.notes.entity.StudyRecord;
import com.tcm.notes.service.StudyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学习记录控制器
 */
@RestController
@RequestMapping("/api/study-records")
public class StudyRecordController {

    @Autowired
    private StudyRecordService studyRecordService;

    /**
     * 获取当前用户的学习记录
     * @param pageable 分页参数
     * @return 学习记录分页结果
     */
    @GetMapping
    public ResponseEntity<Page<StudyRecord>> getCurrentUserStudyRecords(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(studyRecordService.findByUserId(userId, pageable));
    }

    /**
     * 获取指定用户的学习记录（管理员权限）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 学习记录分页结果
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<StudyRecord>> getUserStudyRecords(
            @PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(studyRecordService.findByUserId(userId, pageable));
    }

    /**
     * 更新学习记录
     * @param passageId 条文ID
     * @return 更新后的学习记录
     */
    @PostMapping("/update/{passageId}")
    public ResponseEntity<StudyRecord> updateStudyRecord(@PathVariable Long passageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        StudyRecord updatedRecord = studyRecordService.updateStudyRecord(userId, passageId);
        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * 获取当前用户的学习统计信息
     * @return 学习统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCurrentUserStudyStatistics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> statistics = studyRecordService.getUserStudyStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取指定用户的学习统计信息（管理员权限）
     * @param userId 用户ID
     * @return 学习统计信息
     */
    @GetMapping("/statistics/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserStudyStatistics(@PathVariable Long userId) {
        Map<String, Object> statistics = studyRecordService.getUserStudyStatistics(userId);
        return ResponseEntity.ok(statistics);
    }
}