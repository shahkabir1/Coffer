package com.shahkabir.coffer.service;


import com.shahkabir.coffer.dto.FxRateResponse;
import com.shahkabir.coffer.model.CurrencyType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Locale;

@Service
public class FxQuoteService {

    private final RestClient client = RestClient.create(
            "https://api.frankfurter.dev"
    );

    public FxRateResponse getRate(CurrencyType from, CurrencyType to) {
        if (from == to) {
            return new FxRateResponse(
                    null,
                    from.name(),
                    to.name(),
                    BigDecimal.ONE
            );
        }

        FxRateResponse response = client.get()
                .uri("/v2/rate/{base}/{quote}",
                        from.name().toLowerCase(Locale.ROOT),
                        to.name().toLowerCase(Locale.ROOT))
                .retrieve()
                .body(FxRateResponse.class);

        if (response == null || response.rate() == null
            || response.rate().signum() <= 0) {
            throw new IllegalStateException("FX rate unavailable");
        }
        return response;
    }
}
