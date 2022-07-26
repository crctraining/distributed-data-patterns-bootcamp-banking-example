package net.chrisrichardson.bankingexample.accountservice.backend;

import io.eventuate.common.spring.jdbc.EventuateTransactionTemplateConfiguration;
import io.eventuate.tram.spring.inmemory.TramInMemoryConfiguration;

import net.chrisrichardson.bankingexample.accountservice.common.AccountInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AccountServiceIntegrationTest.Config.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AccountServiceIntegrationTest {


  @Autowired
  private AccountService accountService;

  @Test
  public void shouldSaveAndLoadAccount() {

    AccountInfo accountInfo = AccountMother.makeAccount();

    // Account savedAccount = accountService.openAccount(accountInfo);
    Account savedAccount = accountService.openAccount(accountInfo);

    //    Account loadedAccount = accountService.findAccount(savedAccount.getId());
    Optional<Account> loadedAccount = accountService.findAccount(savedAccount.getId());

    assertTrue(loadedAccount.isPresent());

    assertEquals(accountInfo, loadedAccount.get().getAccountInfo());
  }

  @Configuration
  @Import({AccountBackendConfiguration.class,
          TramInMemoryConfiguration.class, EventuateTransactionTemplateConfiguration.class
  })
  @EnableAutoConfiguration
  public static class Config {



  }
}
