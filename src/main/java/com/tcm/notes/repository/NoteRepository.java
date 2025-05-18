package com.tcm.notes.repository;

import com.tcm.notes.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 笔记数据访问接口
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    /**
     * 根据用户ID查询笔记
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 笔记分页结果
     */
    Page<Note> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据条文ID查询笔记
     * @param passageId 条文ID
     * @param pageable 分页参数
     * @return 笔记分页结果
     */
    Page<Note> findByPassageId(Long passageId, Pageable pageable);
    
    /**
     * 根据用户ID和条文ID查询笔记
     * @param userId 用户ID
     * @param passageId 条文ID
     * @return 笔记列表
     */
    Note findByUserIdAndPassageId(Long userId, Long passageId);
    
    /**
     * 删除指定用户和条文的笔记
     * @param userId 用户ID
     * @param passageId 条文ID
     */
    void deleteByUserIdAndPassageId(Long userId, Long passageId);
}