package net.chrisrichardson.bankingexample.customerservice.backend;

import io.eventuate.common.spring.jdbc.EventuateTransactionTemplateConfiguration;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;

import net.chrisrichardson.bankingexample.customerservice.common.CustomerInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CustomerServiceIntegrationTest.CustomerServiceIntegrationTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomerServiceIntegrationTest {

  @Autowired
  private CustomerService customerService;

  @Test
  public void shouldSaveAndLoadCustomer() {

    CustomerInfo customerInfo = CustomerMother.makeCustomer();

    Customer savedCustomer = customerService.createCustomer(customerInfo);

    Optional<Customer> loadedCustomer = customerService.findCustomer(savedCustomer.getId());

    assertTrue(loadedCustomer.isPresent());

    assertEquals(customerInfo, loadedCustomer.get().getCustomerInfo());
  }

  @Configuration
  @Import({CustomerBackendConfiguration.class, TramInMemoryConfiguration.class, EventuateTransactionTemplateConfiguration.class})
  @EnableAutoConfiguration
  public static class CustomerServiceIntegrationTestConfiguration {



  }
}