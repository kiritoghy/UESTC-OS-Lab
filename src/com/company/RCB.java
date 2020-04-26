package com.company;

import java.util.*;

/**
 * @Author:kiritoghy
 * @Date:2019/11/17 14:56
 */
public class RCB {
  private int rid;
  private Map<Integer, Integer> waitingList;
  private int number;
  
  public RCB(int rid, int number) {
    this.rid = rid;
    this.number = number;
    waitingList = new HashMap<>();
  }
  
  public int getRid() {
    return rid;
  }
  
  public void setRid(int rid) {
    this.rid = rid;
  }
  
  public Map<Integer, Integer> getWaitingList() {
    return waitingList;
  }
  
  public void setWaitingList(Map<Integer, Integer> waitingList) {
    this.waitingList = waitingList;
  }
  
  public int getNumber() {
    return number;
  }
  
  public void setNumber(int number) {
    this.number = number;
  }
  
  public void sub(int cnt){
    this.number -= cnt;
  }
  
  public void add(int cnt){
    this.number += cnt;
  }
  
  @Override
  public String toString() {
    return "RCB{" +
      "rid=" + rid +
      ", waitingList=" + waitingList +
      ", number=" + number +
      '}';
  }
}
