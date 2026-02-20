package com.example.image_process_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> data;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
