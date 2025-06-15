package com.tcm.notes.controller;

import com.tcm.notes.entity.Favorite;
import com.tcm.notes.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 获取当前用户的收藏列表
     * @param pageable 分页参数
     * @return 收藏分页结果
     */
    @GetMapping
    public ResponseEntity<Page<Favorite>> getCurrentUserFavorites(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(favoriteService.findByUserId(userId, pageable));
    }

    /**
     * 获取指定用户的收藏列表（管理员权限）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 收藏分页结果
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<Favorite>> getUserFavorites(
            @PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(favoriteService.findByUserId(userId, pageable));
    }

    /**
     * 添加收藏
     * @param passageId 条文ID
     * @return 添加结果
     */
    @PostMapping("/{passageId}")
    public ResponseEntity<Favorite> addFavorite(@PathVariable Long passageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        Favorite favorite = favoriteService.addFavorite(userId, passageId);
        return ResponseEntity.ok(favorite);
    }

    /**
     * 取消收藏
     * @param passageId 条文ID
     * @return 取消结果
     */
    @DeleteMapping("/{passageId}")
    public ResponseEntity<Map<String, Boolean>> removeFavorite(@PathVariable Long passageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        favoriteService.removeFavorite(userId, passageId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    /**
     * 检查条文是否已收藏
     * @param passageId 条文ID
     * @return 是否已收藏
     */
    @GetMapping("/check/{passageId}")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(@PathVariable Long passageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        boolean isFavorited = favoriteService.isFavorited(userId, passageId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("favorited", isFavorited);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前用户的收藏数量
     * @return 收藏数量
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getFavoriteCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());
        long count = favoriteService.countByUserId(userId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}