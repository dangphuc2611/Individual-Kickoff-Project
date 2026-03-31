package com.example.backend.models.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImportRow<T> {
    private int rowNum;
    private boolean valid;
    private List<String> errors;
    private T requestData;
}
