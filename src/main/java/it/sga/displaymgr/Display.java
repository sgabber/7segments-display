package it.sga.displaymgr;

import com.pi4j.io.gpio.*;
import it.sga.displaymgr.utils.Letters;
import it.sga.displaymgr.utils.Segment;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static it.sga.displaymgr.utils.Segment.*;

public class Display {
    private static final long MILLIS_BETWEEN_REPAINTS = 20;

    private static final Object SYNC = new Object();

    private final Map<Segment, GpioPinDigitalOutput> segmentsMap = new HashMap<>();
    private final List<GpioPinDigitalOutput> grounds;
    private final int digits;
    private Thread innerThread;

    private String text = "";

    public Display(int digits, Map<Segment, String> pinout, List<String> ground) {
        GpioController gpio = GpioFactory.getInstance();
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
        this.digits = digits;
    }

    /**
     * Turns on each segment for 1 second
     *
     * @throws InterruptedException
     */
    public void pinDebugging() throws InterruptedException {
        for (GpioPinDigitalOutput c : segmentsMap.values()) {
            c.high();
            Thread.sleep(1000);
            c.low();
        }
    }

    public void write(String s) {

        text = StringUtils.rightPad(s, digits).substring(0, digits);
        if (digits == 1) {
            turnOffAll();
            turnOnLetterPins(text); //text length should be 1
        } else {
            if (innerThread == null) {
                innerThread = new Thread(this.innerRunnable);
            } else {
                stop();
            }
            innerThread.start();
        }
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


            String[] innerList = text.split("");
            String prev = "";
            List<String> newList = new ArrayList<>();

            for (String l : innerList) {
                if (prev.equals("")) {
                    prev = l;
                } else if (l.equals(".") && !prev.equals(".")) {
                    newList.add(prev + ".");
                    prev = "";
                } else {
                    newList.add(prev);
                    prev = l;
                }
            }
            if (!prev.equals("")) {
                newList.add(prev);
            }
            try {
                exec(newList);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void exec(List<String> newList) throws InterruptedException {
            while (!stopped) {
                for (int i = 0; i < digits; i++) {
                    turnOffAll();
                    grounds.forEach(GpioPinDigitalOutput::high);
                    grounds.get(i).low();
                    turnOffAll();
                    turnOnLetterPins(newList.get(i));
                    //noinspection BusyWait
                    Thread.sleep(MILLIS_BETWEEN_REPAINTS);
                }
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
