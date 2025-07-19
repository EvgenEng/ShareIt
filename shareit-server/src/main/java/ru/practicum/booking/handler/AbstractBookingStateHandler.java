package ru.practicum.booking.handler;

import ru.practicum.booking.BookingRepository;

public abstract class AbstractBookingStateHandler implements BookingStateHandler {
    protected final BookingRepository bookingRepository;
    protected final String supportedState;

    protected AbstractBookingStateHandler(BookingRepository bookingRepository, String supportedState) {
        this.bookingRepository = bookingRepository;
        this.supportedState = supportedState;
    }

    @Override
    public boolean canHandle(String state) {
        return supportedState.equalsIgnoreCase(state);
    }
}
