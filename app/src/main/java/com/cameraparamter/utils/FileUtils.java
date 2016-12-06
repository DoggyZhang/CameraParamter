package com.cameraparamter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/11/9 0009.
 */

public class FileUtils {
    public static final int LOAD_ORDER_LAST_TO_NOW = 0x1;
    public static final int LOAD_ORDER_NOW_TO_LAST = 0x2;

    public static List<ImageView> loadImg(final Context context, final String directory, final String suffix, final int loadOrder) {
        final List<String> itemsDirectory = new ArrayList<>();
        final List<ImageView> items = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                File dir = new File(directory);
                if (dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    for (File file : files) {
                        if (file.getName().endsWith(suffix)) {
                            itemsDirectory.add(file.getAbsolutePath());
                        }
                    }
                }
                if (itemsDirectory.size() != 0) {
                    switch (loadOrder) {
                        case LOAD_ORDER_LAST_TO_NOW:
                            sortFrom_last_to_now(itemsDirectory);
                            break;
                        case LOAD_ORDER_NOW_TO_LAST:
                            sortFrom_now_to_last(itemsDirectory);
                            break;
                    }
                    for (String itemDirectory : itemsDirectory) {
                        Bitmap bitmap = BitmapFactory.decodeFile(itemDirectory);
                        ImageView item = new ImageView(context);
                        item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        item.setAdjustViewBounds(true);
                        item.setImageBitmap(bitmap);
                        items.add(item);
                    }
                }
            }
        }.run();
        return items;
    }

    public static List<ImageView> loadImg(final Context context, final String directory, final String suffix) {

        return loadImg(context, directory, suffix, LOAD_ORDER_NOW_TO_LAST);
    }

    /**
     * 搜索该文件夹下所指定的文件
     *
     * @param directory 需要搜索的文件夹
     * @param suffix    文件末尾匹配字符串
     * @return 所搜索到文件的文件路径
     */
    public static List<String> loadFile(final String directory, final String suffix) {
        return loadFile(directory, suffix, LOAD_ORDER_NOW_TO_LAST);
    }

    public static List<String> loadFile(final String directory, final String suffix, final int loadOrder) {
        final List<String> itemsDirectory = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                File dir = new File(directory);
                if (dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    for (File file : files) {
                        if (file.getName().endsWith(suffix)) {
                            itemsDirectory.add(0, file.getAbsolutePath());
                        }
                    }
                }
                switch (loadOrder) {
                    case LOAD_ORDER_LAST_TO_NOW:
                        sortFrom_last_to_now(itemsDirectory);
                        break;
                    case LOAD_ORDER_NOW_TO_LAST:
                        sortFrom_now_to_last(itemsDirectory);
                        break;
                }
            }
        }.run();
        return itemsDirectory;
    }


    public static void sortFrom_last_to_now(List<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
    }

    private static void sortFrom_now_to_last(List<String> list) {
        sortFrom_last_to_now(list);
        Collections.reverse(list);
    }
}
