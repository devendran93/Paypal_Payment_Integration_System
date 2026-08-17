package com.hulkhiretech.payments.service.interfaces;

import org.springframework.web.bind.annotation.RequestBody;

import com.hulkhiretech.payments.pojo.OrderRes;
import com.hulkhiretech.payments.pojo.PaypalCreateOrderReq;

public interface OrderService {

	OrderRes createOrder(@RequestBody PaypalCreateOrderReq request); //TODO

	OrderRes captureOrder(String orderId); // TODO
}
