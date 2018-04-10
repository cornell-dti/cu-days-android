package org.cornelldti.cudays;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Welcome activity with info about events.
 */
public class InitialActivity extends AppCompatActivity implements View.OnClickListener
{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initial_settings);

		Button startButton = findViewById(R.id.getStartedButton);
		startButton.setOnClickListener(this);
	}

	/**
	 * Close activity when user clicks {@link R.id#getStartedButton}.
	 * @param v
	 */
	@Override
	public void onClick(View v)
	{
		finish();
	}
}
