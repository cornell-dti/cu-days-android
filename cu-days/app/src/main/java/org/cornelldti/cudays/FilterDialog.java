package org.cornelldti.cudays;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.cornelldti.cudays.util.NotificationCenter;

public class FilterDialog extends DialogFragment implements AdapterView.OnItemClickListener
{
	private static final String TAG = FilterDialog.class.getSimpleName();
	private FilterDialogAdapter adapter;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.menu_filter);
		builder.setPositiveButton(R.string.dialog_positive_button, null);
		builder.setNegativeButton(R.string.dialog_clear_button, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				clearFilters();
				adapter.notifyDataSetChanged();
				NotificationCenter.DEFAULT.post(new NotificationCenter.EventFilterChanged());
			}
		});

		//create list
		View view = View.inflate(getContext(), android.R.layout.list_content, null);
		ListView list = view.findViewById(android.R.id.list);
		adapter = new FilterDialogAdapter(getContext());
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		builder.setView(view);

		return builder.create();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Log.i(TAG, "onItemClick: someone was selected");
		switch (adapter.getItemViewType(position))
		{
			case FilterDialogAdapter.CATEGORY_COLLEGE:
				UserData.collegeFilter = adapter.collegeCategories.get(position - 1);
				break;
			case FilterDialogAdapter.CATEGORY_TYPE:
				UserData.typeFilter = adapter.typeCategories.get(position - adapter.collegeCategories.size() - 2);
				break;
			default:
				return;
		}

		adapter.notifyDataSetChanged();
		NotificationCenter.DEFAULT.post(new NotificationCenter.EventFilterChanged());
	}

	/**
	 * Remove all filters.
	 */
	private void clearFilters()
	{
		UserData.collegeFilter = null;
		UserData.typeFilter = null;
	}
}
