/* Copyright (C) 2012 The Android Open Source Project

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.gestures;

import java.util.ArrayList;

import com.gestures.generated.R;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class GestureTest extends Activity implements OnGesturePerformedListener {
  private GestureLibrary gestureLib;

  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
    View inflate = getLayoutInflater().inflate(R.layout.main, null);
    gestureOverlayView.addView(inflate);
    gestureOverlayView.addOnGesturePerformedListener(this);
    gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
    if (!gestureLib.load()) {
      finish();
    }
    setContentView(gestureOverlayView);
  }

  @Override
  public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
    ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
    for (Prediction prediction : predictions) {
      if (prediction.score > 1.0) {
        Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
            .show();
      }
    }
  }
} 