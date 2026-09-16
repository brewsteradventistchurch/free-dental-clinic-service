package com.sda.dentalclinic.patient.service;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(String id) {
        super("Patient not found: " + id);
    }
}