package com.tcm.notes.service.impl;

import com.tcm.notes.entity.Note;
import com.tcm.notes.repository.NoteRepository;
import com.tcm.notes.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 笔记服务实现类
 */
@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public Note saveNote(Note note) {
        return noteRepository.save(note);
    }

    @Override
    public Optional<Note> findById(Long id) {
        return noteRepository.findById(id);
    }

    @Override
    public List<Note> findByUserId(Long userId) {
        return noteRepository.findByUserId(userId, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Note> findByUserId(Long userId, Pageable pageable) {
        return noteRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<Note> findByPassageIdAndUserId(Long passageId, Long userId) {
        Note note = noteRepository.findByUserIdAndPassageId(userId, passageId);
        return note != null ? List.of(note) : List.of();
    }

    @Override
    public void deleteById(Long id) {
        noteRepository.deleteById(id);
    }

    @Override
    public Note updateNote(Long id, Note noteDetails) {
        return noteRepository.findById(id)
                .map(note -> {
                    note.setContent(noteDetails.getContent());
                    return noteRepository.save(note);
                })
                .orElseThrow(() -> new RuntimeException("笔记不存在，ID: " + id));
    }
    
    @Override
    public long countByUserId(Long userId) {
        return noteRepository.findByUserId(userId, Pageable.unpaged()).getTotalElements();
    }
    
    @Override
    public long countThisMonthNotes(Long userId) {
        LocalDateTime startOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        return noteRepository.findByUserId(userId, Pageable.unpaged())
                .getContent()
                .stream()
                .filter(note -> {
                    LocalDateTime createdAt = note.getCreatedAt();
                    return createdAt != null && createdAt.isAfter(startOfMonth) && createdAt.isBefore(endOfMonth);
                })
                .count();
    }
    
    @Override
    public long countRecentNotes(Long userId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        
        return noteRepository.findByUserId(userId, Pageable.unpaged())
                .getContent()
                .stream()
                .filter(note -> {
                    LocalDateTime createdAt = note.getCreatedAt();
                    return createdAt != null && createdAt.isAfter(sevenDaysAgo);
                })
                .count();
    }
}