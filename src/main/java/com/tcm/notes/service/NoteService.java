package com.tcm.notes.service;

import com.tcm.notes.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 笔记服务接口
 */
public interface NoteService {

    /**
     * 保存笔记
     * @param note 笔记对象
     * @return 保存后的笔记
     */
    Note saveNote(Note note);

    /**
     * 根据ID查找笔记
     * @param id 笔记ID
     * @return 笔记对象
     */
    Optional<Note> findById(Long id);

    /**
     * 查找用户的所有笔记
     * @param userId 用户ID
     * @return 笔记列表
     */
    List<Note> findByUserId(Long userId);

    /**
     * 分页查询用户的笔记
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页笔记结果
     */
    Page<Note> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据条文ID查找笔记
     * @param passageId 条文ID
     * @param userId 用户ID
     * @return 笔记列表
     */
    List<Note> findByPassageIdAndUserId(Long passageId, Long userId);

    /**
     * 删除笔记
     * @param id 笔记ID
     */
    void deleteById(Long id);

    /**
     * 更新笔记
     * @param id 笔记ID
     * @param noteDetails 笔记详情
     * @return 更新后的笔记
     */
    Note updateNote(Long id, Note noteDetails);
    
    /**
     * 统计用户笔记数量
     * @param userId 用户ID
     * @return 笔记数量
     */
    long countByUserId(Long userId);
}