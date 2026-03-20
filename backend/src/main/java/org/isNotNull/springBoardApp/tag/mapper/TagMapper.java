package org.isNotNull.springBoardApp.tag.mapper;

import org.isNotNull.springBoardApp.tag.dto.TagDTO;
import org.isNotNull.springBoardApp.tables.pojos.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface TagMapper {

    TagDTO toDto(Tag tag);

    Tag toEntity(TagDTO tagDTO);
}
