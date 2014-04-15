package com.gestures.practice;

import java.util.ArrayList;
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
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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

public class GesturePractice extends Activity implements OnGesturePerformedListener {
	
	private GestureLibrary gestureLib;
	TextView textView;
	String selectedText;
	HashMap<String, Double> results;
	String searchMethod;
	int iteration = 0;
//	private final int numTests = 2; // For quick testing
	final int[] targetStart = { 146, 560, 613, 90, 604, 542, 217, 359, 236, 195 };
	final int[] targetEnd = { 149, 568, 619, 97, 610, 544, 222, 368, 241, 204 };
	ArrayList<ArrayList<float[]>> gestureRecord;
	
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
			googleSearch();
		}
		else {
			Toast.makeText(
					this,
					"Unrecognized gesture detected. Current search method is: "
							+ searchMethod, Toast.LENGTH_LONG).show();
		}
	
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		searchMethod = intent.getExtras().getString(Constants.SEARCH_METHOD);
		GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
		View inflate = getLayoutInflater().inflate(R.layout.main, null);
		gestureOverlayView.addView(inflate);
		gestureOverlayView.setGestureVisible(true);
		if(searchMethod.equalsIgnoreCase(getResources().getString(R.string.normal_search))== false)
		{
			gestureOverlayView.addOnGesturePerformedListener(this);

			if (searchMethod.equalsIgnoreCase(getResources().getString(
					R.string.question_mark)))
			{
				gestureLib = GestureLibraries.fromRawResource(this,
						R.raw.question_gestures);
			} else if (searchMethod.equalsIgnoreCase(getResources().getString(
					R.string.swirl_anticlockwise)))
			{
				gestureLib = GestureLibraries.fromRawResource(this,
						R.raw.swirl_gesture_anticlockwise);
			} else if(searchMethod.equalsIgnoreCase(getResources().getString(
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
			ImageButton button = (ImageButton) findViewById(R.id.search_simulate);
			button.setVisibility(View.VISIBLE);			
			searchBox.setFocusableInTouchMode(true);
		}
		
		if (gestureLib != null && !gestureLib.load())
		{
			finish();
		}
		textView = (TextView) findViewById(R.id.hellotext);
		textView.requestFocus();
		
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
//				startTaskTime = new Date();
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
				EditText searchTerm =(EditText)findViewById(R.id.searchBox);
				selectedText=searchTerm.getText().toString();
				googleSearch();
			}
		});

	}
	void googleSearch()
	{
		Intent intent = new Intent(Intent.ACTION_WEB_SEARCH); 
		String keyword= selectedText;    
		intent.putExtra(SearchManager.QUERY, keyword);    
		startActivity(intent);
	}
}
