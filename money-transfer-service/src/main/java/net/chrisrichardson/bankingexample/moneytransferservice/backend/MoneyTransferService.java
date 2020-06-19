package net.chrisrichardson.bankingexample.moneytransferservice.backend;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import net.chrisrichardson.bankingexample.moneytransferservice.common.MoneyTransferInfo;
import net.chrisrichardson.bankingexample.moneytransferservice.sagas.TransferMoneySaga;
import net.chrisrichardson.bankingexample.moneytransferservice.sagas.TransferMoneySagaData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MoneyTransferService {

  private final MoneyTransferRepository moneyTransferRepository;
  private final SagaInstanceFactory sagaInstanceFactory;
  private final TransferMoneySaga transferMoneySaga;

  public MoneyTransferService(MoneyTransferRepository moneyTransferRepository,
                              SagaInstanceFactory sagaInstanceFactory,
                              TransferMoneySaga transferMoneySaga) {
    this.moneyTransferRepository = moneyTransferRepository;
    this.sagaInstanceFactory = sagaInstanceFactory;
    this.transferMoneySaga = transferMoneySaga;
  }

  public MoneyTransfer createMoneyTransfer(MoneyTransferInfo moneyTransferInfo) {
    TransferMoneySagaData data = createTransferMoneySaga(moneyTransferInfo);
    return moneyTransferRepository.findById(data.getMoneyTransferId()).get();
  }

  private TransferMoneySagaData createTransferMoneySaga(MoneyTransferInfo moneyTransferInfo) {
    throw new RuntimeException("not yet implemented");
  }

  public Optional<MoneyTransfer> findMoneyTransfer(long id) {
    return moneyTransferRepository.findById(id);
  }

  public void completeTransfer(long moneyTransferId) {
    moneyTransferRepository.findById(moneyTransferId).get().complete();
  }

  public void cancelTransfer(long moneyTransferId) {
    moneyTransferRepository.findById(moneyTransferId).get().cancel();
  }
}
