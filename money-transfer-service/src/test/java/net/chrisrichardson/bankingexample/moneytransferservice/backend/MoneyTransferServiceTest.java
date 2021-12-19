package net.chrisrichardson.bankingexample.moneytransferservice.backend;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import net.chrisrichardson.bankingexample.moneytransferservice.sagas.TransferMoneySaga;
import net.chrisrichardson.bankingexample.moneytransferservice.sagas.TransferMoneySagaData;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static net.chrisrichardson.bankingexample.moneytransferservice.backend.MoneyTransferMother.moneyTransferInfo;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoneyTransferServiceTest {

  private SagaInstanceFactory sagaInstanceFactory;
  private TransferMoneySaga transferMoneySaga;
  private MoneyTransferService moneyTransferService;
  private MoneyTransferRepository moneyTransferRepository;
  private MoneyTransfer moneyTransfer;

  @Before
  public void setUp() {
    moneyTransferRepository = mock(MoneyTransferRepository.class);
    sagaInstanceFactory = mock(SagaInstanceFactory.class);
    transferMoneySaga = mock(TransferMoneySaga.class);
    moneyTransfer = mock(MoneyTransfer.class);

    moneyTransferService = new MoneyTransferService(moneyTransferRepository, sagaInstanceFactory, transferMoneySaga);
  }

  @Test
  public void shouldCreateMoneyTransfer() {

    when(sagaInstanceFactory.create(eq(transferMoneySaga), any(TransferMoneySagaData.class))).thenAnswer(invocation -> {
      ((TransferMoneySagaData)invocation.getArguments()[1]).setMoneyTransferId(MoneyTransferMother.moneyTransferId);
      return null;
    });

    when(moneyTransferRepository.findById(MoneyTransferMother.moneyTransferId)).thenReturn(Optional.of(moneyTransfer));

    MoneyTransfer mt = moneyTransferService.createMoneyTransfer(moneyTransferInfo);

    assertSame(mt, moneyTransfer);
  }

}
