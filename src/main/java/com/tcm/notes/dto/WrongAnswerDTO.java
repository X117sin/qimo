package com.tcm.notes.dto;

import com.tcm.notes.entity.WrongAnswer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 错题记录DTO，用于API响应，避免懒加载序列化问题
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrongAnswerDTO {
    
    private Long id;
    private Long userId;
    private String username;
    private Long passageId;
    private String passageTitle;
    private String passageContent;
    private String passageCategory;
    private String passageDifficulty;
    private String passageSource;
    private Integer timesWrong;
    private LocalDateTime lastWrongAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 从WrongAnswer实体转换为DTO
     */
    public static WrongAnswerDTO fromEntity(WrongAnswer wrongAnswer) {
        WrongAnswerDTO dto = new WrongAnswerDTO();
        dto.setId(wrongAnswer.getId());
        dto.setUserId(wrongAnswer.getUser().getId());
        dto.setUsername(wrongAnswer.getUser().getUsername());
        dto.setPassageId(wrongAnswer.getPassage().getId());
        dto.setPassageTitle(wrongAnswer.getPassage().getTitle());
        dto.setPassageContent(wrongAnswer.getPassage().getContent());
        dto.setPassageCategory(wrongAnswer.getPassage().getCategory());
        dto.setPassageDifficulty(String.valueOf(wrongAnswer.getPassage().getDifficulty()));
        dto.setPassageSource(wrongAnswer.getPassage().getSource());
        dto.setTimesWrong(wrongAnswer.getTimesWrong());
        dto.setLastWrongAt(wrongAnswer.getLastWrongAt());
        dto.setCreatedAt(wrongAnswer.getCreatedAt());
        dto.setUpdatedAt(wrongAnswer.getUpdatedAt());
        return dto;
    }
}