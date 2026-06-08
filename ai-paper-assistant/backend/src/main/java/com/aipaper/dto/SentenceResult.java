package com.aipaper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SentenceResult {

    private String text;

    private Double similarity;

    private String suggestion;
}
