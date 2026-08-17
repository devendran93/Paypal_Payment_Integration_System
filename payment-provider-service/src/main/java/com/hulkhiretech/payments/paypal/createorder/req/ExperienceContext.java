package com.hulkhiretech.payments.paypal.createorder.req;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class ExperienceContext {

    @JsonProperty("payment_method_preference")
    private String paymentMethodPreference;

    @JsonProperty("landing_page")
    private String landingPage;

    @JsonProperty("shipping_preference")
    private String shippingPreference;

    @JsonProperty("user_action")
    private String userAction;

    @JsonProperty("return_url")
    private String returnUrl;

    @JsonProperty("cancel_url")
    private String cancelUrl;
}