package com.tcm.notes.service.impl;

import com.tcm.notes.entity.Favorite;
import com.tcm.notes.entity.Passage;
import com.tcm.notes.entity.User;
import com.tcm.notes.repository.FavoriteRepository;
import com.tcm.notes.repository.PassageRepository;
import com.tcm.notes.repository.UserRepository;
import com.tcm.notes.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 收藏服务实现类
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassageRepository passageRepository;

    @Override
    @Transactional
    public Favorite addFavorite(Long userId, Long passageId) {
        // 检查是否已经收藏
        if (favoriteRepository.existsByUserIdAndPassageId(userId, passageId)) {
            return favoriteRepository.findByUserIdAndPassageId(userId, passageId);
        }

        // 获取用户和条文
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + userId));
        
        Passage passage = passageRepository.findById(passageId)
                .orElseThrow(() -> new RuntimeException("条文不存在，ID: " + passageId));

        // 创建新收藏
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setPassage(passage);

        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long passageId) {
        favoriteRepository.deleteByUserIdAndPassageId(userId, passageId);
    }

    @Override
    public boolean isFavorited(Long userId, Long passageId) {
        return favoriteRepository.existsByUserIdAndPassageId(userId, passageId);
    }

    @Override
    public Optional<Favorite> findById(Long id) {
        return favoriteRepository.findById(id);
    }

    @Override
    public Page<Favorite> findByUserId(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserId(userId, pageable);
    }

    @Override
    public long countByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId, Pageable.unpaged()).getTotalElements();
    }
}