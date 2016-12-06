/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zillow
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.cameraparamter.params;

import static android.hardware.Camera.Parameters.FOCUS_MODE_AUTO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
import static android.hardware.Camera.Parameters.FOCUS_MODE_EDOF;
import static android.hardware.Camera.Parameters.FOCUS_MODE_FIXED;
import static android.hardware.Camera.Parameters.FOCUS_MODE_INFINITY;
import static android.hardware.Camera.Parameters.FOCUS_MODE_MACRO;

public enum FocusMode {

    AUTO(
            0, FOCUS_MODE_AUTO),
    INFINITY(
            1, FOCUS_MODE_INFINITY),
    MACRO(
            2, FOCUS_MODE_MACRO),
    FIXED(
            3, FOCUS_MODE_FIXED),
    EDOF(
            4, FOCUS_MODE_EDOF),
    CONTINUOUS_VIDEO(
            5, FOCUS_MODE_CONTINUOUS_VIDEO);

    private int id;

    private String name;

    FocusMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public static FocusMode getFocusModeById(int id) {
        for (FocusMode mode : values()) {
            if (mode.id == id) {
                return mode;
            }
        }
        return null;
    }

    public static FocusMode getFocusModeByName(String name) {
        for (FocusMode mode : values()) {
            if (mode.name.compareTo(name) == 0) {
                return mode;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

}
