package net.chrisrichardson.bankingexample.apigateway.apigateway.proxies;

public class UnknownProxyException extends RuntimeException{
  public UnknownProxyException(String message) {
    super(message);
  }
}
