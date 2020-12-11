# 7segments-display
Allows controlling a 7 segments 1+ digits display.
Uses pi4j library to talk to hardware.

This library contains an enum for mapping the segments
and the pins. The names in the enum match the default positions in the 7segments display:

```
      _________
     /  __A__  \
    |  |     |  |
    |F |     | B|
    |  |_____|  |
        __G__   
    |  |     |  |
    |E |     | C|
    |  |_____|  |
     \____D____/  o <-- DP
```

There are two main use cases

##7 segments 1 digit display
You must connect the 8 pins of the led segments to the raspberry pins and the display ground to ground.
You initialize the display by calling its constructor.
In the "grounds" parameter you can pass any string, it is not used, in this case the pin should be connected directly to ground.

You can use the synchronous method "write" to write a character. You can put a trailing dot if needed.

##7 segments 2+digits display
You must connect the 8 pins of the led segments to the corresponding raspberry pins. 
You must connect the n grounds to n raspberry pins.
In this case the segments are shown alternately for a brief period of time.
A thread for every Display class is started to handle such loop.

##Usage
Let's make an example with a 4 digits display:

Constructor #1

    Display mainDisplay = new Display("GPIO 10","GPIO 11"..., /*grounds*/ "GPIO 14"..);

Constructor #2

    segmentStringHashMap.put(A, "GPIO 10");
    segmentStringHashMap.put(B, "GPIO 11");
    //...
    Display mainDisplay = new Display(segmentStringHashMap, Arrays.asList("GPIO 14", ...));

Usage

    mainDisplay.write("hello"); //h e l l
    mainDisplay.write("37.5c"); //3 7. 5 c
    mainDisplay.write("Um..k"); //U m. . k
Note that the first trailing dot is included in the previous letter.
If there are 2+ dots, only the first is included in the previous letter, the others will use up a digit.
    
Before closing the display, to stop the thread:

    mainDisplay.stop();
