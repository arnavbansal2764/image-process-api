package com.example.image_process_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FiltersRequest {
    private Boolean grayscale;
    private Boolean sepia;
}
