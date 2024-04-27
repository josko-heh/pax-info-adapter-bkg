package com.josko.passenger.adapter.bkg.process;

import com.josko.passenger.adapter.bkg.dto.FlightBooking;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FlightBookingRoute extends RouteBuilder {

	@Value("${bookings.jms.source}")
	private String fromUri;
	
	@Value("${passengers.update.jms.target}")
	private String toUri;
	
	@Autowired
	private FlightBookingToPassengerUpdateConverter converter;
	
	
	@Override
	public void configure() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(FlightBooking.class);
		JaxbDataFormat jaxbDataFormat = new JaxbDataFormat(jaxbContext);
		
		from(fromUri)
				.unmarshal(jaxbDataFormat)
				.bean(converter)
				.to(toUri);
	}
}
