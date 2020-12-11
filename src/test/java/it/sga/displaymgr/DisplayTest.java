package it.sga.displaymgr;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DisplayTest {

    @org.junit.jupiter.api.Test
    void calculateTextList() {
        List<String> strings = Display.calculateTextList("FrA.n", 4);
        strings.forEach(System.out::println);
        assert (strings.size() == 4);
    }
}