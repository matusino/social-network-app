package com.sykora.socialnetworkapp.dto.mapper;

import com.sykora.socialnetworkapp.dto.UserDto;
import com.sykora.socialnetworkapp.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PostMapper.class})
public interface UserMapper {
    UserDto userToUserDto(User user);
    User userDtoToUser(UserDto userDto);
}
