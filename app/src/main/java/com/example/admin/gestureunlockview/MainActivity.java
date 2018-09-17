package com.example.admin.gestureunlockview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GestureDrawView gestureDrawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestureDrawView = findViewById(R.id.gd_gesture_draw);
        final GestureResultView gestureResultView = findViewById(R.id.gr_gesture_result);


        gestureDrawView.setOnGestureDrawListener(new GestureDrawView.OnGestureDrawListener() {
            @Override
            public void gestureDraw(List<GestureCircle> checkedList) {
                for (GestureCircle circle : checkedList) {
                    PrintUtil.log("checkedList", circle.getPosition());
                }
                gestureResultView.setCheckedList(checkedList);
            }
        });

        gestureDrawView.setOnGestureErrorListener(new GestureDrawView.OnGestureErrorListener() {
            @Override
            public void gestureError() {
                gestureResultView.clearResult();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gestureDrawView.cancelHandler();
    }
}
