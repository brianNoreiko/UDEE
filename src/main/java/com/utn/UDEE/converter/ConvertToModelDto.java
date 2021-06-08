package com.utn.UDEE.converter;

import com.utn.UDEE.models.Model;
import com.utn.UDEE.models.dto.ModelDto;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ConvertToModelDto implements Converter<Model, ModelDto> {

    private final ModelMapper modelMapper;

    public ConvertToModelDto(final ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public ModelDto convert(Model model){
        return modelMapper.map(model, ModelDto.class);
    }

}

