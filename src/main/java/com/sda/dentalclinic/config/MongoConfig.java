package com.sda.dentalclinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@Configuration(proxyBeanMethods = false)
@EnableReactiveMongoAuditing
public class MongoConfig {
}