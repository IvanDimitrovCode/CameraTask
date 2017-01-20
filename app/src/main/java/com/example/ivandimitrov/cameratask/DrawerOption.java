package com.example.ivandimitrov.cameratask;

import android.widget.TextView;

/**
 * Created by Ivan Dimitrov on 1/17/2017.
 */

public class DrawerOption {
    public static final int DRAWER_WITH_RADIO      = 100;
    public static final int DRAWER_WITH_CHECKBOX   = 101;
    public static final int DRAWER_WITH_SEEKBAR    = 102;
    public static final int DRAWER_WITH_SEEKBAR_EX = 103; // just for exposure percentage

    private int type;

    public DrawerOption(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
