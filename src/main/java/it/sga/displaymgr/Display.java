package it.sga.displaymgr;

import com.pi4j.io.gpio.*;
import it.sga.displaymgr.utils.Letters;
import it.sga.displaymgr.utils.Segment;

import java.util.*;

import static it.sga.displaymgr.utils.Segment.*;

/**
 * This is the main class. You call the constructor with the map of segments -> pins
 */
public class Display {
    private static final long MILLIS_BETWEEN_REPAINTS = 2;

    private static final Object SYNC = new Object();

    private final Map<Segment, GpioPinDigitalOutput> segmentsMap = new HashMap<>();
    private final List<GpioPinDigitalOutput> grounds;
    private final int digits;
    private List<String> textList = new ArrayList<>();

    /**
     * @param a         GPIO for the segment A (es GPIO 10)
     * @param b         GPIO for the segment b
     * @param c         GPIO for the segment c
     * @param d         GPIO for the segment d
     * @param e         GPIO for the segment e
     * @param f         GPIO for the segment f
     * @param g         GPIO for the segment g
     * @param dp        GPIO for the dot.
     * @param inGrounds GPIOS for the grounds
     * @see Display
     */
    public Display(String a, String b, String c, String d, String e, String f, String g, String dp, String... inGrounds) {
        this(getSegmentStringHashMap(a, b, c, d, e, f, g, dp), Arrays.asList(inGrounds));
    }

    private static HashMap<Segment, String> getSegmentStringHashMap(String a, String b, String c, String d, String e, String f, String g, String dp) {
        HashMap<Segment, String> s = new HashMap<>();
        s.put(A, a);
        s.put(B, b);
        s.put(C, c);
        s.put(D, d);
        s.put(E, e);
        s.put(F, f);
        s.put(G, g);
        s.put(DP, dp);
        return s;
    }

    /**
     * Initializes a display. If it has multiple digits a thread is started.
     * A 7 segments 1 digit or n digits multiplexed display is set up by connecting
     * the 7 segments and the grounds to raspberry pins. If there are more than 1 digits
     * then the writing is multiplexed and this code switches quickly between digits to
     * turn them on. Only 1 digit is on at a time but the action is so fast it looks like
     * all n digits are on instead.
     *
     * @param pinout map segment -> pin name
     * @param ground list of the ground pins
     */
    public Display(Map<Segment, String> pinout, List<String> ground) {
        GpioController gpio = GpioFactory.getInstance();
        digits = ground.size();
        for (Segment r : Arrays.asList(A, B, C, D, E, F, G, DP)) {
            String pinName = pinout.get(r);
            Pin pinByName = RaspiPin.getPinByName(pinName);
            GpioPinDigitalOutput digitalOutput = gpio.provisionDigitalOutputPin(pinByName);
            segmentsMap.put(r, digitalOutput);
        }
        List<GpioPinDigitalOutput> list = new ArrayList<>();

        for (String s : ground) {
            Pin pinByName = RaspiPin.getPinByName(s);
            GpioPinDigitalOutput gpioPinDigitalOutput = gpio.provisionDigitalOutputPin(pinByName);
            list.add(gpioPinDigitalOutput);
        }
        grounds = list;

        if (digits > 1) {
            Runnable innerRunnable = () -> {
                try {
                    while (!stopped) {
                        synchronized (SYNC) {
                            for (int i = 0; i < digits && i < textList.size(); i++) {
                                turnOffAll();
                                grounds.get(i).low();
                                turnOnLetterPins(textList.get(i));
                                //noinspection BusyWait
                                Thread.sleep(MILLIS_BETWEEN_REPAINTS);
                                grounds.forEach(GpioPinDigitalOutput::high);

                            }
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            Thread innerThread = new Thread(innerRunnable);
            innerThread.start();
        }
    }


    /**
     * Turns on each segment for 1 second
     *
     * @throws InterruptedException if the thread sleep is interrupted
     */
    public void pinDebugging() throws InterruptedException {
        for (GpioPinDigitalOutput d : grounds) {
            d.low();
        }
        for (GpioPinDigitalOutput c : segmentsMap.values()) {
            c.high();
            Thread.sleep(1000);
            c.low();
        }
    }

    public void write(String s) {

        synchronized (SYNC) {
            textList = calculateTextList(s);
        }
        if (digits == 1) {
            turnOffAll();
            turnOnLetterPins(textList.get(0)); //text length should be 1
        }
    }

    public static List<String> calculateTextList(String text) {
        String[] innerList = text.split("");
        String prev = "";
        ArrayList<String> innerTextList = new ArrayList<>();

        for (String l : innerList) {
            if (prev.equals("")) {
                prev = l;
            } else if (l.equals(".") && !prev.equals(".")) {
                innerTextList.add(prev + ".");
                prev = "";
            } else {
                innerTextList.add(prev);
                prev = l;
            }
        }
        if (!prev.equals("")) {
            innerTextList.add(prev);
        }
        return innerTextList;
    }

    private boolean stopped = false;

    public void stop() {
        synchronized (SYNC) {
            stopped = true;
        }
    }

    private void turnOffAll() {
        segmentsMap.values().forEach(GpioPinDigitalOutput::low);
    }

    private void turnOnLetterPins(String letter) {
        Letters.getMap().getOrDefault(letter, Collections.emptyList()).forEach(seg -> segmentsMap.get(seg).high());
    }
}
