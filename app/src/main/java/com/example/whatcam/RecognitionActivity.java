package com.example.whatcam;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.DetectedObject.Label;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import com.google.mlkit.vision.objects.defaults.PredefinedCategory;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.ByteBuffer;
import java.util.List;


public class RecognitionActivity extends AppCompatActivity {
    /**
     * how to convert byte[] image into Bitmap;
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        ImageView imageView;
        imageView = findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeByteArray(CameraActivity.outputImage, 0, CameraActivity.outputImage.length);
        imageView.setImageBitmap(bitmap);
        InputImage image = InputImage.fromBitmap(bitmap, 90);

        LocalModel localModel =
                new LocalModel.Builder()
                        .setAssetFilePath("detect.tflite")
                        // or .setAbsoluteFilePath(absolute file path to model file)
                        // or .setUri(URI to model file)
                        .build();

        CustomObjectDetectorOptions options =
                new CustomObjectDetectorOptions.Builder(localModel)
                        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                        .enableMultipleObjects()
                        .enableClassification()
                        .setClassificationConfidenceThreshold(0.5f)
                        .setMaxPerObjectLabelCount(3)
                        .build();
        ObjectDetector objectDetector = ObjectDetection.getClient(options);

        objectDetector.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<DetectedObject>>() {
                    @Override
                    public void onSuccess(List<DetectedObject> results) {
                        // Task completed successfully

                        Log.d("TAG", "onSuccess: size : " + results.size());
                        for (DetectedObject detectedObject : results) {
                            Log.d("TAG", "onSuccess: detectedObject : " + detectedObject.getTrackingId());
                            Rect boundingBox = detectedObject.getBoundingBox();
                            Integer trackingId = detectedObject.getTrackingId();
                            for (Label label : detectedObject.getLabels()) {
                                String text = label.getText();
                                Log.d("TAG", "onSuccess: " + text);
                                if (PredefinedCategory.FOOD.equals(text)) {

                                }
                                int index = label.getIndex();
                                if (PredefinedCategory.FOOD_INDEX == index) {

                                }
                                float confidence = label.getConfidence();
                            }
                        }
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "onSuccess: FAILED : ", e);
                            }
                        });
    }

}