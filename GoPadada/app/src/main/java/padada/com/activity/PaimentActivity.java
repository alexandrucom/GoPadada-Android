package padada.com.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import padada.com.R;

public class PaimentActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paiment);

		findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});

	}

}
