package com.hulkhiretech.payments.paypal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayPalErrorResponse {

    private String name;

    private String message;

    private String error;

    @JsonProperty("error_description")
    private String errorDescription;

    private List<PayPalErrorDetail> details;
}