package com.josko.passenger.adapter.bkg.process;

import com.josko.passenger.adapter.bkg.dto.FlightBooking;
import com.josko.passenger.adapter.bkg.dto.Passenger;
import com.josko.passenger.adapter.bkg.dto.Ticket;
import com.josko.passenger.update.dto.PassengerUpdate;
import com.josko.passenger.update.dto.keys.KeyDTO;
import com.josko.passenger.update.dto.keys.PnrKeyDTO;
import com.josko.passenger.update.dto.keys.TicketNumberKeyDTO;
import com.josko.passenger.update.slices.BookingData;
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
import static com.josko.passenger.update.dto.keys.KeyDTO.Type.PNR;
import static com.josko.passenger.update.dto.keys.KeyDTO.Type.TICKET_NUMBER;
import static com.josko.passenger.update.slices.SliceData.Type.BOOKING;
import static com.josko.passenger.update.slices.SliceData.Type.DETAILS;

@Component
public class FlightBookingToPassengerUpdateConverter {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("ddMMyy");

	public static PassengerUpdate convert(FlightBooking bkg) {
		PassengerUpdate passengerUpdate = new PassengerUpdate();
		
		passengerUpdate.setReference(UUID.randomUUID());
		passengerUpdate.setDatasource(DS_NAME);
		passengerUpdate.setUpdateTs(Instant.now());
		passengerUpdate.setKeys(getKeys(bkg));
		passengerUpdate.setSlices(getSlices(bkg));

		return passengerUpdate;
	}

	private static List<Slice> getSlices(FlightBooking bkg) {
		List<Slice> slices = new ArrayList<>();

		Passenger passenger = bkg.getPassenger();
		
		Slice<PassengerDetailsData> detailsSlice = Slice.<PassengerDetailsData>builder()
				.name(DETAILS)
				.content(PassengerDetailsData.builder()
						.firstName(passenger.getFirstName())
						.lastName(passenger.getLastName())
						.title(passenger.getTitle())
						.gender(passenger.getGender())
						.dateOfBirth(toLD(passenger.getDateOfBirth()))
						.city(passenger.getAddress().getCity())
						.zipCode(passenger.getAddress().getZipCode())
						.streetAndNumber(passenger.getAddress().getStreetAndNumber())
						.country(passenger.getAddress().getCountry())
						.language(passenger.getPreferredLanguage())
						.build())
				.updateTs(Instant.now())
				.build();
		slices.add(detailsSlice);

		Slice<BookingData> bookingDataSlice = Slice.<BookingData>builder()
				.name(BOOKING)
				.content(BookingData.builder()
						.locator(bkg.getLocator())
						.tattoo(bkg.getTattoo())
						.pointOfSale(bkg.getPointOfSale())
						.build())
				.updateTs(Instant.now())
				.build();
		slices.add(bookingDataSlice);
		
		return slices;
	}

	private static List<KeyDTO> getKeys(FlightBooking bkg) {
		List<KeyDTO> keys = new ArrayList<>();

		Ticket ticket = bkg.getTicketDetails();
		Passenger passenger = bkg.getPassenger();
		
		if (ticket != null) {
			var ticketNumberKey = TicketNumberKeyDTO.builder()
					.type(TICKET_NUMBER)
					.carrier(ticket.getCarrier())
					.flightNumber(ticket.getFlightNumber())
					.departureDate(toLD(ticket.getDepartureDate()))
					.ticketNumber(ticket.getTicketNumber())
					.build();
			keys.add(ticketNumberKey);
		}

		var pnrKey = PnrKeyDTO.builder()
				.type(PNR)
				.firstName(passenger.getFirstName())
				.lastName(passenger.getLastName())
				.locator(bkg.getLocator())
				.build();
		keys.add(pnrKey);
		
		return keys;
	}

	private static LocalDate toLD(String dateString) {
		return LocalDate.parse(dateString, FORMATTER);
	}
}
