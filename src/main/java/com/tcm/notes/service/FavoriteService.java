package com.tcm.notes.service;

import com.tcm.notes.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * 收藏服务接口
 */
public interface FavoriteService {

    /**
     * 添加收藏
     * @param userId 用户ID
     * @param passageId 条文ID
     * @return 收藏对象
     */
    Favorite addFavorite(Long userId, Long passageId);

    /**
     * 取消收藏
     * @param userId 用户ID
     * @param passageId 条文ID
     */
    void removeFavorite(Long userId, Long passageId);

    /**
     * 检查用户是否收藏了指定条文
     * @param userId 用户ID
     * @param passageId 条文ID
     * @return 是否收藏
     */
    boolean isFavorited(Long userId, Long passageId);

    /**
     * 根据ID查找收藏
     * @param id 收藏ID
     * @return 收藏对象
     */
    Optional<Favorite> findById(Long id);

    /**
     * 分页查询用户的收藏
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页收藏结果
     */
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    /**
     * 获取用户收藏数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    long countByUserId(Long userId);
}