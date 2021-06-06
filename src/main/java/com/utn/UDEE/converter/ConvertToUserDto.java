package com.utn.UDEE.converter;

import com.utn.UDEE.models.User;
import com.utn.UDEE.models.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConvertToUserDto implements Converter<User, UserDto> {

    private final ModelMapper modelMapper;

    public ConvertToUserDto(final ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto convert(User user){
        return modelMapper.map(user, UserDto.class);
    }

}
