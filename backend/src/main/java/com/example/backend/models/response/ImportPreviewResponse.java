package com.example.backend.models.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImportPreviewResponse<T> {
    private int totalRows;
    private int validRows;
    private int invalidRows;
    private List<ImportRow<T>> data;
}
