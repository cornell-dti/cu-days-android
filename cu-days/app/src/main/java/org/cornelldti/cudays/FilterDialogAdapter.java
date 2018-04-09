package org.cornelldti.cudays;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import org.cornelldti.cudays.models.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterDialogAdapter extends ArrayAdapter<String>
{
	public static final int HEADER_COLLEGE = 0;
	public static final int CATEGORY_COLLEGE = 1;
	public static final int HEADER_TYPE = 2;
	public static final int CATEGORY_TYPE = 3;
	public final List<Category> collegeCategories;
	public final List<Category> typeCategories;
	private static final String TAG = FilterDialogAdapter.class.getSimpleName();

	public FilterDialogAdapter(Context context)
	{
		super(context, R.layout.cell_subheader);
		collegeCategories = new ArrayList<>(UserData.collegeCategories.values());
		Collections.sort(collegeCategories);
		typeCategories = new ArrayList<>(UserData.typeCategories.values());
		Collections.sort(typeCategories);
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
	{
		String content = getItem(position);
		int viewType = getItemViewType(position);
		if (convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(getContext());
			switch (viewType)
			{
				case HEADER_COLLEGE:
				case HEADER_TYPE:
					convertView = inflater.inflate(R.layout.cell_subheader, parent, false);
					break;
				case CATEGORY_COLLEGE:
				case CATEGORY_TYPE:
					convertView = inflater.inflate(R.layout.cell_radio, parent, false);
					break;
				default:
					Log.e(TAG, "getView: invalid view type " + viewType);
					return parent;
			}
		}

		TextView textView = convertView.findViewById(R.id.text);
		textView.setText(content);

		//check the correct textview
		if (viewType == CATEGORY_COLLEGE)
		{
			RadioButton radioButton = convertView.findViewById(R.id.radio);
			if (collegeCategories.get(position - 1).equals(UserData.collegeFilter))
				radioButton.setChecked(true);
			else
				radioButton.setChecked(false);
		}
		else if (viewType == CATEGORY_TYPE)
		{
			RadioButton radioButton = convertView.findViewById(R.id.radio);
			if (typeCategories.get(position - collegeCategories.size() - 2).equals(UserData.typeFilter))
				radioButton.setChecked(true);
			else
				radioButton.setChecked(false);
		}

		return convertView;
	}

	@Nullable
	@Override
	public String getItem(int position)
	{
		int viewType = getItemViewType(position);
		switch (viewType)
		{
			case HEADER_COLLEGE:
				return getContext().getString(R.string.filter_college);
			case CATEGORY_COLLEGE:
				return collegeCategories.get(position - 1).name;
			case HEADER_TYPE:
				return getContext().getString(R.string.filter_type);
			case CATEGORY_TYPE:
				return typeCategories.get(position - collegeCategories.size() - 2).name;
			default:
				Log.e(TAG, "getItem: invalid view type " + viewType);
				return "";
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		if (position == 0)
			return HEADER_COLLEGE;
		if (position > 0 && position <= collegeCategories.size())
			return CATEGORY_COLLEGE;
		if (position == collegeCategories.size() + 1)
			return HEADER_TYPE;
		return CATEGORY_TYPE;
	}

	@Override
	public int getViewTypeCount()
	{
		return 4;
	}

	@Override
	public int getCount()
	{
		return collegeCategories.size() + typeCategories.size() + 2;
	}
}
