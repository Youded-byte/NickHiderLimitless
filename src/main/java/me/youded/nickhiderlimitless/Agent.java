package me.youded.nickhiderlimitless;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String args, Instrumentation inst){
        inst.addTransformer(new Transformer(), true);
    }
    public static void agentmain(Instrumentation inst){
        premain(null, inst);
    }
}