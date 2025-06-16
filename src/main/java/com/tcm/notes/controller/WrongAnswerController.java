package com.tcm.notes.controller;
import com.tcm.notes.dto.WrongAnswerDTO;
import com.tcm.notes.entity.Passage;
import com.tcm.notes.entity.User;
import com.tcm.notes.entity.WrongAnswer;
import com.tcm.notes.repository.PassageRepository;
import com.tcm.notes.repository.UserRepository;
import com.tcm.notes.service.WrongAnswerService;
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
import java.util.Map;
import java.util.Optional;

/**
 * 错题本控制器，处理错题记录的管理
 */
@RestController
@RequestMapping("/api/wrong-answers")
public class WrongAnswerController {

    @Autowired
    private WrongAnswerService wrongAnswerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassageRepository passageRepository;

    /**
     * 添加错题记录
     * @param request 请求参数
     * @return 添加结果
     */
    @PostMapping
    public ResponseEntity<?> addWrongAnswer(@RequestBody Map<String, Object> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            Long passageId = Long.valueOf(request.get("passageId").toString());
            passageRepository.findById(passageId)
                    .orElseThrow(() -> new RuntimeException("条文不存在"));

            WrongAnswer wrongAnswer = wrongAnswerService.incrementWrongCount(passageId, user.getId());
            // 转换为DTO避免懒加载序列化问题
            WrongAnswerDTO wrongAnswerDTO = WrongAnswerDTO.fromEntity(wrongAnswer);
            return new ResponseEntity<>(wrongAnswerDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("添加错题记录失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 获取当前用户的所有错题记录（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 分页错题记录结果
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserWrongAnswers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastWrongAt"));
            Page<WrongAnswer> wrongAnswers = wrongAnswerService.findByUserId(user.getId(), pageable);

            // 转换为DTO避免懒加载序列化问题
            java.util.List<WrongAnswerDTO> wrongAnswerDTOs = wrongAnswers.getContent().stream()
                    .map(WrongAnswerDTO::fromEntity)
                    .collect(java.util.stream.Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("wrongAnswers", wrongAnswerDTOs);
            response.put("currentPage", wrongAnswers.getNumber());
            response.put("totalItems", wrongAnswers.getTotalElements());
            response.put("totalPages", wrongAnswers.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据ID获取错题记录
     * @param id 错题记录ID
     * @return 错题记录对象
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getWrongAnswerById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            WrongAnswer wrongAnswer = wrongAnswerService.findById(id)
                    .orElseThrow(() -> new RuntimeException("错题记录不存在"));

            // 验证错题记录所有者
            if (!wrongAnswer.getUser().getId().equals(user.getId())) {
                return new ResponseEntity<>("无权访问此错题记录", HttpStatus.FORBIDDEN);
            }

            // 转换为DTO避免懒加载序列化问题
            WrongAnswerDTO wrongAnswerDTO = WrongAnswerDTO.fromEntity(wrongAnswer);
            return ResponseEntity.ok(wrongAnswerDTO);
        } catch (Exception e) {
            return new ResponseEntity<>("获取错题记录失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 检查条文是否在错题本中
     * @param passageId 条文ID
     * @return 检查结果
     */
    @GetMapping("/check/{passageId}")
    public ResponseEntity<?> checkWrongAnswer(@PathVariable Long passageId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            Optional<WrongAnswer> wrongAnswer = wrongAnswerService.findByPassageIdAndUserId(passageId, user.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("exists", wrongAnswer.isPresent());
            if (wrongAnswer.isPresent()) {
                // 转换为DTO避免懒加载序列化问题
                WrongAnswerDTO wrongAnswerDTO = WrongAnswerDTO.fromEntity(wrongAnswer.get());
                response.put("wrongAnswer", wrongAnswerDTO);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>("检查错题记录失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 删除错题记录
     * @param id 错题记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWrongAnswer(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            WrongAnswer wrongAnswer = wrongAnswerService.findById(id)
                    .orElseThrow(() -> new RuntimeException("错题记录不存在"));

            // 验证错题记录所有者
            if (!wrongAnswer.getUser().getId().equals(user.getId())) {
                return new ResponseEntity<>("无权删除此错题记录", HttpStatus.FORBIDDEN);
            }

            wrongAnswerService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("删除错题记录失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 清空当前用户的所有错题记录
     * @return 清空结果
     */
    @DeleteMapping("/clear-all")
    public ResponseEntity<?> clearAllWrongAnswers() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            wrongAnswerService.deleteAllByUserId(user.getId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("清空错题记录失败: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}