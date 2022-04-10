package com.sykora.socialnetworkapp.dto.mapper;

import com.sykora.socialnetworkapp.dto.PostDto;
import com.sykora.socialnetworkapp.model.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PostMapper {
    PostDto postToPostDto(Post post);
    Post postDtoToPost(PostDto postDto);
}
