package com.tcm.notes.repository;

import com.tcm.notes.entity.StudyRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习记录数据访问接口
 */
@Repository
public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {
    
    /**
     * 根据用户ID查询学习记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 学习记录分页结果
     */
    Page<StudyRecord> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和条文ID查询学习记录
     * @param userId 用户ID
     * @param passageId 条文ID
     * @return 学习记录
     */
    StudyRecord findByUserIdAndPassageId(Long userId, Long passageId);
    
    /**
     * 检查用户是否有指定条文的学习记录
     * @param userId 用户ID
     * @param passageId 条文ID
     * @return 是否存在学习记录
     */
    boolean existsByUserIdAndPassageId(Long userId, Long passageId);
    
    /**
     * 获取用户学习的条文数量
     * @param userId 用户ID
     * @return 学习条文数量
     */
    long countByUserId(Long userId);
    
    /**
     * 获取用户总学习时间
     * @param userId 用户ID
     * @return 总学习时间（秒）
     */
    @Query("SELECT SUM(s.studyTime) FROM StudyRecord s WHERE s.user.id = :userId")
    Integer getTotalStudyTimeByUserId(@Param("userId") Long userId);
    
    /**
     * 获取用户最近学习的条文记录
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 学习记录列表
     */
    @Query("SELECT s FROM StudyRecord s WHERE s.user.id = :userId ORDER BY s.lastStudyAt DESC")
    List<StudyRecord> findRecentStudyRecordsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 获取用户在指定时间段内的学习记录
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 学习记录列表
     */
    List<StudyRecord> findByUserIdAndLastStudyAtBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取用户学习时间最长的条文
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 学习记录列表
     */
    @Query("SELECT s FROM StudyRecord s WHERE s.user.id = :userId ORDER BY s.studyTime DESC")
    List<StudyRecord> findMostStudiedPassagesByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 获取用户学习时间超过指定阈值的条文数量
     * @param userId 用户ID
     * @param minStudyTime 最小学习时间（分钟）
     * @return 学习条文数量
     */
    @Query("SELECT COUNT(s) FROM StudyRecord s WHERE s.user.id = :userId AND s.studyTime >= :minStudyTime")
    long countByUserIdAndStudyTimeGreaterThanEqual(@Param("userId") Long userId, @Param("minStudyTime") Integer minStudyTime);
}