package it.sga.displaymgr.utils;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.sga.displaymgr.utils.Segment.*;

public class Letters {

    static final List<Segment> _A = Arrays.asList(A, B, C, E, F, G);
    static final List<Segment> _B = Arrays.asList(A, B, C, D, E, F, G);
    static final List<Segment> _b = Arrays.asList(F, E, D, C, G);
    static final List<Segment> _C = Arrays.asList(A, F, E, D);
    static final List<Segment> _c = Arrays.asList(G, E, D);
    static final List<Segment> _d = Arrays.asList(G, E, D, C, B);
    static final List<Segment> _E = Arrays.asList(A, F, E, D, G);
    static final List<Segment> _e = Arrays.asList(A, B, F, D, E, G);
    static final List<Segment> _F = Arrays.asList(A, G, E, F);
    static final List<Segment> _f = Arrays.asList(A, G, E, F);
    static final List<Segment> _G = Arrays.asList(A, F, E, D, C);
    static final List<Segment> _g = Arrays.asList(B, A, F, G, C, D);
    static final List<Segment> _H = Arrays.asList(B, C, E, F, G);
    static final List<Segment> _h = Arrays.asList(E, C, F, G);
    static final List<Segment> _I = Arrays.asList(B, C);
    static final List<Segment> _i = Arrays.asList(C);
    static final List<Segment> _J = Arrays.asList(B, C, D, E);
    static final List<Segment> _j = Arrays.asList(B, C, D);
    static final List<Segment> _L = Arrays.asList(F, E, D);
    static final List<Segment> _l = Arrays.asList(F, E);
    static final List<Segment> _N = Arrays.asList(B, C, A, E, F);
    static final List<Segment> _n = Arrays.asList(G, E, C);
    static final List<Segment> _O = Arrays.asList(A, B, C, D, E, F);
    static final List<Segment> _o = Arrays.asList(C, D, E, G);
    static final List<Segment> _P = Arrays.asList(A, B, E, F, G);
    static final List<Segment> _q = Arrays.asList(A, B, C, F, G);
    static final List<Segment> _r = Arrays.asList(E, G);
    static final List<Segment> _S = Arrays.asList(A, C, D, F, G);
    static final List<Segment> _t = Arrays.asList(D, E, F, G);
    static final List<Segment> _U = Arrays.asList(B, C, D, E, F);
    static final List<Segment> _u = Arrays.asList(C, D, E);
    static final List<Segment> _y = Arrays.asList(B, C, D, F, G);
    static final List<Segment> _Z = Arrays.asList(A, B, D, E, G);
    static final List<Segment> _0 = Arrays.asList(A, B, C, D, E, F);
    static final List<Segment> _1 = Arrays.asList(B, C);
    static final List<Segment> _2 = Arrays.asList(A, B, D, E, G);
    static final List<Segment> _3 = Arrays.asList(B, C, D, A, G);
    static final List<Segment> _4 = Arrays.asList(F, G, B, C);
    static final List<Segment> _5 = Arrays.asList(A, C, D, F, G);
    static final List<Segment> _6 = Arrays.asList(A, C, D, E, F, G);
    static final List<Segment> _7 = Arrays.asList(B, C, A);
    static final List<Segment> _8 = Arrays.asList(A, B, C, D, E, F, G);
    static final List<Segment> _9 = Arrays.asList(A, B, C, D, F, G);
    static final List<Segment> _hyphen = Arrays.asList(G);
    static final List<Segment> _dot = Arrays.asList(DP);
    static final List<Segment> _degree = Arrays.asList(A, B, G, F);
    static final List<Segment> _underscore = Arrays.asList(D);


    private static final HashMap<String, List<Segment>> lettersTranslate = new HashMap<>();

    public static HashMap<String, List<Segment>> getMap() {
        if (lettersTranslate.isEmpty()) {
            initMap();
        }
        return lettersTranslate;
    }

    private static void initMap() {
        lettersTranslate.put("A", _A);
        lettersTranslate.put("B", _B);
        lettersTranslate.put("b", _b);
        lettersTranslate.put("C", _C);
        lettersTranslate.put("c", _c);
        lettersTranslate.put("d", _d);
        lettersTranslate.put("E", _E);
        lettersTranslate.put("e", _e);
        lettersTranslate.put("F", _F);
        lettersTranslate.put("f", _f);
        lettersTranslate.put("G", _G);
        lettersTranslate.put("g", _g);
        lettersTranslate.put("H", _H);
        lettersTranslate.put("h", _h);
        lettersTranslate.put("I", _I);
        lettersTranslate.put("i", _i);
        lettersTranslate.put("J", _J);
        lettersTranslate.put("j", _j);
        lettersTranslate.put("L", _L);
        lettersTranslate.put("l", _l);
        lettersTranslate.put("N", _N);
        lettersTranslate.put("n", _n);
        lettersTranslate.put("O", _O);
        lettersTranslate.put("o", _o);
        lettersTranslate.put("P", _P);
        lettersTranslate.put("q", _q);
        lettersTranslate.put("r", _r);
        lettersTranslate.put("S", _S);
        lettersTranslate.put("t", _t);
        lettersTranslate.put("U", _U);
        lettersTranslate.put("u", _u);
        lettersTranslate.put("y", _y);
        lettersTranslate.put("Z", _Z);
        lettersTranslate.put("0", _0);
        lettersTranslate.put("1", _1);
        lettersTranslate.put("2", _2);
        lettersTranslate.put("3", _3);
        lettersTranslate.put("4", _4);
        lettersTranslate.put("5", _5);
        lettersTranslate.put("6", _6);
        lettersTranslate.put("7", _7);
        lettersTranslate.put("8", _8);
        lettersTranslate.put("9", _9);
        lettersTranslate.put("-", _hyphen);
        lettersTranslate.put("°", _degree);
        lettersTranslate.put("_", _underscore);

        Stream<Pair<String, ArrayList<Segment>>> pairStream = lettersTranslate.keySet().stream().map(k ->
                {
                    List<Segment> list = lettersTranslate.get(k);
                    ArrayList<Segment> l = new ArrayList<>(list);
                    l.add(DP);
                    return Pair.of(k + ".", l);
                }
        );
        Map<String, List<Segment>> dottedLetters = pairStream
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        lettersTranslate.putAll(dottedLetters);

        lettersTranslate.put(".", _dot);

    }


}
