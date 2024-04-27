package com.josko.passenger.adapter.bkg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.josko.passenger.adapter.bkg")
public class BookingAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingAdapterApplication.class, args);
	}

}
