package com.csci366.july2020.tasfique_enam.imagefilter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    //variable
    boolean imageLoad = false;
    ImageView selectedImage, processedImage;
    SeekBar seekBar;
    private int seekBarLevel = 0;
    Button loadButton;
    Button reset;
    Bitmap bitmapInput, bitmapOutput;
    TextView textView;

    int[][] matrixMinus3 = {
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1}
    };

    int[][] matrixMinus2 = {
            {1,1,1,1,1},
            {1,1,1,1,1},
            {1,1,1,1,1},
            {1,1,1,1,1},
            {1,1,1,1,1}
    };

    int[][] matrixMinus1 = {
            {1,1,1},
            {1,1,1},
            {1,1,1}
    };

    int[][] matrix = {
            {-1,-1,-1},
            {-1,9,-1},
            {-1,-1,-1}
    };

    int[][] matrix2 = {
            {-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1},
            {-1,-1,25,-1,-1},
            {-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1}
    };

    int[][] matrix3 = {
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,49,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedImage = findViewById(R.id.imageView_SelectedImage);
        processedImage = findViewById(R.id.imageView_ProcessedImage);
        seekBar = findViewById(R.id.seekBarBrightness);
        loadButton = findViewById(R.id.button_Select);
        reset = findViewById(R.id.button_reset);
        textView = findViewById(R.id.textView);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textView.setVisibility(View.GONE);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*"); //help us select any image.

                startActivityForResult(Intent.createChooser(intent, "Select from Gallery"), 111);
                textView.setText("Waiting for User Input.");
                textView.setVisibility(View.VISIBLE);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage.setImageBitmap(null);
                processedImage.setImageBitmap(null);

                reset.setVisibility(View.INVISIBLE);
                loadButton.setVisibility(View.VISIBLE);
                seekBar.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.GONE);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                seekBarLevel = progress - 3;

            }

            public void onStartTrackingTouch(SeekBar sb) {
                textView.setText("Processing, Takes some time, Please wait.");
            }

            // message for seekbar change
            public void onStopTrackingTouch(SeekBar sb) {
                    updateImage(seekBarLevel);
            }
        });
    }

    // loading image.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmapInput = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                selectedImage.setImageBitmap(bitmapInput);
                loadButton.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.VISIBLE);
                imageLoad = true;
                seekBar.setVisibility(View.VISIBLE);
                seekBarLevel = 0;
                seekBar.setProgress(3);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // image processing.
    private void updateImage(int level) {
        if (imageLoad) {
            //original image
            if(level == 0)
            {
                bitmapOutput = bitmapInput;
                processedImage.setImageBitmap(bitmapOutput);
                return;
            }
            switch(level) {
                // softening
                case -3:
                {

                    bitmapOutput = processImage(7, matrixMinus3, level);
                    processedImage.setImageBitmap(bitmapOutput);
                    textView.setText("Done...");
                    break;
                }


                case -2:
                {

                    bitmapOutput = processImage(5, matrixMinus2, level);
                    processedImage.setImageBitmap(bitmapOutput);
                    textView.setText("Done...");
                    break;
                }


                case -1:
                {

                    bitmapOutput = processImage(3, matrixMinus1, level);
                    processedImage.setImageBitmap(bitmapOutput);
                    textView.setText("Done...");
                    break;
                }

                // sharpening
                case 1:
                {

                    bitmapOutput = processImage(3, matrix, level);
                    processedImage.setImageBitmap(bitmapOutput);
                    textView.setText("Done...");
                    break;
                }


                case 2:
                {

                    bitmapOutput = processImage(5, matrix2, level);
                    processedImage.setImageBitmap(bitmapOutput);
                    textView.setText("Done...");
                    break;
                }

                case 3:
                {
                    bitmapOutput = processImage(7, matrix3, level);
                    processedImage.setImageBitmap(bitmapOutput);
                    textView.setText("Done...");
                    break;
                }
            }
        }
    }

    //algorithm to process the image.
    public Bitmap processImage(int size, int[][] matrix, int level) {

        int width = bitmapInput.getWidth();

        int height = bitmapInput.getHeight();

        bitmapOutput = Bitmap.createBitmap(width, height, bitmapInput.getConfig());

        int sumR, sumG, sumB;
        int A, R, G, B;
        A = R = G = B = 0;

        int[][] pixels = new int[size][size];

        for (int y = 1; y <= height - size; y++) {
            for (int x = 1; x <= width - size; x++) {

                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        pixels[i][j] = bitmapInput.getPixel(x + i, y + j);
                    }
                }

                //alpha
                if(level == 1 || level == -1) A = Color.alpha(pixels[1][1]);
                else if(level == 2 || level == -2) A = Color.alpha(pixels[2][2]);
                else if(level == 3 || level == -3) A = Color.alpha(pixels[3][3]);

                sumR = sumG = sumB = 0;

                for(int i = 0; i < size; i++) {
                    for(int j = 0; j < size; j++) {
                        sumR += (Color.red(pixels[i][j]) * matrix[i][j]);
                        sumG += (Color.green(pixels[i][j]) * matrix[i][j]);
                        sumB += (Color.blue(pixels[i][j]) * matrix[i][j]);
                    }
                }

                if (level >= 1 && level <= 3) {
                    R = (int)(sumR);
                    G = (int)(sumG);
                    B = (int)(sumB);
                }
                // For softening
                else if (level >= -3 && level <= -1) {
                    R = (int)(sumR / (size*size));
                    G = (int)(sumG / (size*size));
                    B = (int)(sumB / (size*size));
                }

                // TO check rgb bounds
                if(R < 0) R = 0;
                else if(R > 255) R = 255;
                if(G < 0) G = 0;
                else if(G > 255) G = 255;
                if(B < 0) B = 0;
                else if(B > 255) B = 255;

                // apply new pixels
                bitmapOutput.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
            }
        }
        return bitmapOutput;

    }
}
