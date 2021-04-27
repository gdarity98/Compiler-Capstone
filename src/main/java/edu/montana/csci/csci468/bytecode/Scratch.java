package edu.montana.csci.csci468.bytecode;

import java.util.LinkedList;

public class Scratch {
    int x = 10;
    public void main(String[] args){
       intFunc(x);
       intFunc(x);
       System.out.println(intFunc(x));
    }
    public int intFunc(int i1) {
        System.out.println(i1);
        return i1;
    }
}
