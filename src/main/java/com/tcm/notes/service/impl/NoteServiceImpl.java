package com.tcm.notes.service.impl;

import com.tcm.notes.entity.Note;
import com.tcm.notes.repository.NoteRepository;
import com.tcm.notes.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}