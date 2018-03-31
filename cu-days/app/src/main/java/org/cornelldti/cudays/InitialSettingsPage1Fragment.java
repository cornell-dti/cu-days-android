package org.cornelldti.cudays;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.cornelldti.cudays.models.StudentType;


/**
 * The pager fragment for the student type initial settings.
 */

public class InitialSettingsPage1Fragment extends Fragment implements View.OnClickListener
{
	private Button freshman;
	private Button transfer;
	private StudentType studentType = StudentType.NOTSET;

	/**
	 * Sets up the view components of the student type settings.
	 * add action listener to each button.
	 *
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.initial_settings_student_type, container, false);
		freshman = rootView.findViewById(R.id.freshman);
		transfer = rootView.findViewById(R.id.transfer);

		//Sometimes view is reloaded after scrolling back from the third page. This ensures that the options the user had chosen remains visible.
		if (studentType == StudentType.FRESHMAN)
		{
			setSelected(freshman);
			disableSelected(transfer);
		}
		if (studentType == StudentType.TRANSFER)
		{
			setSelected(transfer);
			disableSelected(freshman);
		}

		freshman.setOnClickListener(this);
		transfer.setOnClickListener(this);

		return rootView;
	}

	/**
	 * Set a button's style as selected
	 *
	 * @param button
	 */
	private void setSelected(Button button)
	{
		button.setBackgroundResource(R.drawable.bg_button_selected_ripple);
		button.setTextColor(Color.WHITE);
	}

	/**
	 * set a button's style as not selected
	 *
	 * @param button
	 */
	private void disableSelected(Button button)
	{
		button.setBackgroundResource(R.drawable.bg_button_ripple);
		button.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
	}

	/**
	 * Handles button clicks.
	 *
	 * @param view {@link #freshman}, {@link #transfer}
	 */
	@Override
	public void onClick(View view)
	{
		Button buttonToSelect;
		Button buttonToDeselect;

		switch (view.getId())
		{
			case R.id.freshman:
				buttonToSelect = freshman;
				buttonToDeselect = transfer;
				studentType = StudentType.FRESHMAN;
				break;
			case R.id.transfer:
				buttonToSelect = transfer;
				buttonToDeselect = freshman;
				studentType = StudentType.TRANSFER;
				break;
			default:
				Log.e("InitialPage1Fragment", "onClick unexpected id: " + view);
				return;
		}

		setSelected(buttonToSelect);
		disableSelected(buttonToDeselect);

		InitialSettingsActivity parentActivity = (InitialSettingsActivity) getActivity();
		parentActivity.setStudentType(studentType);
		parentActivity.switchToNextPage();
	}
}
