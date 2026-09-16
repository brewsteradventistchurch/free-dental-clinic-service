package com.sda.dentalclinic.counter.service;

import reactor.core.publisher.Mono;

public interface CounterService {

    Mono<Long> nextSequence(String counterName);
}