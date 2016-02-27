package com.eddiep.muse.utils;

import java.util.Random;

public class Global {
    public static final Random RANDOM = new Random();

    public static int rand(int min, int max) {
        return RANDOM.nextInt(max - min) + min;
    }
}
