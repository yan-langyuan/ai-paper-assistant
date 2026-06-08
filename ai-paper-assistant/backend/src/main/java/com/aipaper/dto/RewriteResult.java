package com.aipaper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewriteResult {

    private SchemeOption schemeA;

    private SchemeOption schemeB;

    private SchemeOption schemeC;
}
