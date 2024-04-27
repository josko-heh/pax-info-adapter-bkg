package com.josko.passenger.adapter.bkg.process;

import com.josko.passenger.adapter.bkg.dto.FlightBooking;
import com.josko.passenger.update.dto.PassengerUpdate;
import com.josko.passenger.update.dto.keys.KeyDTO;
import com.josko.passenger.update.dto.keys.TicketNumberKeyDTO;
import com.josko.passenger.update.slices.PassengerDetailsData;
import com.josko.passenger.update.slices.Slice;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.josko.passenger.adapter.bkg.Definitions.DS_NAME;
import static com.josko.passenger.update.dto.keys.KeyDTO.Type.TICKET_NUMBER;
import static com.josko.passenger.update.slices.SliceData.Type.DETAILS;

@Component
public class FlightBookingToPassengerUpdateConverter {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("ddMMyy");

	public static PassengerUpdate convert(FlightBooking bkg) {
		PassengerUpdate passengerUpdate = new PassengerUpdate();
		passengerUpdate.setReference(UUID.randomUUID());
		passengerUpdate.setDatasource(DS_NAME);
		passengerUpdate.setUpdateTs(Instant.now());
		
		// Map keys
		TicketNumberKeyDTO ticketNumberKeyDTO = TicketNumberKeyDTO.builder()
				.type(TICKET_NUMBER)
				.carrier(bkg.getTicketDetails().getCarrier())
				.flightNumber(bkg.getTicketDetails().getFlightNumber())
				.departureDate(toLD(bkg.getTicketDetails().getDepartureDate()))
				.ticketNumber(bkg.getTicketDetails().getTicketNumber())
				.build();
		
		List<KeyDTO> keys = new ArrayList<>();
		keys.add(ticketNumberKeyDTO);
		passengerUpdate.setKeys(keys);

		// Map slices
		Slice<PassengerDetailsData> detailsSlice = Slice.<PassengerDetailsData>builder()
				.name(DETAILS)
				.content(PassengerDetailsData.builder()
						.firstName(bkg.getPassenger().getFirstName())
						.lastName(bkg.getPassenger().getLastName())
						.title(bkg.getPassenger().getTitle())
						.gender(bkg.getPassenger().getGender())
						.dateOfBirth(toLD(bkg.getPassenger().getDateOfBirth()))
						.city(bkg.getPassenger().getAddress().getCity())
						.zipCode(bkg.getPassenger().getAddress().getZipCode())
						.streetAndNumber(bkg.getPassenger().getAddress().getStreetAndNumber())
						.country(bkg.getPassenger().getAddress().getCountry())
						.language(bkg.getPassenger().getPreferredLanguage())
						.build())
				.updateTs(Instant.now())
				.build();
		
		List<Slice> slices = new ArrayList<>();
		slices.add(detailsSlice);
		passengerUpdate.setSlices(slices);

		return passengerUpdate;
	}

	private static LocalDate toLD(String dateString) {
		return LocalDate.parse(dateString, FORMATTER);
	}
}
