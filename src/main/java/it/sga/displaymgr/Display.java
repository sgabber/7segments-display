package it.sga.displaymgr;

import com.pi4j.io.gpio.*;
import it.sga.displaymgr.utils.Letters;
import it.sga.displaymgr.utils.Segment;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static it.sga.displaymgr.utils.Segment.*;

public class Display {
    private static final long MILLIS_BETWEEN_REPAINTS = 2;

    private static final Object SYNC = new Object();

    private final Map<Segment, GpioPinDigitalOutput> segmentsMap = new HashMap<>();
    private final List<GpioPinDigitalOutput> grounds;
    private final int digits;
    private List<String> textList = new ArrayList<>();

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
            Thread innerThread = new Thread(this.innerRunnable);
            innerThread.start();
        }
    }

    /**
     * Turns on each segment for 1 second
     *
     * @throws InterruptedException
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
            textList= calculateTextList(s, digits);
        }
        if (digits == 1) {
            turnOffAll();
            turnOnLetterPins(textList.get(0)); //text length should be 1
        }
    }

    public static List<String> calculateTextList(String text, int digits) {
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

    private final Runnable innerRunnable = new Runnable() {

        @Override
        public void run() {
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
        }

    };

    private void turnOffAll() {
        segmentsMap.values().forEach(GpioPinDigitalOutput::low);
    }

    private void turnOnLetterPins(String letter) {
        Letters.getMap().getOrDefault(letter, Collections.emptyList()).forEach(seg -> segmentsMap.get(seg).high());
    }
}
