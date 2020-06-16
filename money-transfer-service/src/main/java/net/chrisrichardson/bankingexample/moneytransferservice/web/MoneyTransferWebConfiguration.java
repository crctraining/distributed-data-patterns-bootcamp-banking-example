package net.chrisrichardson.bankingexample.moneytransferservice.web;

import net.chrisrichardson.bankingexample.moneytransferservice.backend.MoneyTransferBackendConfiguration;
import net.chrisrichardson.eventstore.javaexamples.banking.commonswagger.CommonSwaggerConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MoneyTransferBackendConfiguration.class, CommonSwaggerConfiguration.class})
@ComponentScan
public class MoneyTransferWebConfiguration {
}
