package com.aipaper.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReferenceFormatRequest {

    private List<Long> literatureIds;

    private String standard;

    private String sortBy;
}
