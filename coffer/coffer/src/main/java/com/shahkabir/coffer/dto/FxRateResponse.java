package com.shahkabir.coffer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FxRateResponse (
    LocalDate date,
    String base,
    String quote,
    BigDecimal rate
) {}
