package com.company;

import java.util.PrimitiveIterator;

/**
 * @Author:kiritoghy
 * @Date:2019/11/17 15:30
 */
public abstract class Command {
  protected Shell shell;
  
  public Command(Shell shell) {
    this.shell = shell;
  }
  
  abstract void parse(String[] cmd);
  String getRunningProcessName(){
    return shell.getRunningProcessName();
  }
}

class CmdCr extends Command{
  
  public CmdCr(Shell shell) {
    super(shell);
  }
  
  @Override
  void parse(String[] cmd) {
    if(cmd.length != 3){
      System.out.println("[ERROR] It should have 2 args");
      return;
    }
    
    if(!cmd[2].equals("1") && !cmd[2].equals("2")){
      System.out.println("[ERROR] The priority should be 1 or 2");
      return;
    }
    if(shell.create(cmd[1], shell.getRunningPCB(), Integer.parseInt(cmd[2]))){
      System.out.println("* Process " + getRunningProcessName() + " is running");
    }
    else{
      System.out.println("[ERROR] Process " + cmd[1] + " has exits!");
    }
  }
}

class Cmddel extends Command{
  
  public Cmddel(Shell shell) {
    super(shell);
  }
  
  @Override
  void parse(String[] cmd) {
    if(cmd.length != 2){
      System.out.println("[Error] It should have 1 arg");
      return;
    }
    if(shell.deleteProcess(cmd[1])){
      System.out.println("* Process " + cmd[1] + " has been deleted");
      System.out.printf("* Process %s is running\n",getRunningProcessName());
    }
    else{
      System.out.println("[ERROR] No such process");
    }
  }
}

class CmdList extends Command{
  
  public CmdList(Shell shell) {
    super(shell);
  }
  
  @Override
  void parse(String[] cmd) {
    if(cmd.length >= 2 && !cmd[1].equals("process") && !cmd[1].equals("resource")){
      System.out.println("[ERROR] It should be list process or list resource");
      return ;
    }
    if(cmd.length == 2){
      shell.list(cmd[1],-1);
    }
    else if(cmd.length == 3){
      try{
        shell.list(cmd[1], Integer.parseInt(cmd[2]));
      }
      catch (NumberFormatException e){
        System.out.println("[ERROR] The third arg should be a number");
      }
    }
    else{
      System.out.println("[ERROR] It should have no more than 2 arg");
    }
  }
}

class CmdReq extends Command{
  public CmdReq(Shell shell) {
    super(shell);
  }
  
  @Override
  void parse(String[] cmd) {
    if(cmd.length != 3){
      System.out.println("[ERROR] It should have 2 args");
      return ;
    }
    try{
      int rid = Integer.parseInt(cmd[1].substring(1));
      int cnt = Integer.parseInt(cmd[2]);
      String oldName = getRunningProcessName();
      if(rid < 1 || rid > 4) {
        System.out.println("[ERROR] No such resource!");
        return ;
      }
      if(rid < cnt) {
        System.out.println("[ERROR] The count is not right!");
        return ;
      }
      if(shell.request(shell.getRunningPCB(), rid, cnt)){
        System.out.printf("* Process %s request %d %s\n", oldName, cnt, cmd[1]);
        System.out.printf("* Now Process %s is running\n",getRunningProcessName());
      }
      else{
        System.out.println("[ERROR] Request Error");
      }
    }
    catch (NumberFormatException e){
      System.out.println("[ERROR] Argument Error!");
    }
  }
}

class CmdTo extends Command{
  
  public CmdTo(Shell shell) {
    super(shell);
  }
  
  @Override
  void parse(String[] cmd) {
    if(cmd.length != 1){
      System.out.println("[ERROR] It should have no arg");
      return ;
    }
    shell.timeOut();
    System.out.printf("* Process %s is running\n", getRunningProcessName());
  }
}

class CmdRel extends Command{
  
  public CmdRel(Shell shell) {
    super(shell);
  }
  
  @Override
  void parse(String[] cmd) {
    if(cmd.length != 3){
      System.out.println("[ERROR] It should have 2 args");
      return ;
    }
    try{
      int rid = Integer.parseInt(cmd[1].substring(1));
      int cnt = Integer.parseInt(cmd[2]);
      String oldName = getRunningProcessName();
      if(rid < 1 || rid > 4) {
        System.out.println("[ERROR] No such resource!");
        return ;
      }
      if(rid < cnt) {
        System.out.println("[ERROR] The count is not right!");
        return ;
      }
      if(shell.release(shell.getRunningPCB(), rid, cnt)){
        System.out.printf("* Process %s release %d %s\n*Process %s is running\n", oldName, cnt, cmd[1], getRunningProcessName());
      }
      else{
        System.out.printf("[ERROR] Release Error\n");
      }
    }
    catch (NumberFormatException e){
      System.out.println("[ERROR] Argument Error!");
    }
  }
}




