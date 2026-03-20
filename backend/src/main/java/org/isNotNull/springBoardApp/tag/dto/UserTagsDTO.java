package org.isNotNull.springBoardApp.tag.dto;
import java.util.List;
import lombok.Data;

@Data
public class UserTagsDTO {
    private Long userId;
    private List<TagDTO> tags;
}
