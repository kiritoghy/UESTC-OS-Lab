package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static java.lang.System.exit;

/**
 * @Author:kiritoghy
 * @Date:2019/11/17 15:08
 */
public class Shell {
  private List<RCB>rcbs;
  private Queue<PCB>[] processQueue;
  private Map<Integer,PCB>processList;
  private int runningPCB;
  private int nextPid;
  private Map<String, Command>commandMap;
  
  Shell(){
    init();
  }
  
  public void init(){
    //rcb init
    rcbs = new ArrayList<>();
    for(int i = 1; i <= 4; ++i){
      RCB rcb = new RCB(i,i);
      rcbs.add(rcb);
    }
    //pcb queue init
    processQueue = new LinkedList<PCB>().toArray(new Queue[3]);
    processQueue[0] = new LinkedList<>();
    processQueue[1] = new LinkedList<>();
    processQueue[2] = new LinkedList<>();
    processList = new HashMap<>();
    nextPid = 0;
    
    //command init
    commandMap = new HashMap<>();
    commandMap.put("cr",new CmdCr(this));
    commandMap.put("de",new Cmddel(this));
    commandMap.put("list", new CmdList(this));
    commandMap.put("req", new CmdReq(this));
    commandMap.put("to", new CmdTo(this));
    commandMap.put("rel", new CmdRel(this));
    
    //process init
    create("init",-1,0);
    System.out.println("Process init is running");
  }
  public boolean isNameExits(String name){
    for(PCB pcb : processList.values()){
      if(name.equals(pcb.getName()))return true;
    }
    return false;
  }
  public void scheduler(){
    if(!processQueue[2].isEmpty()){
      this.runningPCB = processQueue[2].peek().getPid();
      processList.get(this.runningPCB).setStatus("running");
      return;
    }
    else if(!processQueue[1].isEmpty()){
      this.runningPCB = processQueue[1].peek().getPid();
      processList.get(this.runningPCB).setStatus("running");
      return;
    }
    else{
      this.runningPCB = 0;
      processList.get(0).setStatus("running");
    }
  }
  
  public boolean create(String name, int parent, int priority){
    if(isNameExits(name))return false;
    PCB pcb = new PCB(parent, this.nextPid, name, priority,"ready");
    if(parent != -1){
      processList.get(parent).getChild().add(this.nextPid);
    }
    processList.put(this.nextPid, pcb);
    processList.get(this.runningPCB).setStatus("ready");
    processQueue[pcb.getPriority()].add(pcb);
    this.nextPid++;
    scheduler();
    return true;
  }
  
  public boolean deleteProcess(String name){
    PCB pcb = null;
    for(PCB p : processList.values()){
      if(name.equals("init")){
        System.out.println("init process has been deleted, the shell will be closed");
        exit(0);
      }
      if(name.equals(p.getName())) pcb = p;
    }
    if(pcb == null)return false;
    for(Integer pid:pcb.getChild())
      deleteProcess(processList.get(pid).getName());
    for(Resource resource:pcb.getResources())release(pcb.getPid(),resource.getRid(),resource.getCount());
    
    processList.remove(pcb.getPid());
    if(pcb.getParent() != -1)
    processList.get(pcb.getParent()).getChild().remove((Object)pcb.getPid());
    processQueue[pcb.getPriority()].remove(pcb);
    scheduler();
    return true;
  }
  
  public boolean release(int pid, int rid, int cnt){
    PCB pcb = processList.get(pid);
    RCB rcb = null;
    if(!pcb.sub(rid, cnt)) return false;
    for(RCB r : rcbs){
      if(rid == r.getRid())rcb = r;
    }
    rcb.setNumber(rcb.getNumber() + cnt);
    for(Map.Entry<Integer,Integer> entry : rcb.getWaitingList().entrySet()){
      
      if(entry.getValue() > rcb.getNumber())continue;
      
      PCB bpcb = processList.get(entry.getKey());
      request(bpcb.getPid(), rid, entry.getValue());
      bpcb.setWaiting(bpcb.getWaiting() - 1);
      rcb.getWaitingList().remove(bpcb.getPid());
      if(bpcb.getWaiting() == 0){
        bpcb.setStatus("ready");
        System.out.printf("[INFO] Process %s has woken up\n", bpcb.getName());
        processQueue[bpcb.getPriority()].add(bpcb);
      }
      break;
    }
    scheduler();
    return true;
  }
  public boolean request(int pid, int rid, int cnt){
    RCB rcb = null;
    PCB pcb = processList.get(pid);
    for(RCB r : rcbs){
      if(r.getRid() == rid)rcb = r;
    }
    for(Resource r : pcb.getResources()){
      if(r.getRid() == rid && r.getCount() + cnt > rcb.getNumber())return false;
    }
    if(rcb != null && rcb.getNumber() >= cnt){
      rcb.sub(cnt);
      Resource resource = null;
      for(Resource r : pcb.getResources()){
        if(r.getRid() == rid)resource = r;
      }
      if(resource != null)
        resource.setCount(resource.getCount() + cnt);
      else
        pcb.getResources().add(new Resource(rid, cnt));
      
        return true;
    }
    else{
      pcb.setStatus("block");
      System.out.printf("[INFO] Process %s has been blocked\n",pcb.getName());
      processQueue[pcb.getPriority()].remove(pcb);
      rcb.getWaitingList().put(pcb.getPid(), cnt);
      pcb.setWaiting(pcb.getWaiting() + 1);
      scheduler();
      return true;
    }
  }
  public void timeOut(){
    PCB pcb = processList.get(this.runningPCB);
    processQueue[pcb.getPriority()].remove(pcb);
    pcb.setStatus("ready");
    processQueue[pcb.getPriority()].add(pcb);
    scheduler();
  }
  public void runCmd(){
    while(true){
      System.out.print("shell>");
      Scanner scanner = new Scanner(System.in);
      String cmdLine = scanner.nextLine().trim();
      String[] cmd = cmdLine.split(" ");
      if(cmd.length == 0)continue;
      if(commandMap.containsKey(cmd[0])){
        commandMap.get(cmd[0]).parse(cmd);
      }
      else{
        System.out.println("No such command");
      }
    }
  }
  public void runFile(){
    FileReader fr = null;
    try {
      fr = new FileReader("src/com/company/test.txt");
      BufferedReader bf = new BufferedReader(fr);
      String cmdLine;
      while((cmdLine = bf.readLine()) != null){
        System.out.print("shell>\n");
        String[] cmd = cmdLine.trim().split(" ");
        if(cmd.length == 0)continue;
        if(commandMap.containsKey(cmd[0])){
          commandMap.get(cmd[0]).parse(cmd);
        }
        else{
          System.out.println("No such command");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void list(String type, int id){
    if(type.equals("process")){
      if(id == -1){
        for(PCB pcb: processList.values()) System.out.println(pcb.toString());
      }
      else {
        for(PCB pcb:processList.values()){
          if(id == pcb.getPid()){
            System.out.println(pcb.toString());
            return ;
          }
        }
      }
    }
    else if(type.equals("resource")){
      if(id == -1){
        for(RCB rcb : rcbs) System.out.println(rcb.toString());
      }
      else{
        for(RCB rcb : rcbs){
          if(rcb.getRid() == id){
            System.out.println(rcb.toString());
            return;
          }
        }
      }
    }
  }
  public String getRunningProcessName(){
    return processList.get(this.runningPCB).getName();
  }
  public int getRunningPCB(){
    return this.runningPCB;
  }
}
