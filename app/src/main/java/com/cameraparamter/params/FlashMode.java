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

import android.hardware.Camera;

public enum FlashMode {
    ON(
            0, Camera.Parameters.FLASH_MODE_ON),
    AUTO(
            1, Camera.Parameters.FLASH_MODE_AUTO),
    OFF(
            2, Camera.Parameters.FLASH_MODE_OFF);

    private int id;

    private String name;

    FlashMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public static FlashMode getFlashModeById(int id) {
        for (FlashMode mode : values()) {
            if (mode.id == id) {
                return mode;
            }
        }
        return FlashMode.ON;
    }

    public static FlashMode getFlashModeByName(String name) {
        for (FlashMode mode : values()) {
            if (mode.name.compareTo(name) == 0) {
                return mode;
            }
        }
        return FlashMode.ON;
    }

    @Override
    public String toString() {
        return name;
    }

}
