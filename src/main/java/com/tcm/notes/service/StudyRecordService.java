package com.tcm.notes.service;

import com.tcm.notes.entity.StudyRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * 学习记录服务接口
 */
public interface StudyRecordService {

    /**
     * 保存学习记录
     * @param studyRecord 学习记录对象
     * @return 保存后的学习记录
     */
    StudyRecord saveStudyRecord(StudyRecord studyRecord);

    /**
     * 根据ID查找学习记录
     * @param id 学习记录ID
     * @return 学习记录对象
     */
    Optional<StudyRecord> findById(Long id);

    /**
     * 根据用户ID和条文ID查找学习记录
     * @param userId 用户ID
     * @param passageId 条文ID
     * @return 学习记录对象
     */
    Optional<StudyRecord> findByUserIdAndPassageId(Long userId, Long passageId);

    /**
     * 分页查询用户的学习记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页学习记录结果
     */
    Page<StudyRecord> findByUserId(Long userId, Pageable pageable);

    /**
     * 更新学习记录
     * @param userId 用户ID
     * @param passageId 条文ID
     * @return 更新后的学习记录
     */
    StudyRecord updateStudyRecord(Long userId, Long passageId);

    /**
     * 获取用户学习统计信息
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserStudyStatistics(Long userId);

    /**
     * 获取用户最近学习时间
     * @param userId 用户ID
     * @return 最近学习时间
     */
    LocalDateTime getLastStudyTime(Long userId);

    /**
     * 获取用户已学习条文数量
     * @param userId 用户ID
     * @return 已学习条文数量
     */
    long getStudiedPassageCount(Long userId);
    
    /**
     * 获取用户学习天数
     * @param userId 用户ID
     * @return 学习天数
     */
    long getStudyDaysCount(Long userId);
}