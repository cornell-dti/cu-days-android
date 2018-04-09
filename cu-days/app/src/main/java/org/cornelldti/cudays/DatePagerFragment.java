package org.cornelldti.cudays;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.cornelldti.cudays.models.Category;
import org.cornelldti.cudays.util.NotificationCenter;
import com.google.common.eventbus.Subscribe;

/**
 * Displays either {@link FeedFragment} or {@link ScheduleFragment} through {@link DatePagerAdapter}.
 * Allows swiping between dates.
 *
 * {@link #type}: Whether the feed or the schedule will be displayed.
 * {@link #datePager}: View that displays the multiple pages of feed/schedule.
 */
public class DatePagerFragment extends Fragment implements ViewPager.OnPageChangeListener
{
	private static final String TAG = DatePagerFragment.class.getSimpleName();
	private static final String TYPE_BUNDLE_KEY = "type";
	private DatePagerAdapter.Type type;
	private ViewPager datePager;
	private MenuItem filterMenu;

	/**
	 * Create an instance of {@link DatePagerFragment} on the given type.
	 * This should be the only way you create instances of {@link DatePagerFragment}.
	 *
	 * It passes the given type as a Bundle to {@link DatePagerFragment}.
	 *
	 * @param type Whether this fragment displays {@link FeedFragment} or {@link ScheduleFragment}.
	 * @return Instance of the date pager.
	 */
	public static DatePagerFragment newInstance(DatePagerAdapter.Type type)
	{
		DatePagerFragment datePagerFragment = new DatePagerFragment();

		Bundle args = new Bundle();
		args.putSerializable(TYPE_BUNDLE_KEY, type);
		datePagerFragment.setArguments(args);

		return datePagerFragment;
	}

	/**
	 * Set up {@link DatePagerAdapter} to display feed or schedule, depending on {@link #type}.
	 * {@link #type} will be retrieved from the bundle.
	 *
	 * @param inflater {@inheritDoc}
	 * @param container {@inheritDoc}
	 * @param savedInstanceState {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_pager, container, false);

		//retrieve type from bundle
		if (getArguments() != null)
			type = (DatePagerAdapter.Type) getArguments().getSerializable(TYPE_BUNDLE_KEY);
		else
			Log.e(TAG, "onCreateView: type not found");

		DatePagerAdapter adapter = new DatePagerAdapter(getChildFragmentManager(), type);
		datePager = view.findViewById(R.id.viewPager);
		datePager.setAdapter(adapter);
		datePager.addOnPageChangeListener(this);
		//trigger a call to onDateChanged so this fragment starts out with the correct date
		onDateChanged(null);

		if (type == DatePagerAdapter.Type.Feed)
			setHasOptionsMenu(true);

		NotificationCenter.DEFAULT.register(this);
		return view;
	}

	/**
	 * Unregister ourselves as a listener.
	 */
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		NotificationCenter.DEFAULT.unregister(this);
	}

	//do nothing for these events
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
	@Override
	public void onPageScrollStateChanged(int state) {}

	/**
	 * Send page change events when the user swipes to a new page.
	 * @param position The index of the new page.
	 */
	@Override
	public void onPageSelected(int position)
	{
		UserData.selectedDate = UserData.DATES.get(position);
		NotificationCenter.DEFAULT.post(new NotificationCenter.EventDateChanged());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.menu_of_feed, menu);
		filterMenu = menu.findItem(R.id.filterMenu);
		updateFilterIcon(null);
	}

	/**
	 * Listens for clicks and performs the following actions:
	 * filterMenu: Shows filter dialog.
	 *
	 * @param item {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.filterMenu:
				FilterDialog filterDialog = new FilterDialog();
				filterDialog.show(getFragmentManager(), "tag");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Change the filter icon based on whether any filters are activated.
	 */
	@Subscribe
	public void updateFilterIcon(NotificationCenter.EventFilterChanged eventFilterChanged)
	{
		if (UserData.collegeFilter == null && UserData.typeFilter == null)
			filterMenu.setIcon(R.drawable.ic_tune_white_24dp);
		else
			filterMenu.setIcon(R.drawable.ic_tune_inverted);
	}

	/**
	 * Flip the page to the location of the new date.
	 * @param event Ignored.
	 */
	@Subscribe
	public void onDateChanged(NotificationCenter.EventDateChanged event)
	{
		int position = UserData.DATES.indexOf(UserData.selectedDate);
		if (position == datePager.getCurrentItem())
			return;
		datePager.setCurrentItem(position, true);
	}
}
