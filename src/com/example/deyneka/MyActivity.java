package com.example.deyneka;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MyActivity extends Activity implements View.OnClickListener {

    static final int WIDTH_2 = 405;
    static final int HEIGHT_2 = 434;
    ImageView view;
    int width;
    int height;
    int[] table;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new ImageView(this);
        view.setClickable(true);
        view.setOnClickListener(this);
        view.setScaleType(ImageView.ScaleType.CENTER);
        setContentView(view);

        //чтение
        Bitmap bitmapOriginal = BitmapFactory.decodeResource(getResources(), R.raw.source);
        width = bitmapOriginal.getWidth();
        height = bitmapOriginal.getHeight();
        table = new int[width * height];
        bitmapOriginal.getPixels(table, 0, width, 0, 0, width, height);
        for(int i = 0; i < table.length; i++) {
            int e = table[i];
            //достаём состовляющие всех цветов
            int alphaComp = Color.alpha(e);
            int redComp = Color.red(e);
            int greenComp = Color.green(e);
            int blueComp = Color.blue(e);
            //улучшаем яркость
            double kf = 1.8;
            blueComp = Math.min((int)(blueComp*kf), 255);
            redComp = Math.min((int)(redComp*kf), 255);
            greenComp = Math.min((int)(greenComp*kf), 255);
            table[i] = Color.argb(alphaComp, redComp, greenComp, blueComp);

        }
        //освобождаем ресурсы
        bitmapOriginal.recycle();
        //показываем картинку
        showImage(fastImageResizing(table, width, height), HEIGHT_2, WIDTH_2);
    }

    Bitmap bitmap;

    private void showImage(int[] colors, int width, int height) {
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
        view.setImageBitmap(bitmap);
    }

    private int[] fastImageResizing(int[] colors, int fromWidth, int fromHeight) {

        int[] tableTo = new int[HEIGHT_2*WIDTH_2];
        int[] reversedTable = new int[HEIGHT_2*WIDTH_2];
        for(int i = 0; i < WIDTH_2*HEIGHT_2; i++) {
            int y = i / WIDTH_2;
            int x = i % WIDTH_2;
            int fromX = x * fromWidth / WIDTH_2;
            int fromY = y * fromHeight / HEIGHT_2;
            tableTo[i] = colors[fromY*fromWidth + fromX];
        }

        for(int i = 0; i < HEIGHT_2*WIDTH_2; i++) {
            int x = i % HEIGHT_2;
            int y = i / HEIGHT_2;
            int k = (HEIGHT_2-x-1)*WIDTH_2 + WIDTH_2 - 1 - (WIDTH_2 - y-1);
            reversedTable[i] = tableTo[k];
        }
        return reversedTable;
    }
    private int[] goodImageResizing(int[] colors, int fromWidth, int fromHeight) {
        int[] tableTo = new int[HEIGHT_2*WIDTH_2];
        int[] reversedTable = new int[HEIGHT_2*WIDTH_2];
        for(int i = 0; i < WIDTH_2*HEIGHT_2; i++) {
            int y = i / WIDTH_2;
            int x = i % WIDTH_2;
            int fromX = x * fromWidth / WIDTH_2;
            int fromY = y * fromHeight / HEIGHT_2;
            int firstColor = colors[fromY*fromWidth + fromX];
            int rightColor = colors[fromY*fromWidth + fromX];
            int leftColor = colors[fromY*fromWidth + fromX];
            int upColor = colors[fromY*fromWidth + fromX];
            int downColor = colors[fromY*fromWidth + fromX];
            if(fromX + 1 < fromWidth)
                rightColor = colors[fromY*fromWidth + fromX+1];
            if(fromX - 1 >= 0)
                leftColor = colors[fromY*fromWidth + fromX-1];
            if(fromY + 1 < fromHeight)
                downColor = colors[(fromY+1)*fromWidth + fromX];
            if(fromY - 1 >= 0)
                upColor = colors[(fromY-1)*fromWidth + fromX];

            int multi = 2;
            int sum = 6;
            int r = Color.red(firstColor) * multi + Color.red(leftColor) + Color.red(rightColor) + Color.red(upColor) + Color.red(downColor);
            r = r/sum;
            int b = Color.blue(firstColor) * multi + Color.blue(leftColor) + Color.blue(rightColor) + Color.blue(upColor) + Color.blue(downColor);
            b = b/sum;
            int g = Color.green(firstColor) * multi + Color.green(leftColor) + Color.green(rightColor) + Color.green(upColor) + Color.green(downColor);
            g = g/sum;
            tableTo[i] = Color.argb(255, r, g, b);
        }
        for(int i = 0; i < HEIGHT_2*WIDTH_2; i++) {
            int x = i % HEIGHT_2;
            int y = i / HEIGHT_2;
            int k = (HEIGHT_2-x-1)*WIDTH_2 + WIDTH_2 - 1 - (WIDTH_2 - y-1);
            reversedTable[i] = tableTo[k];
        }

        return reversedTable;
    }


    @Override
    public void onClick(View v) {
        showImage(goodImageResizing(table, width, height), HEIGHT_2, WIDTH_2);
    }
}