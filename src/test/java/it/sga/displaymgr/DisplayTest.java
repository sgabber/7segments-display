package it.sga.displaymgr;

import java.util.List;

class DisplayTest {

    @org.junit.jupiter.api.Test
    void calculateTextList() {
        List<String> strings = Display.calculateTextList("FrA.n");
        strings.forEach(System.out::println);
        assert (strings.size() == 4);
    }
}