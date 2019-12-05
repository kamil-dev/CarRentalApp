package com.mkcomp.CarRentalApp.service;

import com.mkcomp.CarRentalApp.api.request.AddReservationRequest;
import com.mkcomp.CarRentalApp.model.Reservation;

import java.util.Set;

public interface ReservationService {

    Long addReservation(AddReservationRequest request);
    Set<Reservation> getAllReservations(long customerId);
    boolean deleteReservation(long reservationId);
    boolean makeAnAdvancePayment(long reservationId);

}