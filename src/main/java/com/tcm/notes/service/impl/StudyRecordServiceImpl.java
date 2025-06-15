package com.tcm.notes.service.impl;

import com.tcm.notes.entity.Passage;
import com.tcm.notes.entity.StudyRecord;
import com.tcm.notes.entity.User;
import com.tcm.notes.repository.PassageRepository;
import com.tcm.notes.repository.StudyRecordRepository;
import com.tcm.notes.repository.UserRepository;
import com.tcm.notes.service.StudyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 学习记录服务实现类
 */
@Service
public class StudyRecordServiceImpl implements StudyRecordService {

    @Autowired
    private StudyRecordRepository studyRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassageRepository passageRepository;

    @Override
    public StudyRecord saveStudyRecord(StudyRecord studyRecord) {
        return studyRecordRepository.save(studyRecord);
    }

    @Override
    public Optional<StudyRecord> findById(Long id) {
        return studyRecordRepository.findById(id);
    }

    @Override
    public Optional<StudyRecord> findByUserIdAndPassageId(Long userId, Long passageId) {
        StudyRecord studyRecord = studyRecordRepository.findByUserIdAndPassageId(userId, passageId);
        return Optional.ofNullable(studyRecord);
    }

    @Override
    public Page<StudyRecord> findByUserId(Long userId, Pageable pageable) {
        return studyRecordRepository.findByUserId(userId, pageable);
    }

    @Override
    public StudyRecord updateStudyRecord(Long userId, Long passageId) {
        StudyRecord studyRecord = studyRecordRepository.findByUserIdAndPassageId(userId, passageId);
        
        if (studyRecord == null) {
            // 创建新的学习记录
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + userId));
            
            Passage passage = passageRepository.findById(passageId)
                    .orElseThrow(() -> new RuntimeException("条文不存在，ID: " + passageId));
            
            studyRecord = new StudyRecord();
            studyRecord.setUser(user);
            studyRecord.setPassage(passage);
            studyRecord.setStudyTime(1); // 初始学习时间为1（系统中以分钟为单位，但前端显示为秒钟）
            studyRecord.setLastStudyAt(LocalDateTime.now());
        } else {
            // 更新现有学习记录
            studyRecord.setLastStudyAt(LocalDateTime.now());
            studyRecord.setStudyTime(studyRecord.getStudyTime() + 1); // 每次更新增加1（系统中以分钟为单位，但前端显示为秒钟）
        }
        
        return studyRecordRepository.save(studyRecord);
    }

    @Override
    public Map<String, Object> getUserStudyStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取已学习条文数量（只计算学习时间超过5秒钟的条文）
        long studiedCount = studyRecordRepository.countByUserIdAndStudyTimeGreaterThanEqual(userId, 1);
        statistics.put("studiedCount", studiedCount);
        
        // 获取最近学习时间
        LocalDateTime lastStudyTime = getLastStudyTime(userId);
        statistics.put("lastStudyTime", lastStudyTime);
        
        // 获取总学习时间（分钟）
        int totalStudyTime = studyRecordRepository.findByUserId(userId, Pageable.unpaged())
                .getContent()
                .stream()
                .mapToInt(StudyRecord::getStudyTime)
                .sum();
        statistics.put("totalStudyTime", totalStudyTime);
        
        return statistics;
    }

    @Override
    public LocalDateTime getLastStudyTime(Long userId) {
        Page<StudyRecord> records = studyRecordRepository.findByUserId(userId, 
                org.springframework.data.domain.PageRequest.of(0, 1, org.springframework.data.domain.Sort.by("lastStudyAt").descending()));
        
        if (records.hasContent()) {
            return records.getContent().get(0).getLastStudyAt();
        }
        
        return null;
    }

    @Override
    public long getStudiedPassageCount(Long userId) {
        return studyRecordRepository.countByUserId(userId);
    }
    
    @Override
    public long getStudyDaysCount(Long userId) {
        // 获取用户所有学习记录
        java.util.List<StudyRecord> records = studyRecordRepository.findByUserId(userId, Pageable.unpaged()).getContent();
        
        // 统计不同的学习日期数量
        return records.stream()
                .map(record -> record.getLastStudyAt().toLocalDate())
                .distinct()
                .count();
    }
}