package com.hulkhiretech.payments.paypal.createorder.req;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class Amount {

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("value")
    private String value;
}