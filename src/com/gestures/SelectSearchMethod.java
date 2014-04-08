package com.gestures;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gestures.generated.R;

public class SelectSearchMethod extends Activity {
	private RadioGroup searchMethodGroup;
	private RadioButton selectSearchMethodButton;
	private Button nextButton;

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

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// get selected radio button from radioGroup
				int selectedId = searchMethodGroup.getCheckedRadioButtonId();

				// find the radiobutton by returned id
				selectSearchMethodButton = (RadioButton) findViewById(selectedId);

				Toast.makeText(SelectSearchMethod.this,
						selectSearchMethodButton.getText(), Toast.LENGTH_SHORT)
						.show();
			}
		});
	}
}
