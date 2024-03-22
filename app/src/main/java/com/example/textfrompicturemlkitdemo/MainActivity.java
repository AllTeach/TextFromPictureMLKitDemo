package com.example.textfrompicturemlkitdemo;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private ActivityResultLauncher<Void> mGetThumb;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        registerLauncher();

        // Assuming you have a bitmap of the image you want to process
       // Bitmap bitmap = null;
        //imageView.setImageBitmap(bitmap);

    //    processImage(bitmap);
    }

    private void processImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient();

        recognizer.process(image)
                .addOnSuccessListener(result -> displayResult(result))
                .addOnFailureListener(e -> Log.e("MainActivity", "Text recognition failed", e));
    }

    private void displayResult(Text result) {
        String resultText = result.getText();
        textView.setText(resultText);
    }

    public void TakePhoto(View view) {
        if( ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED)
            mGetThumb.launch(null);
        else {
            String[] permissions = {android.Manifest.permission.CAMERA};
            requestPermissionLauncher.launch(permissions);
        }


    }

    private void registerLauncher() {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if (result != null) {
                    boolean write = result.get(android.Manifest.permission.CAMERA);
                    if (write)
                        TakePhoto(null);
                    else
                        Toast.makeText(MainActivity.this, "No permissions", Toast.LENGTH_LONG).show();

                }


            }

        });

        mGetThumb = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                imageView.setImageBitmap(result);
                processImage(result);
            }
        });
    }

}