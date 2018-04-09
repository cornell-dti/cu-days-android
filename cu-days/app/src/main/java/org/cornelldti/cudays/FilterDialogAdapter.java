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

/**
 * Manages the list of categories shown in {@link FilterDialog}.
 *
 * {@link #HEADER_COLLEGE}, {@link #CATEGORY_COLLEGE}, {@link #HEADER_TYPE}, {@link #CATEGORY_TYPE}:
 * Possible types of items in the list; either subheaders or categories.
 *
 * {@link #collegeCategories}, {@link #typeCategories}: Sorted versions of {@link UserData#collegeCategories}
 * and {@link UserData#typeCategories}'s values, to be displayed for the user to select in the list.
 */
public class FilterDialogAdapter extends ArrayAdapter<String>
{
	public static final int HEADER_COLLEGE = 0;
	public static final int CATEGORY_COLLEGE = 1;
	public static final int HEADER_TYPE = 2;
	public static final int CATEGORY_TYPE = 3;
	public final List<Category> collegeCategories;
	public final List<Category> typeCategories;
	private static final String TAG = FilterDialogAdapter.class.getSimpleName();

	/**
	 * Create the adapter and give the parent an arbitrary layout.
	 * Sort categories.
	 * @param context
	 */
	public FilterDialogAdapter(Context context)
	{
		super(context, R.layout.cell_subheader);
		collegeCategories = new ArrayList<>(UserData.collegeCategories.values());
		Collections.sort(collegeCategories);
		typeCategories = new ArrayList<>(UserData.typeCategories.values());
		Collections.sort(typeCategories);
	}

	/**
	 * Called for each item in the list on initialization and each time a change is detected.
	 *
	 * @param position Index of list item.
	 * @param convertView Item view. Can be null initially.
	 * @param parent Parent of item view?
	 * @return Item view.
	 */
	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
	{
		String content = getItem(position); //string to display
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

		//if this is a category, check/uncheck it
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

	/**
	 * Return the name of the item in this position. Note that the calculation of index from position
	 * is done taking into consideration the order or categories and the 2 headers.
	 *
	 * @param position Index of item in list.
	 * @return String to be displayed.
	 */
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

	/**
	 * Returns whether the item in this position is a header or a category, and what kind.
	 *
	 * @param position Index in list.
	 * @return Int representing the type of the item.
	 */
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

	/**
	 * @return The number of different types of list items.
	 */
	@Override
	public int getViewTypeCount()
	{
		return 4;
	}

	/**
	 * @return The size of the list.
	 */
	@Override
	public int getCount()
	{
		return collegeCategories.size() + typeCategories.size() + 2;
	}
}
