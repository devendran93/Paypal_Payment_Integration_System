package com.hulkhiretech.payments.paypal.createorder.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hulkhiretech.payments.paypal.createorder.req.PaymentSource;

import lombok.Data;

@Data
public class PPOrderResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("payment_source")
    private PaymentSource paymentSource;

    @JsonProperty("links")
    private List<Link> links;
}