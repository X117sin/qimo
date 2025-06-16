package com.tcm.notes.repository;

import com.tcm.notes.entity.WrongAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 错题记录数据访问接口
 */
@Repository
public interface WrongAnswerRepository extends JpaRepository<WrongAnswer, Long> {
    
    /**
     * 根据用户ID查询错题记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 错题记录分页结果
     */
    Page<WrongAnswer> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和条文ID查询错题记录
     * @param userId 用户ID
     * @param passageId 条文ID
     * @return 错题记录
     */
    WrongAnswer findByUserIdAndPassageId(Long userId, Long passageId);
    
    /**
     * 检查用户是否有指定条文的错题记录
     * @param userId 用户ID
     * @param passageId 条文ID
     * @return 是否存在错题记录
     */
    boolean existsByUserIdAndPassageId(Long userId, Long passageId);
    
    /**
     * 获取用户错题数量
     * @param userId 用户ID
     * @return 错题数量
     */
    long countByUserId(Long userId);
    
    /**
     * 获取用户错误次数最多的条文
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 错题记录列表
     */
    @Query("SELECT w FROM WrongAnswer w WHERE w.user.id = :userId ORDER BY w.timesWrong DESC")
    List<WrongAnswer> findTopWrongAnswersByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 删除指定用户和条文的错题记录
     * @param userId 用户ID
     * @param passageId 条文ID
     */
    void deleteByUserIdAndPassageId(Long userId, Long passageId);
}