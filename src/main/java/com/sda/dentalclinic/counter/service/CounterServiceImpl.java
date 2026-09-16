package com.sda.dentalclinic.counter.service;

import com.sda.dentalclinic.counter.model.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CounterServiceImpl implements CounterService {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Long> nextSequence(String counterName) {

        Query query = Query.query(
                Criteria.where("_id").is(counterName)
        );

        Update update = new Update()
                .inc("sequence", 1);

        FindAndModifyOptions options =
                FindAndModifyOptions.options()
                        .upsert(true)
                        .returnNew(true);

        return mongoTemplate.findAndModify(
                        query,
                        update,
                        options,
                        Counter.class
                )
                .map(Counter::getSequence);
    }
}