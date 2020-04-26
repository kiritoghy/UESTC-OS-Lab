package com.company;

/**
 * @Author:kiritoghy
 * @Date:2019/11/17 15:06
 */
public class Resource {
  private int rid;
  private int count;
  
  public Resource(int rid, int count) {
    this.rid = rid;
    this.count = count;
  }
  
  public int getRid() {
    return rid;
  }
  
  public void setRid(int rid) {
    this.rid = rid;
  }
  
  public int getCount() {
    return count;
  }
  
  public void setCount(int count) {
    this.count = count;
  }
  
  @Override
  public String toString() {
    return "Resource{" +
      "rid=" + rid +
      ", count=" + count +
      '}';
  }
}
