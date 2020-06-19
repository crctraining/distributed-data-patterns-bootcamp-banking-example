package net.chrisrichardson.bankingexample.moneytransferservice.sagas;

import net.chrisrichardson.bankingexample.accountservice.common.commands.AccountServiceChannels;
import net.chrisrichardson.bankingexample.accountservice.common.commands.CreditCommand;
import net.chrisrichardson.bankingexample.accountservice.common.commands.DebitCommand;
import net.chrisrichardson.bankingexample.commondomain.Money;
import net.chrisrichardson.bankingexample.moneytransferservice.backend.MoneyTransfer;
import net.chrisrichardson.bankingexample.moneytransferservice.backend.MoneyTransferRepository;
import net.chrisrichardson.bankingexample.moneytransferservice.common.MoneyTransferInfo;
import net.chrisrichardson.bankingexample.moneytransferservice.common.MoneyTransferState;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static io.eventuate.tram.sagas.testing.SagaUnitTestSupport.given;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransferMoneySagaTest {

  private long moneyTransferId = 104L;
  private long fromAccountId = 106L;
  private long toAccountId = 107L;
  private Money amount = new Money("78.90");
  private MoneyTransferInfo moneyTransferInfo = new MoneyTransferInfo(fromAccountId, toAccountId, amount);
  private AccountServiceProxy accountServiceProxy = new AccountServiceProxy();
  private MoneyTransferRepository moneyTransferRepository;

  private MoneyTransfer moneyTransfer;

  private TransferMoneySaga makeTransferMoneySaga() {
    return new TransferMoneySaga(accountServiceProxy, moneyTransferRepository);
  }

  @Before
  public void setUp() {
    moneyTransferRepository = mock(MoneyTransferRepository.class);

    when(moneyTransferRepository.save(any(MoneyTransfer.class))).then( invocation -> {
      moneyTransfer = (MoneyTransfer) invocation.getArguments()[0];
      moneyTransfer.setId(moneyTransferId);
      return moneyTransfer;
    });

    when(moneyTransferRepository.findById(moneyTransferId)).thenAnswer(invocation -> Optional.of(moneyTransfer));

  }

  @Test
  public void shouldTransferMoney() {
    given()
      .saga(makeTransferMoneySaga(),
                    new TransferMoneySagaData(moneyTransferInfo)).
    expect().
      command(new DebitCommand(fromAccountId, amount)).
      to(AccountServiceChannels.accountServiceChannel).
    andGiven().
      successReply().
    expect().
      command(new CreditCommand(toAccountId, amount)).
      to(AccountServiceChannels.accountServiceChannel).
    andGiven().
      successReply()
    .expectCompletedSuccessfully();

    assertEquals(MoneyTransferState.COMPLETED, moneyTransfer.getState());
  }

  @Test
  public void shouldFailDueToInsufficientFunds() {
    given()
      .saga(makeTransferMoneySaga(),
                    new TransferMoneySagaData(moneyTransferInfo)).
    expect().
      command(new DebitCommand(fromAccountId, amount)).
      to(AccountServiceChannels.accountServiceChannel).
    andGiven().
      successReply().
    expect().
      command(new CreditCommand(toAccountId, amount)).
      to(AccountServiceChannels.accountServiceChannel).
    andGiven().
      failureReply().
    expect().
      command(new CreditCommand(fromAccountId, amount)).
      to(AccountServiceChannels.accountServiceChannel).
      andGiven().
      successReply()
    .expectRolledBack();

    assertEquals(MoneyTransferState.FAILED_DUE_TO_INSUFFICIENT_FUNDS, moneyTransfer.getState());
  }



}