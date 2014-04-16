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
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
	String participant;
	int iteration = 0;
//	private final int numTests = 2; // For quick testing
	private final int numTests = 10;
	final int[] targetStart = { 146, 560, 613, 90, 604, 542, 217, 359, 236, 195 };
	final int[] targetEnd = { 149, 568, 619, 97, 610, 544, 222, 368, 241, 204 };
	private Date endTaskTime;
	private Date startTaskTime;
	ArrayList<ArrayList<float[]>> gestureRecord;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		searchMethod = intent.getExtras().getString(Constants.SEARCH_METHOD);
		participant = intent.getExtras().getString(Constants.PARTICIPANT);
		iteration = intent.getExtras().getInt("iteration");
		results = intent.getExtras().get(Constants.RESULTS) == null ? new HashMap<String, Double>(
				11) : (HashMap<String, Double>) intent.getExtras().get(
				Constants.RESULTS);
		gestureRecord = intent.getExtras().get(Constants.PATHS) == null ? new ArrayList<ArrayList<float[]>>(
				) : (ArrayList<ArrayList<float[]>>) intent.getExtras().get(
				Constants.PATHS);

		GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
		View inflate = getLayoutInflater().inflate(R.layout.main, null);
		gestureOverlayView.addView(inflate);
/*		gestureOverlayView.setGestureColor(Color.TRANSPARENT);
*/		gestureOverlayView.setGestureVisible(false);
		
	if(searchMethod.equalsIgnoreCase(getResources().getString(R.string.normal_search))== false)
	{
		gestureOverlayView.addOnGesturePerformedListener(this);

		if (searchMethod.equalsIgnoreCase(getResources().getString(
			R.string.question_mark)))
		{
			gestureLib = GestureLibraries.fromRawResource(this,
				R.raw.question_gestures);
		} 
		else if (searchMethod.equalsIgnoreCase(getResources().getString(
			R.string.swirl_anticlockwise)))
		{
			gestureLib = GestureLibraries.fromRawResource(this,
				R.raw.swirl_gesture_anticlockwise);
		}
		else if(searchMethod.equalsIgnoreCase(getResources().getString(
			R.string.swirl_clockwise)))
		{
			gestureLib = GestureLibraries.fromRawResource(this,
				R.raw.swirl_gesture_clockwise);
		}
		setContentView(gestureOverlayView);
		EditText searchBox = (EditText) findViewById(R.id.searchBox);
		searchBox.setEnabled(false);
		ImageButton button = (ImageButton) findViewById(R.id.search_simulate);
		button.setEnabled(false);
	}
	else 
	{
		setContentView(gestureOverlayView);
		EditText searchBox = (EditText) findViewById(R.id.searchBox);
		searchBox.setVisibility(View.VISIBLE);
		searchBox.setFocusableInTouchMode(true);
		ImageButton button = (ImageButton) findViewById(R.id.search_simulate);
		button.setVisibility(View.VISIBLE);
	}
		//setContentView(gestureOverlayView);
		if (gestureLib != null && !gestureLib.load()) {
			finish();
		}
		textView = (TextView) findViewById(R.id.hellotext);
		textView.requestFocus();

		/* Bold the target text */
		final SpannableStringBuilder sb = new SpannableStringBuilder(textView
				.getText().toString());
		final StyleSpan bdit = new StyleSpan(
				android.graphics.Typeface.BOLD_ITALIC);
		final ForegroundColorSpan fgc = new ForegroundColorSpan(
				Color.rgb(0, 0, 255));
		sb.setSpan(bdit, targetStart[iteration], targetEnd[iteration],
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		sb.setSpan(fgc, targetStart[iteration], targetEnd[iteration], 
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		textView.setText(sb);

		textView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO: rename date variable
				startTaskTime = new Date();
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
				advance();
			}
		});
	}
	
	/** Move to the next view. 
	 *  If the current view is the final test, move to the results page. */
	private void advance() {
		if (startTaskTime == null) {
			// If we never started, we'd best not finish
			Toast.makeText(this, "Please select the search text first", 
					Toast.LENGTH_SHORT).show();
			return;
		}
		// Record timing data
		endTaskTime = new Date();
		double timetoCompleteTask = ((double) (endTaskTime.getTime() - startTaskTime
				.getTime()));
		Log.i("customgestures",
				"Time to complete the task: "
						+ String.valueOf(timetoCompleteTask));
		results.put(String.valueOf(iteration),
				Double.valueOf((timetoCompleteTask / 1000)));
		
		// Go to next view
		finish();
		Intent next;
		if (iteration < (numTests-1)) {
			next = getIntent();
			next.putExtra("iteration", iteration + 1);
		} else {
			next = new Intent(GestureTest.this, ShowResults.class);
		}
		next.putExtra(Constants.SEARCH_METHOD, searchMethod);
		next.putExtra(Constants.PARTICIPANT, participant);
		next.putExtra(Constants.RESULTS, results);
		next.putExtra(Constants.PATHS, gestureRecord);
		startActivity(next);
	}
	
	/** Helper method to save the path of the gesture as an ArrayList of points. 
	 *  
	 *  Samples from path are not time-based. */
	private void saveGesturePath(Gesture gesture) {
		final int samples = 64; // Number of samples from path
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
		do
		{
			prediction = predictions.get(index++);
		} while (prediction.score < 1.0 && index < predictions.size());

		if (index < 4)
		{
			// Log.i("customgestures","Stop Timer:"+new Date().toString());
			saveGesturePath(gesture);
			advance();
			Toast.makeText(this, "Successful method " + searchMethod, 
					Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(
					this,
					"Unrecognized gesture detected. Current search method is: "
							+ searchMethod, Toast.LENGTH_LONG).show();
		}
	}
}