package com.tcm.notes.repository;

import com.tcm.notes.entity.Passage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 条文数据访问接口
 */
@Repository
public interface PassageRepository extends JpaRepository<Passage, Long> {
    
    /**
     * 根据标题模糊查询条文
     * @param title 标题关键词
     * @param pageable 分页参数
     * @return 条文分页结果
     */
    Page<Passage> findByTitleContaining(String title, Pageable pageable);
    
    /**
     * 根据内容模糊查询条文
     * @param content 内容关键词
     * @param pageable 分页参数
     * @return 条文分页结果
     */
    Page<Passage> findByContentContaining(String content, Pageable pageable);
    
    /**
     * 根据来源查询条文
     * @param source 来源
     * @param pageable 分页参数
     * @return 条文分页结果
     */
    Page<Passage> findBySource(String source, Pageable pageable);
    
    /**
     * 根据分类查询条文
     * @param category 分类
     * @param pageable 分页参数
     * @return 条文分页结果
     */
    Page<Passage> findByCategory(String category, Pageable pageable);
    
    /**
     * 根据难度等级查询条文
     * @param difficulty 难度等级
     * @param pageable 分页参数
     * @return 条文分页结果
     */
    Page<Passage> findByDifficulty(Integer difficulty, Pageable pageable);
    
    /**
     * 复合条件查询条文
     * @param title 标题关键词
     * @param content 内容关键词
     * @param source 来源
     * @param category 分类
     * @param pageable 分页参数
     * @return 条文分页结果
     */
    @Query("SELECT p FROM Passage p WHERE " +
           "(:title IS NULL OR p.title LIKE %:title%) AND " +
           "(:content IS NULL OR p.content LIKE %:content%) AND " +
           "(:source IS NULL OR p.source = :source) AND " +
           "(:category IS NULL OR p.category = :category)")
    Page<Passage> findByConditions(
            @Param("title") String title,
            @Param("content") String content,
            @Param("source") String source,
            @Param("category") String category,
            Pageable pageable);
    
    /**
     * 获取所有可用的条文来源
     * @return 来源列表
     */
    @Query("SELECT DISTINCT p.source FROM Passage p WHERE p.source IS NOT NULL")
    List<String> findAllSources();
    
    /**
     * 获取所有可用的条文分类
     * @return 分类列表
     */
    @Query("SELECT DISTINCT p.category FROM Passage p WHERE p.category IS NOT NULL")
    List<String> findAllCategories();
}