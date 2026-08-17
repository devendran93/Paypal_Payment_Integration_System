package com.hulkhiretech.payments.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayPalErrorDetail {

    private String field;

    private String value;

    private String location;

    private String issue;

    private String description;
}