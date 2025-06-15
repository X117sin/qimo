package com.tcm.notes.service.impl;

import com.tcm.notes.entity.WrongAnswer;
import com.tcm.notes.entity.User;
import com.tcm.notes.entity.Passage;
import com.tcm.notes.repository.WrongAnswerRepository;
import com.tcm.notes.repository.UserRepository;
import com.tcm.notes.repository.PassageRepository;
import com.tcm.notes.service.WrongAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 错题本服务实现类
 */
@Service
public class WrongAnswerServiceImpl implements WrongAnswerService {

    @Autowired
    private WrongAnswerRepository wrongAnswerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassageRepository passageRepository;

    @Override
    public WrongAnswer saveWrongAnswer(WrongAnswer wrongAnswer) {
        return wrongAnswerRepository.save(wrongAnswer);
    }

    @Override
    public Optional<WrongAnswer> findById(Long id) {
        return wrongAnswerRepository.findById(id);
    }

    @Override
    public List<WrongAnswer> findByUserId(Long userId) {
        return wrongAnswerRepository.findByUserId(userId, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<WrongAnswer> findByUserId(Long userId, Pageable pageable) {
        return wrongAnswerRepository.findByUserId(userId, pageable);
    }

    @Override
    public Optional<WrongAnswer> findByPassageIdAndUserId(Long passageId, Long userId) {
        WrongAnswer wrongAnswer = wrongAnswerRepository.findByUserIdAndPassageId(userId, passageId);
        return Optional.ofNullable(wrongAnswer);
    }

    @Override
    @Transactional
    public WrongAnswer incrementWrongCount(Long passageId, Long userId) {
        WrongAnswer wrongAnswer = wrongAnswerRepository.findByUserIdAndPassageId(userId, passageId);
        
        if (wrongAnswer == null) {
            // 创建新的错题记录
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + userId));
            
            Passage passage = passageRepository.findById(passageId)
                    .orElseThrow(() -> new RuntimeException("条文不存在，ID: " + passageId));
            
            wrongAnswer = new WrongAnswer();
            wrongAnswer.setUser(user);
            wrongAnswer.setPassage(passage);
            wrongAnswer.setTimesWrong(1);
            wrongAnswer.setLastWrongAt(LocalDateTime.now());
        } else {
            // 更新现有错题记录
            wrongAnswer.setTimesWrong(wrongAnswer.getTimesWrong() + 1);
            wrongAnswer.setLastWrongAt(LocalDateTime.now());
        }
        
        return wrongAnswerRepository.save(wrongAnswer);
    }

    @Override
    public void deleteById(Long id) {
        wrongAnswerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllByUserId(Long userId) {
        Page<WrongAnswer> wrongAnswers = wrongAnswerRepository.findByUserId(userId, Pageable.unpaged());
        wrongAnswerRepository.deleteAll(wrongAnswers.getContent());
    }
}