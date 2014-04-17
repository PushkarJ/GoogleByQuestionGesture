package com.gestures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gestures.generated.R;
import com.gestures.utils.Constants;

public class ShowResults extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_results);
		Intent intent = getIntent();
		String participant = intent.getExtras().getString(
				Constants.PARTICIPANT);
		String searchMethod = intent.getExtras().getString(
				Constants.SEARCH_METHOD);
		@SuppressWarnings("unchecked")
		HashMap<String, Double> results =(HashMap<String,Double>) intent.getExtras().get(
				Constants.RESULTS);
		StringBuilder resultsCSV = new StringBuilder();
		resultsCSV.append(Constants.PARTICIPANT).append(",");
		resultsCSV.append(Constants.SEARCH_METHOD).append(",");
		if(results!=null)
		{
			Set<String> result_header=results.keySet();
			List<String> result_header_list = new ArrayList<String>(result_header);
			Collections.sort(result_header_list,new Comparator<String>() {
			    public int compare(String o1, String o2) {
			        Integer i1 = Integer.parseInt(o1);
			        Integer i2 = Integer.parseInt(o2);
			        return (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
			    }
			});
			for(String rh:result_header_list)
			{
				resultsCSV.append(rh).append(",");
			}
			resultsCSV.append("Total Time");
			resultsCSV.append(System.getProperty("line.separator"));
			resultsCSV.append(participant+",");
			resultsCSV.append(searchMethod+",");
			Collection<Double> result_values= results.values();
			double total=0;
			List<Double> result_values_list = new ArrayList<Double>(result_values);
			Collections.sort(result_values_list);
	
			for(Double rh:result_values_list)
			{
				resultsCSV.append(rh).append(",");
				total+=rh;
			}
			resultsCSV.append(total).append(System.getProperty("line.separator"));
	
			TextView resultsText = (TextView)findViewById(R.id.results);
			resultsText.setText(resultsCSV.toString());
			
			// Paths SVG
			ArrayList<ArrayList<float[]>> paths =(ArrayList<ArrayList<float[]>>) intent.getExtras().get(
					Constants.PATHS);
			if (paths != null) {
				String polylineElt = "";
				for (int i = 0; i < paths.size(); i++) {
					polylineElt += polylineEltFromPoints(paths.get(i),participant,searchMethod,i+1);
				}
				String allResults = "";
				allResults += resultsText.getText();
				allResults += System.getProperty("line.separator");
				allResults += System.getProperty("line.separator");
				allResults += polylineElt;
				resultsText.setText(allResults);
			}
		}
		
		// Yanks the CSV to the clipboard
		Button copycsv = (Button) findViewById(R.id.copycsv);
		copycsv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				TextView resultsText = (TextView)findViewById(R.id.results);
				ClipData clip = ClipData.newPlainText("CSV Data", resultsText.getText());
				clipboard.setPrimaryClip(clip);
			}
		});
		
		// Returns to the method selection page		
		Button restart= (Button)findViewById(R.id.restart);
		restart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent pintent = new Intent(ShowResults.this, SelectSearchMethod.class);
				startActivity(pintent);
			}
		});
	}

	private String polylineEltFromPoints(
				ArrayList<float[]> path,
				String participant,
				String method,
				int iteration) {
		String polyline = "<polyline points=\" ";
		for (float[] pt:path) {
			polyline += String.format("%.2f,%.2f ", pt[0], pt[1]);
		}
		polyline += "\" ";
		
		polyline += "class=\"";
		polyline += "participant-" + tidyString(participant) + " ";
		polyline += "method-" + tidyString(method) + " ";
		polyline += "iteration-" + iteration;
		polyline += "\" ";
				
		polyline += "/>";
		polyline += System.getProperty("line.separator");
		return polyline;
	}

	private String tidyString(String participant) {
		return participant.replaceAll("\\W+", "").toLowerCase();
	}
}
