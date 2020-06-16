package net.chrisrichardson.bankingexample.customerservice.web;

import net.chrisrichardson.bankingexample.customerservice.backend.CustomerBackendConfiguration;
import net.chrisrichardson.eventstore.javaexamples.banking.commonswagger.CommonSwaggerConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CustomerBackendConfiguration.class, CommonSwaggerConfiguration.class})
@ComponentScan
public class CustomerWebConfiguration {
}
