package com.samourai.xmanager.server.beans;

public class AddressIndex {
  private String address;
  private int index;

  public AddressIndex(String address, int index) {
    this.address = address;
    this.index = index;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }
}
