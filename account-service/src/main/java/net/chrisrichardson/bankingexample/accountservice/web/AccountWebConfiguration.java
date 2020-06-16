package net.chrisrichardson.bankingexample.accountservice.web;

import net.chrisrichardson.bankingexample.accountservice.backend.AccountBackendConfiguration;
import net.chrisrichardson.eventstore.javaexamples.banking.commonswagger.CommonSwaggerConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AccountBackendConfiguration.class, CommonSwaggerConfiguration.class,})
@ComponentScan
public class AccountWebConfiguration {
}
