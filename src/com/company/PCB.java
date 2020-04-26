package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:kiritoghy
 * @Date:2019/11/17 14:52
 */
public class PCB {
  private int parent;//-1 is init's parent process
  private int pid;//0 is init process
  private String name;
  private int priority;//0 is init, 1 is user, 2 is system
  private String status;
  private List<Integer> child;
  private List<Resource>resources;
  private int waiting;
  
  public PCB(int parent, int pid, String name, int priority, String status) {
    this.parent = parent;
    this.pid = pid;
    this.name = name;
    this.priority = priority;
    this.status = status;
    child = new ArrayList<>();
    resources = new ArrayList<>();
    waiting = 0;
  }
  
  public int getParent() {
    return parent;
  }
  
  public void setParent(int parent) {
    this.parent = parent;
  }
  
  public int getPid() {
    return pid;
  }
  
  public void setPid(int pid) {
    this.pid = pid;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getPriority() {
    return priority;
  }
  
  public void setPriority(int priority) {
    this.priority = priority;
  }
  
  public String getStatus() {
    return status;
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
  
  public List<Integer> getChild() {
    return child;
  }
  
  public void setChild(List<Integer> child) {
    this.child = child;
  }
  
  public List<Resource> getResources() {
    return resources;
  }
  
  public void setResources(List<Resource> resources) {
    this.resources = resources;
  }
  
  public boolean sub(int rid, int cnt){
    for(Resource r : resources){
      if(r.getRid() == rid) {
        if(r.getCount() < cnt)return false;
        else {
          r.setCount(r.getCount() - cnt);
        }
      }
    }
    return true;
  }
  
  public int getWaiting() {
    return waiting;
  }
  
  public void setWaiting(int waiting) {
    this.waiting = waiting;
  }
  
  @Override
  public String toString() {
    return "PCB{" +
      "parent=" + parent +
      ", pid=" + pid +
      ", name='" + name + '\'' +
      ", priority=" + priority +
      ", status='" + status + '\'' +
      ", child=" + child +
      ", resources=" + resources +
      ", waiting=" + waiting +
      '}';
  }
}
