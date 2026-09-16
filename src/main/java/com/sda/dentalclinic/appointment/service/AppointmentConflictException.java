package com.sda.dentalclinic.appointment.service;

public class AppointmentConflictException
        extends RuntimeException {

    public AppointmentConflictException(String message) {
        super(message);
    }
}