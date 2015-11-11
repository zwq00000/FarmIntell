package com.lingya.farmintell.adapters;

import android.content.Context;
import android.content.res.Resources;

import com.lingya.farmintell.R;

/**
 * Created by zwq00000 on 2015/8/10.
 */
class Palettes {

    private final int[] colors;
    private int index = 0;

    private Palettes(Context context) {
        Resources r = context.getResources();
        colors = r.getIntArray(R.array.colorPalttes);
    }

    public static Palettes getInstance(Context context) {
        return new Palettes(context);
    }

    public int getNext() {
        index++;
        if (index >= colors.length) {
            index = 0;
        }
        return colors[index];
    }

    public int get(int index) {
        return colors[index % colors.length];
    }

}
