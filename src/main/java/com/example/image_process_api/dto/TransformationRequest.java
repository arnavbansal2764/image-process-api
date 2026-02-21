package com.example.image_process_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransformationRequest {
    @JsonProperty("resize")
    private ResizeRequest resize;
    
    @JsonProperty("crop")
    private CropRequest crop;
    
    @JsonProperty("rotate")
    private Integer rotate;
    
    @JsonProperty("format")
    private String format;
    
    @JsonProperty("filters")
    private FiltersRequest filters;
}
