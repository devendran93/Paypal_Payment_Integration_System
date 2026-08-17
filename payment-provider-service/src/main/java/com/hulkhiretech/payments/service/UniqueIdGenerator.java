package com.hulkhiretech.payments.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UniqueIdGenerator {

	public String getUniqueReqId() {
		log.info("Generating unique request ID");
		return UUID.randomUUID().toString();
	}
	
}
