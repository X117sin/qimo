package com.tcm.notes.controller;

import com.tcm.notes.entity.Note;
import com.tcm.notes.entity.Passage;
import com.tcm.notes.entity.User;
import com.tcm.notes.repository.PassageRepository;
import com.tcm.notes.repository.UserRepository;
import com.tcm.notes.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 笔记控制器，处理笔记的CRUD操作
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassageRepository passageRepository;

    /**
     * 创建笔记
     * @param noteRequest 笔记请求
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody Map<String, Object> noteRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authName = authentication.getName();
            
            // 由于JWT中存储的是用户ID，authentication.getName()可能返回用户ID或用户名
            // 我们需要同时尝试两种方式来查找用户
            User user = null;
            try {
                // 首先尝试按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
                if (user == null) {
                    // 如果按用户名找不到，尝试按ID查找
                    Long userId = Long.parseLong(authName);
                    user = userRepository.findById(userId).orElse(null);
                }
            } catch (NumberFormatException e) {
                // 如果不是数字，只按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
            }
            
            if (user == null) {
                throw new RuntimeException("用户不存在: " + authName);
            }

            Long passageId = Long.valueOf(noteRequest.get("passageId").toString());
            Passage passage = passageRepository.findById(passageId)
                    .orElseThrow(() -> new RuntimeException("条文不存在"));

            String content = (String) noteRequest.get("content");

            Note note = new Note();
            note.setUser(user);
            note.setPassage(passage);
            note.setContent(content);

            Note savedNote = noteService.saveNote(note);
            return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("创建笔记失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 获取当前用户的所有笔记（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 分页笔记结果
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authName = authentication.getName();
            
            // 由于JWT中存储的是用户ID，authentication.getName()可能返回用户ID或用户名
            // 我们需要同时尝试两种方式来查找用户
            User user = null;
            try {
                // 首先尝试按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
                if (user == null) {
                    // 如果按用户名找不到，尝试按ID查找
                    Long userId = Long.parseLong(authName);
                    user = userRepository.findById(userId).orElse(null);
                }
            } catch (NumberFormatException e) {
                // 如果不是数字，只按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
            }
            
            if (user == null) {
                throw new RuntimeException("用户不存在: " + authName);
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
            Page<Note> notes = noteService.findByUserId(user.getId(), pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("notes", notes.getContent());
            response.put("currentPage", notes.getNumber());
            response.put("totalItems", notes.getTotalElements());
            response.put("totalElements", notes.getTotalElements()); // 前端期望的字段
            response.put("totalPages", notes.getTotalPages());
            
            // 计算本月笔记数量
            long thisMonthCount = noteService.countThisMonthNotes(user.getId());
            response.put("thisMonthCount", thisMonthCount);
            
            // 计算最近笔记数量（最近7天）
            long recentCount = noteService.countRecentNotes(user.getId());
            response.put("recentCount", recentCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据ID获取笔记
     * @param id 笔记ID
     * @return 笔记对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authName = authentication.getName();
            
            // 由于JWT中存储的是用户ID，authentication.getName()可能返回用户ID或用户名
            // 我们需要同时尝试两种方式来查找用户
            User user = null;
            try {
                // 首先尝试按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
                if (user == null) {
                    // 如果按用户名找不到，尝试按ID查找
                    Long userId = Long.parseLong(authName);
                    user = userRepository.findById(userId).orElse(null);
                }
            } catch (NumberFormatException e) {
                // 如果不是数字，只按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
            }
            
            if (user == null) {
                throw new RuntimeException("用户不存在: " + authName);
            }

            Note note = noteService.findById(id)
                    .orElseThrow(() -> new RuntimeException("笔记不存在"));

            // 验证笔记所有者
            if (!note.getUser().getId().equals(user.getId())) {
                return new ResponseEntity<>("无权访问此笔记", HttpStatus.FORBIDDEN);
            }

            return ResponseEntity.ok(note);
        } catch (Exception e) {
            return new ResponseEntity<>("获取笔记失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 根据条文ID获取笔记
     * @param passageId 条文ID
     * @return 笔记列表
     */
    @GetMapping("/passage/{passageId}")
    public ResponseEntity<?> getNotesByPassageId(@PathVariable Long passageId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authName = authentication.getName();
            
            // 由于JWT中存储的是用户ID，authentication.getName()可能返回用户ID或用户名
            // 我们需要同时尝试两种方式来查找用户
            User user = null;
            try {
                // 首先尝试按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
                if (user == null) {
                    // 如果按用户名找不到，尝试按ID查找
                    Long userId = Long.parseLong(authName);
                    user = userRepository.findById(userId).orElse(null);
                }
            } catch (NumberFormatException e) {
                // 如果不是数字，只按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
            }
            
            if (user == null) {
                throw new RuntimeException("用户不存在: " + authName);
            }

            List<Note> notes = noteService.findByPassageIdAndUserId(passageId, user.getId());
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return new ResponseEntity<>("获取笔记失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 更新笔记
     * @param id 笔记ID
     * @param noteRequest 笔记请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody Map<String, Object> noteRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authName = authentication.getName();
            
            // 由于JWT中存储的是用户ID，authentication.getName()可能返回用户ID或用户名
            // 我们需要同时尝试两种方式来查找用户
            User user = null;
            try {
                // 首先尝试按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
                if (user == null) {
                    // 如果按用户名找不到，尝试按ID查找
                    Long userId = Long.parseLong(authName);
                    user = userRepository.findById(userId).orElse(null);
                }
            } catch (NumberFormatException e) {
                // 如果不是数字，只按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
            }
            
            if (user == null) {
                throw new RuntimeException("用户不存在: " + authName);
            }

            Note existingNote = noteService.findById(id)
                    .orElseThrow(() -> new RuntimeException("笔记不存在"));

            // 验证笔记所有者
            if (!existingNote.getUser().getId().equals(user.getId())) {
                return new ResponseEntity<>("无权修改此笔记", HttpStatus.FORBIDDEN);
            }

            String content = (String) noteRequest.get("content");
            existingNote.setContent(content);

            Note updatedNote = noteService.updateNote(id, existingNote);
            return ResponseEntity.ok(updatedNote);
        } catch (Exception e) {
            return new ResponseEntity<>("更新笔记失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 删除笔记
     * @param id 笔记ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authName = authentication.getName();
            
            // 由于JWT中存储的是用户ID，authentication.getName()可能返回用户ID或用户名
            // 我们需要同时尝试两种方式来查找用户
            User user = null;
            try {
                // 首先尝试按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
                if (user == null) {
                    // 如果按用户名找不到，尝试按ID查找
                    Long userId = Long.parseLong(authName);
                    user = userRepository.findById(userId).orElse(null);
                }
            } catch (NumberFormatException e) {
                // 如果不是数字，只按用户名查找
                user = userRepository.findByUsername(authName).orElse(null);
            }
            
            if (user == null) {
                throw new RuntimeException("用户不存在: " + authName);
            }

            Note note = noteService.findById(id)
                    .orElseThrow(() -> new RuntimeException("笔记不存在"));

            // 验证笔记所有者
            if (!note.getUser().getId().equals(user.getId())) {
                return new ResponseEntity<>("无权删除此笔记", HttpStatus.FORBIDDEN);
            }

            noteService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("删除笔记失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}