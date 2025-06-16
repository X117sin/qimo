package com.tcm.notes.service;

import com.tcm.notes.entity.WrongAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 错题本服务接口
 */
public interface WrongAnswerService {

    /**
     * 保存错题记录
     * @param wrongAnswer 错题记录对象
     * @return 保存后的错题记录
     */
    WrongAnswer saveWrongAnswer(WrongAnswer wrongAnswer);

    /**
     * 根据ID查找错题记录
     * @param id 错题记录ID
     * @return 错题记录对象
     */
    Optional<WrongAnswer> findById(Long id);

    /**
     * 查找用户的所有错题记录
     * @param userId 用户ID
     * @return 错题记录列表
     */
    List<WrongAnswer> findByUserId(Long userId);

    /**
     * 分页查询用户的错题记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页错题记录结果
     */
    Page<WrongAnswer> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据条文ID和用户ID查找错题记录
     * @param passageId 条文ID
     * @param userId 用户ID
     * @return 错题记录对象
     */
    Optional<WrongAnswer> findByPassageIdAndUserId(Long passageId, Long userId);

    /**
     * 增加错题次数
     * @param passageId 条文ID
     * @param userId 用户ID
     * @return 更新后的错题记录
     */
    WrongAnswer incrementWrongCount(Long passageId, Long userId);

    /**
     * 删除错题记录
     * @param id 错题记录ID
     */
    void deleteById(Long id);

    /**
     * 删除用户的所有错题记录
     * @param userId 用户ID
     */
    void deleteAllByUserId(Long userId);
}