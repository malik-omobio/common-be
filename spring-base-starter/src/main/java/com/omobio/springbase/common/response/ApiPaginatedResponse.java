package com.omobio.springbase.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiPaginatedResponse<T> {
    private List<T> data;
    private Pagination pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private long totalCount;
        private int currentPage;
        private int totalPages;
        private boolean hasNextPage;
    }
}
