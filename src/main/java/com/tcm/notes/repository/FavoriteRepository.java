package com.tcm.notes.repository;

import com.tcm.notes.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 收藏数据访问接口
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * 根据用户ID查询收藏
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 收藏分页结果
     */
    Page<Favorite> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 检查用户是否收藏了指定条文
     * @param userId 用户ID
     * @param passageId 条文ID
     * @return 是否收藏
     */
    boolean existsByUserIdAndPassageId(Long userId, Long passageId);
    
    /**
     * 根据用户ID和条文ID查询收藏
     * @param userId 用户ID
     * @param passageId 条文ID
     * @return 收藏对象
     */
    Favorite findByUserIdAndPassageId(Long userId, Long passageId);
    
    /**
     * 删除指定用户和条文的收藏
     * @param userId 用户ID
     * @param passageId 条文ID
     */
    void deleteByUserIdAndPassageId(Long userId, Long passageId);
}