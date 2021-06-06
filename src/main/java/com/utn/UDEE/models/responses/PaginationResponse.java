package com.utn.UDEE.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

import java.io.Serializable;

@Data
@AllArgsConstructor

public class PaginationResponse<T> implements Serializable {

    private List<T> response;
    private Integer totalPages;
    private Long totalElements;

}
