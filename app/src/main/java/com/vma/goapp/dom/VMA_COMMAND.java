package com.vma.goapp.dom;

public enum VMA_COMMAND {

    GNRMC(0x100),
    UNKNOWN(0xFFFF);

    private final   int value;

    VMA_COMMAND(int command){
        this.value    = command;
    }
    public int  getValue(){
        return value;
    }
    public static VMA_COMMAND fromId(int id){
        for(VMA_COMMAND cmd: values() ){
            if(cmd.getValue() == id)
                return cmd;
        }
        return  UNKNOWN;
    }
}
