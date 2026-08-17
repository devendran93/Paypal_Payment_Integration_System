package com.hulkhiretech.payments.paypal.createorder.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hulkhiretech.payments.paypal.createorder.req.Paypal;

import lombok.Data;

@Data
public class PaymentSource {

    @JsonProperty("paypal")
    private Paypal paypal;
}