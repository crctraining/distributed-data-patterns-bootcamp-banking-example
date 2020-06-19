package net.chrisrichardson.bankingexample.accountservice.common;

import net.chrisrichardson.bankingexample.commondomain.Money;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class AccountInfo {
  private long customerId;
  private String name;
  private Money balance;

  public AccountInfo() {
  }

  public AccountInfo(long customerId, String name, Money balance) {
    this.customerId = customerId;
    this.name = name;
    this.balance = balance;
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

  public long getCustomerId() {
    return customerId;
  }

  public String getName() {
    return name;
  }

  public Money getBalance() {
    return balance;
  }
}
