package io.xsun.xcs4j.parser.impl.automata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AutomataNode implements Cloneable,Serializable {

    private final Map<Character, Integer> moveTable;

    public AutomataNode(){
        moveTable = new HashMap<>();
    }


}
