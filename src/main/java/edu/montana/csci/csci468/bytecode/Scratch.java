package edu.montana.csci.csci468.bytecode;

import java.util.LinkedList;

public class Scratch {

    public void main(){

        String y = String.valueOf("null").concat(String.valueOf("bar"));

        LinkedList<Object> x = new LinkedList<>();
        x.add(1);
        x.add(2);
        x.add(3);

    }

    public int intFunc(int i1, int i2) {
        return i1 + i2;
    }
}
