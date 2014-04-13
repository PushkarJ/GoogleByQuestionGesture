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
import java.util.Date;
import java.util.HashMap;

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
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gestures.generated.R;
import com.gestures.utils.Constants;

public class GestureTest extends Activity implements OnGesturePerformedListener {

	private GestureLibrary gestureLib;
	TextView textView;
	String selectedText;
	HashMap<String, Double> results;
	String searchMethod;
	int iteration = 0;
	final int[] targetStart = { 146, 560, 613, 90, 604, 542, 217, 359, 236, 195 };
	final int[] targetEnd = { 149, 568, 619, 97, 610, 544, 222, 368, 241, 204 };
	private Date endTaskTime;
	private Date startTaskTime;
	ArrayList<ArrayList<float[]>> gestureRecord = new ArrayList<ArrayList<float[]>>();

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		searchMethod = intent.getExtras().getString(Constants.SEARCH_METHOD);
		iteration = intent.getExtras().getInt("iteration");
		results = intent.getExtras().get(Constants.RESULTS) == null ? new HashMap<String, Double>(
				11) : (HashMap<String, Double>) intent.getExtras().get(
				Constants.RESULTS);

		GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
		View inflate = getLayoutInflater().inflate(R.layout.main, null);
		gestureOverlayView.addView(inflate);
		gestureOverlayView.setGestureColor(Color.TRANSPARENT);
		gestureOverlayView.addOnGesturePerformedListener(this);
		if (searchMethod.equalsIgnoreCase(getResources().getString(
				R.string.question_mark))) {
			gestureLib = GestureLibraries.fromRawResource(this,
					R.raw.question_gestures);
		}
		if (searchMethod.equalsIgnoreCase(getResources().getString(
				R.string.swirl_anticlockwise))
				|| searchMethod.equalsIgnoreCase(getResources().getString(
						R.string.swirl_clockwise))) {
			gestureLib = GestureLibraries.fromRawResource(this,
					R.raw.swirl_gestures);
		}
		if (gestureLib!=null && !gestureLib.load()) {
			finish();
		}
		setContentView(gestureOverlayView);
		textView = (TextView) findViewById(R.id.hellotext);
		Button advance = (Button) findViewById(R.id.advance);
		advance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				Intent next = getIntent();
				next.putExtra("iteration", iteration + 1);
				next.putExtra(Constants.RESULTS, results);
				startActivity(next);
			}
		});
		/* Send the hashtable to ShowResults Activity */
		Button showResults = (Button) findViewById(R.id.show_results);
		showResults.setVisibility(Button.INVISIBLE);
		showResults.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GestureTest.this, ShowResults.class);
				intent.putExtra(Constants.SEARCH_METHOD, searchMethod);
				intent.putExtra(Constants.RESULTS, results);
				startActivity(intent);
			}
		});
		if (iteration == 9) {
			advance.setEnabled(false);
			showResults.setVisibility(Button.VISIBLE);
		}
		/* Bold the target text */
		final SpannableStringBuilder sb = new SpannableStringBuilder(textView
				.getText().toString());
		final StyleSpan bdit = new StyleSpan(
				android.graphics.Typeface.BOLD_ITALIC);
		sb.setSpan(bdit, targetStart[iteration], targetEnd[iteration],
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		textView.setText(sb);

		textView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO: rename date variable
				startTaskTime = new Date();
				Log.i("customgestures",
						"Start Timer:" + startTaskTime.toString());
				Log.i("customgestures", "Start:" + textView.getSelectionStart()
						+ " End: " + textView.getSelectionEnd());
				selectedText = textView
						.getText()
						.subSequence(textView.getSelectionStart(),
								textView.getSelectionEnd()).toString();
				return false;

			}
		});
		ImageButton button = (ImageButton) findViewById(R.id.search_simulate);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText searchTerm=(EditText)findViewById(R.id.editText1);				
				Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
				String keyword = searchTerm.getText().toString();
				intent.putExtra(SearchManager.QUERY, keyword);
				startActivity(intent);
			}
		});
	}
	
	/** Helper method to save the path of the gesture as an ArrayList of points. 
	 *  
	 *  Samples from path are not time-based. */
	private void saveGesturePath(Gesture gesture) {
		final int samples = 16; // Number of samples from path
		ArrayList<float[]> points = new ArrayList<float[]>();
		PathMeasure measure = new PathMeasure(gesture.toPath(),false);
		float[] coordinateContainer = {0f, 0f};
		
		final float step = measure.getLength()/samples;
		for (int iSample = 0; iSample < samples; iSample++) {
			measure.getPosTan(step * iSample, coordinateContainer, null);
			points.add(coordinateContainer.clone());
		}
		
		gestureRecord.add(points);
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		int index = 0;
		Prediction prediction;
		// Go through all the gestures in the library till you find the one
		// which has a higher prediction score
		do {
			prediction = predictions.get(index++);
		} while (prediction.score < 1.0 && index < predictions.size());

		if (index < 4) {
			// Log.i("customgestures","Stop Timer:"+new Date().toString());
			Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
			String keyword = selectedText;
			intent.putExtra(SearchManager.QUERY, keyword);
			startActivity(intent);
			// TODO:rename date variable
			endTaskTime = new Date();
			double timetoCompleteTask = ((double) (endTaskTime.getTime() - startTaskTime
					.getTime()));
			Log.i("customgestures",
					"Time to complete the task: "
							+ String.valueOf(timetoCompleteTask));
			results.put(String.valueOf(iteration),
					Double.valueOf((timetoCompleteTask / 1000)));
			saveGesturePath(gesture);
			// Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
			// .show();
		} else {
			Toast.makeText(
					this,
					"Unrecognized gesture detected. Current search method is: "
							+ searchMethod, Toast.LENGTH_LONG).show();
		}
	}
}