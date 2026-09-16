package com.sda.dentalclinic.configuration.repository;

import com.sda.dentalclinic.configuration.model.ClinicConfiguration;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ClinicConfigurationRepository
        extends ReactiveMongoRepository<ClinicConfiguration, String> {
}