package net.chrisrichardson.bankingexample.moneytransferservice;

import io.eventuate.tram.spring.jdbckafka.TramJdbcKafkaConfiguration;
import net.chrisrichardson.bankingexample.moneytransferservice.web.MoneyTransferWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MoneyTransferWebConfiguration.class,
        TramJdbcKafkaConfiguration.class})
@EnableAutoConfiguration
public class MoneyTransferServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(MoneyTransferServiceMain.class, args);
    }
}
