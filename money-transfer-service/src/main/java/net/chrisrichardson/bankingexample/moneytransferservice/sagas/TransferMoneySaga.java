package net.chrisrichardson.bankingexample.moneytransferservice.sagas;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import net.chrisrichardson.bankingexample.moneytransferservice.backend.MoneyTransfer;
import net.chrisrichardson.bankingexample.moneytransferservice.backend.MoneyTransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferMoneySaga implements SimpleSaga<TransferMoneySagaData> {


  private Logger logger = LoggerFactory.getLogger(getClass());

  private SagaDefinition<TransferMoneySagaData> sagaDefinition;

  private final MoneyTransferRepository moneyTransferRepository;

  public TransferMoneySaga(AccountServiceProxy accountingService, MoneyTransferRepository moneyTransferRepository) {
    this.moneyTransferRepository = moneyTransferRepository;
    initializeSagaDefinition(accountingService);
  }

  private void initializeSagaDefinition(AccountServiceProxy accountingService) {
    this.sagaDefinition =
             step()
              .invokeLocal(this::createMoneyTransfer)       
              .withCompensation(this::cancel)
            .step()
              .invokeParticipant(accountingService.debit, TransferMoneySagaData::makeDebitAccountCommand)
              .withCompensation(accountingService.credit, TransferMoneySagaData::makeReverseDebitCommand)
            .step()
              .invokeParticipant(accountingService.credit, TransferMoneySagaData::makeCreditAccountCommand)
            .step()
              .invokeLocal(this::complete)
            .build();
  }



  private void createMoneyTransfer(TransferMoneySagaData data) {
    MoneyTransfer mt = new MoneyTransfer(data.getMoneyTransferInfo());
    mt = moneyTransferRepository.save(mt);
    data.setMoneyTransferId(mt.getId());
  }

  private void complete(TransferMoneySagaData data) {
    moneyTransferRepository.findById(data.getMoneyTransferId()).get().complete();
  }

  private void cancel(TransferMoneySagaData data) {
    moneyTransferRepository.findById(data.getMoneyTransferId()).get().cancel();
  }

  @Override
  public SagaDefinition<TransferMoneySagaData> getSagaDefinition() {
    return sagaDefinition;
  }


}
