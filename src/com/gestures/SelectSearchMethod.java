package com.gestures;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.gestures.generated.R;
import com.gestures.practice.GesturePractice;
import com.gestures.utils.Constants;

public class SelectSearchMethod extends Activity {
	private RadioGroup searchMethodGroup;
	private RadioButton selectSearchMethodButton;
	private Button nextButton;
	Class test = GestureTest.class;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_search_method);
		addListenerOnButton();

	}

	public void addListenerOnButton() {

		searchMethodGroup = (RadioGroup) findViewById(R.id.search_group);
		nextButton = (Button) findViewById(R.id.next);
		ToggleButton tests=(ToggleButton)findViewById(R.id.practice_toggle);
		tests.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					test = GesturePractice.class;
				}
				else
				{
					test = GestureTest.class;
				}
			}
		});
		
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// get selected radio button from radioGroup
				int selectedId = searchMethodGroup.getCheckedRadioButtonId();

				// find the radiobutton by returned id
				selectSearchMethodButton = (RadioButton) findViewById(selectedId);
				if(selectSearchMethodButton != null)
				{
					Toast.makeText(SelectSearchMethod.this,
							selectSearchMethodButton.getText(), Toast.LENGTH_SHORT)
							.show();
					Intent intent = new Intent(SelectSearchMethod.this, test);
					intent.putExtra(Constants.SEARCH_METHOD, selectSearchMethodButton.getText());
					EditText participantIdentifierField = (EditText) findViewById(R.id.participant);
					intent.putExtra(Constants.PARTICIPANT, participantIdentifierField.getText().toString());
					startActivity(intent); 
				}
			}
		});
	}
}
