package com.gestures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		String searchMethod = intent.getExtras().getString(
				Constants.SEARCH_METHOD);
		@SuppressWarnings("unchecked")
		HashMap<String, Double> results =(HashMap<String,Double>) intent.getExtras().get(
				Constants.RESULTS);
		StringBuilder resultsCSV = new StringBuilder();
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
		resultsCSV.append(searchMethod+",");
		//TODO:continue
		Collection result_values= results.values();
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
		}
	}
}
