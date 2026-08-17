package com.hulkhiretech.payments.service.constants;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionStatusEnum {

	CREATED(1, "CREATED"),
	INITIATED(2, "INITIATED"),
	PENDING(3, "PENDING"),
	APPROVED(4, "APPROVED"),
	SUCCESS(5, "SUCCESS"),
	FAILED(6, "FAILED");

	private final int id;
	private final String name;

	public static TransactionStatusEnum getById(int id) {
		return Arrays.stream(values())
				.filter(status -> status.id == id)
				.findFirst()
				.orElseThrow(() ->
				new IllegalArgumentException("Invalid Transaction Status Id: " + id));
	}	

	public static TransactionStatusEnum getByName(String name) {
		return Arrays.stream(values())
				.filter(status -> status.name.equalsIgnoreCase(name))
				.findFirst()
				.orElseThrow(() ->
				new IllegalArgumentException("Invalid Transaction Status name: " + name));
	}	

}
