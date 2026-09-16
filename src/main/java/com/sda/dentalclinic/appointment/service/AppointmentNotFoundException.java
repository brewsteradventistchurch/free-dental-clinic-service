package com.sda.dentalclinic.appointment.service;

public class AppointmentNotFoundException
        extends RuntimeException {

    public AppointmentNotFoundException(String id) {
        super("Appointment not found: " + id);
    }
}