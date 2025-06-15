package com.tcm.notes.service;

import com.tcm.notes.entity.Passage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 条文服务接口
 */
public interface PassageService {

    /**
     * 保存条文
     * @param passage 条文对象
     * @return 保存后的条文
     */
    Passage savePassage(Passage passage);

    /**
     * 根据ID查找条文
     * @param id 条文ID
     * @return 条文对象
     */
    Optional<Passage> findById(Long id);

    /**
     * 查找所有条文
     * @return 条文列表
     */
    List<Passage> findAll();

    /**
     * 分页查询条文
     * @param pageable 分页参数
     * @return 分页条文结果
     */
    Page<Passage> findAll(Pageable pageable);

    /**
     * 根据关键词搜索条文
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 分页条文结果
     */
    Page<Passage> searchByKeyword(String keyword, Pageable pageable);

    /**
     * 删除条文
     * @param id 条文ID
     */
    void deleteById(Long id);

    /**
     * 更新条文
     * @param id 条文ID
     * @param passageDetails 条文详情
     * @return 更新后的条文
     */
    Passage updatePassage(Long id, Passage passageDetails);

    /**
     * 获取条文总数量
     * @return 条文总数量
     */
    long count();
}