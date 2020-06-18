package net.chrisrichardson.bankingexample.apigateway.apigateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "api.gateway.destinations")
public class ApiGatewayDestinations {

  @NotNull
  private String accountServiceUrl;

  @NotNull
  private String customerServiceUrl;

  @NotNull
  private String moneyTransferServiceUrl;

  @NotNull
  private String customerViewServiceUrl;

  public String getAccountServiceUrl() {
    return accountServiceUrl;
  }

  public void setAccountServiceUrl(String accountServiceUrl) {
    this.accountServiceUrl = accountServiceUrl;
  }

  public String getCustomerServiceUrl() {
    return customerServiceUrl;
  }

  public void setCustomerServiceUrl(String customerServiceUrl) {
    this.customerServiceUrl = customerServiceUrl;
  }

  public String getMoneyTransferServiceUrl() {
    return moneyTransferServiceUrl;
  }

  public void setMoneyTransferServiceUrl(String moneyTransferServiceUrl) {
    this.moneyTransferServiceUrl = moneyTransferServiceUrl;
  }

  public String getCustomerViewServiceUrl() {
    return customerViewServiceUrl;
  }

  public void setCustomerViewServiceUrl(String customerViewServiceUrl) {
    this.customerViewServiceUrl = customerViewServiceUrl;
  }
}
