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

public enum Ratio {
    R_4x3(
            0, 4, 3, "4:3"),
    R_16x9(
            1, 16, 9, "16:9");

    private int id;

    public int w;

    public int h;

    private String name;

    Ratio(int id, int w, int h, String name) {
        this.id = id;
        this.w = w;
        this.h = h;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public static Ratio getRatioById(int id) {
        for (Ratio ratio : values()) {
            if (ratio.id == id) {
                return ratio;
            }
        }
        return Ratio.R_16x9;
    }

    public static Ratio getRatioByName(String name) {
        for (Ratio ratio : values()) {
            if (ratio.toString().compareTo(name) == 0) {
                return ratio;
            }
        }
        return Ratio.R_16x9;
    }

    public static Ratio pickRatio(int width, int height) {
        for (Ratio ratio : values()) {
            float orignalRatio = ratio.w * 1.0f / ratio.h;
            float targetRatio = width * 1.0f / height;
            if (Math.abs(orignalRatio - targetRatio) < 0.1f) {
                return ratio;
            }
        }
        return Ratio.R_16x9;
    }

    @Override
    public String toString() {
        return name;
    }

}
