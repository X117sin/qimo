package com.tcm.notes.service.impl;

import com.tcm.notes.entity.Passage;
import com.tcm.notes.repository.PassageRepository;
import com.tcm.notes.service.PassageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 条文服务实现类
 */
@Service
public class PassageServiceImpl implements PassageService {

    @Autowired
    private PassageRepository passageRepository;

    @Override
    public Passage savePassage(Passage passage) {
        return passageRepository.save(passage);
    }

    @Override
    public Optional<Passage> findById(Long id) {
        return passageRepository.findById(id);
    }

    @Override
    public List<Passage> findAll() {
        return passageRepository.findAll();
    }

    @Override
    public Page<Passage> findAll(Pageable pageable) {
        return passageRepository.findAll(pageable);
    }

    @Override
    public Page<Passage> searchByKeyword(String keyword, Pageable pageable) {
        // 先在标题中搜索，如果没有结果再在内容中搜索
        Page<Passage> result = passageRepository.findByTitleContaining(keyword, pageable);
        if (result.isEmpty()) {
            result = passageRepository.findByContentContaining(keyword, pageable);
        }
        return result;
    }

    @Override
    public void deleteById(Long id) {
        passageRepository.deleteById(id);
    }

    @Override
    public Passage updatePassage(Long id, Passage passageDetails) {
        return passageRepository.findById(id)
                .map(passage -> {
                    passage.setTitle(passageDetails.getTitle());
                    passage.setContent(passageDetails.getContent());
                    passage.setSource(passageDetails.getSource());
                    passage.setCategory(passageDetails.getCategory());
                    passage.setDifficulty(passageDetails.getDifficulty());
                    return passageRepository.save(passage);
                })
                .orElseThrow(() -> new RuntimeException("条文不存在，ID: " + id));
    }

    @Override
    public long count() {
        return passageRepository.count();
    }
}