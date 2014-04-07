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

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.gestures.generated.R;

public class GestureTest extends Activity implements OnGesturePerformedListener {
  private GestureLibrary gestureLib;
  TextView textView ;
  String selectedText;
  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
    View inflate = getLayoutInflater().inflate(R.layout.main, null);
    gestureOverlayView.addView(inflate);
    gestureOverlayView.setGestureColor(Color.TRANSPARENT);
    gestureOverlayView.addOnGesturePerformedListener(this);
    gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
    if (!gestureLib.load()) {
      finish();
    }
    setContentView(gestureOverlayView);
    textView = (TextView)findViewById(R.id.hellotext);
    textView.setOnTouchListener(new OnTouchListener()
	{		
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
		    Log.i("customgestures", "Start:"+textView.getSelectionStart()+ " End: "+textView.getSelectionEnd());
		    selectedText = textView.getText().subSequence(textView.getSelectionStart(), textView.getSelectionEnd()).toString();
				return false;

		}
	});
    
  }

  @Override
  public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
    ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
    for (Prediction prediction : predictions) {
      if (prediction.score > 1.0) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        String keyword=selectedText;
        intent.putExtra(SearchManager.QUERY, keyword);
        startActivity(intent);
        //Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
          //  .show();
      }
    }
  }
} 