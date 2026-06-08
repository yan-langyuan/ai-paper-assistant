package com.aipaper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemeOption {

    private String text;

    private List<String> keptTerms;

    private String changes;

    private String warning;
}
