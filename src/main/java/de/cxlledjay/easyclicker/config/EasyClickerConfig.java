package de.cxlledjay.easyclicker.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class EasyClickerConfig extends MidnightConfig {

    // ----- right click config -----

    // info
    @Comment(category = "rmb", centered = true)
    public static Comment rmbHeading;

    @Entry(category = "rmb")
    public static boolean rmbEnabled = true;

    @Entry(category = "rmb")
    public static boolean rmbOnlyWhileHolding = false;



    @Comment(category = "rmb", centered = true)
    public static Comment rmbModesHeading;

    @Entry(category = "rmb")
    public static RightClickMode rmbMode = RightClickMode.HOLDING;

    public enum RightClickMode {
        HOLDING,
        INDIVIDUAL,
        INTERVAL
    }

    @Entry(category = "rmb", min = 1, max = 20, isSlider = true)
    public static int rmbIndividualCps = 5;

    @Entry(category = "rmb", min = 1)
    public static int rmbIntervalTime = 1;



    // ----- left click config -----
    // TBD
    @Comment(category = "lmb", centered = true)
    public static Comment lmbHeading;

    @Entry(category = "lmb")
    public static boolean lmbEnabled = true;

}
