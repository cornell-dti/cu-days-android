package org.cornelldti.cudays;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import org.cornelldti.cudays.models.Category;
import org.cornelldti.cudays.models.Event;
import org.cornelldti.cudays.util.Callback;
import org.cornelldti.cudays.util.Internet;
import org.cornelldti.cudays.util.NotificationCenter;
import org.cornelldti.cudays.util.Settings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles all data shared between classes. Many of these variables have associated {@link NotificationCenter}
 * events that should be fired when they are changed, so do so when changing their values.
 *
 * {@link #allEvents}: All events on disk, sorted by date, indexed by pk.
 * {@link #selectedEvents}: All events selected by the user, sorted by date, indexed by pk.
 * {@link #collegeCategories}: All categories representing colleges, indexed by pk.
 * {@link #typeCategories}: All categories representing types, indexed by pk.
 * {@link #DATES}: Dates of the orientation. Determined from {@link #YEAR}, {@link #MONTH}, {@link #START_DAY},
 *                 and {@link #END_DAY}.
 * {@link #selectedDate}: The date to display events for.
 * {@link #collegeFilter}: Events belonging to this college will show in feed.
 * {@link #typeFilter}: Events belonging to this type will show in feed.
 */
public final class UserData
{
	public static final Map<LocalDate, Map<Integer, Event>> allEvents;
	public static final Map<LocalDate, Map<Integer, Event>> selectedEvents;
	public static final Map<Integer, Category> collegeCategories = new HashMap<>();
	public static final Map<Integer, Category> typeCategories = new HashMap<>();
	public static final List<LocalDate> DATES;
	public static LocalDate selectedDate;
	@Nullable
	public static Category collegeFilter;
	@Nullable
	public static Category typeFilter;
	private static final int YEAR = 2018;
	private static final int MONTH = 4;
	private static final int START_DAY = 12;    //Dates range: [START_DAY, END_DAY], inclusive
	private static final int END_DAY = 23;      //Note: END_DAY must > START_DAY
	private static final Set<Integer> EXCLUDED_DAYS = new ImmutableSet.Builder<Integer>()
			.add(14, 15, 17, 18, 21, 22).build();           //days in range but not included
	private static final String TAG = UserData.class.getSimpleName();

	/**
	 * Initialize {@link #DATES} and lists for maps of events
	 */
	static
	{
		ImmutableList.Builder<LocalDate> tempDates = ImmutableList.builder();
		ImmutableMap.Builder<LocalDate, Map<Integer, Event>> tempAllEvents = ImmutableMap.builder();
		ImmutableMap.Builder<LocalDate, Map<Integer, Event>> tempSelectedEvents = ImmutableMap.builder();
		LocalDate today = LocalDate.now();
		for (int i = START_DAY; i <= END_DAY; i++)
		{
			if (EXCLUDED_DAYS.contains(i))
				continue;
			LocalDate date = new LocalDate(YEAR, MONTH, i);
			if (date.isEqual(today))
				selectedDate = date;
			tempDates.add(date);
			tempAllEvents.put(date, new HashMap<Integer, Event>());
			tempSelectedEvents.put(date, new HashMap<Integer, Event>());
		}
		DATES = tempDates.build();
		allEvents = tempAllEvents.build();
		selectedEvents = tempSelectedEvents.build();

		if (selectedDate == null)
			selectedDate = DATES.get(0);
	}

	//suppress instantiation
	private UserData(){}

	/**
	 * Returns true if the event is selected.
	 * @param event The event that we want to check is selected.
	 * @return See method description.
	 */
	public static boolean selectedEventsContains(Event event)
	{
		Map<Integer, Event> eventsForDate = selectedEvents.get(event.date);
		return eventsForDate != null && eventsForDate.containsKey(event.pk);
	}
	/**
	 * Adds event to {@link #allEvents} for the correct date according to {@link Event#date}.
	 * The date should match a date in {@link #DATES}.
	 * @param event Event to add.
	 */
	public static void appendToAllEvents(Event event)
	{
		Map<Integer, Event> eventsForDate = allEvents.get(event.date);
		if (eventsForDate == null)
			return;
		eventsForDate.put(event.pk, event);
	}
	/**
	 * Removes event form {@link #allEvents}.
	 * @param event Event to remove.
	 */
	public static void removeFromAllEvents(Event event)
	{
		Map<Integer, Event> eventsForDate = allEvents.get(event.date);
		if (eventsForDate != null)
			eventsForDate.remove(event.pk);
	}
	/**
	 * Adds event to {@link #selectedEvents}. The date should match a date in {@link #DATES}.
	 * @param event Event to add.
	 */
	public static void insertToSelectedEvents(Event event)
	{
		Map<Integer, Event> eventsForDate = selectedEvents.get(event.date);
		if (eventsForDate == null)
		{
			Log.e(TAG, "insertToSelectedEvents: attempted to add event with date outside orientation");
			return;
		}
		eventsForDate.put(event.pk, event);
	}
	/**
	 * Removes event from {@link #selectedEvents}.
	 * @param event Event to remove.
	 */
	public static void removeFromSelectedEvents(Event event)
	{
		Map<Integer, Event> eventsForDate = selectedEvents.get(event.date);
		if (eventsForDate == null)
			Log.e(TAG, "removeFromSelectedEvents: No selected events for date");
		else
			eventsForDate.remove(event.pk);
	}
	/**
	 * Clears all selected events.
	 */
	private static void clearSelectedEvents()
	{
		for (Map<Integer, Event> eventsOfDay : selectedEvents.values())
			eventsOfDay.clear();
	}
	/**
	 * Search for an event given its pk value.
	 * @param pk {@link Event#pk}
	 * @return Event. May be null.
	 */
	@Nullable
	public static Event eventForPk(int pk)
	{
		for (Map<Integer, Event> eventsOfDay : allEvents.values())
			if (eventsOfDay.containsKey(pk))
				return eventsOfDay.get(pk);
		Log.e(TAG, "eventForPk: Event not found for given pk");
		return null;
	}
	/**
	 * Loads {@link #allEvents}, {@link #selectedEvents}, {@link #collegeCategories}.
	 * 1. Retrieves all events and collegeCategories from disk, adding them to {@link #allEvents}, {@link #collegeCategories}.
	 * 2. Downloads updates from the database.
	 * 3. Sorts all events and collegeCategories. Retrieves selected events. If anything WAS updated from the
	 *    database, save the updates. Note that after events and collegeCategories are saved here, the lists
	 *    they belong in should not be mutated any further.
	 *
	 * @param context
	 */
	public static void loadData(final Context context)
	{
		final Set<Event> events = Settings.getAllEvents(context);
		for (Event event : events)
			appendToAllEvents(event);

		Set<Event> selectedEvents = Settings.getSelectedEvents(context);
		for (Event event : selectedEvents)
			insertToSelectedEvents(event);

		Set<Category> categories = Settings.getCategories(context);
		for (Category category : categories)
		{
			if (category.isCollege)
				UserData.collegeCategories.put(category.pk, category);
			else
				UserData.typeCategories.put(category.pk, category);
		}

		Internet.getUpdatesForVersion(Settings.getVersion(context), context, new Callback<Integer>()
		{
			//versionNum is 0 if failed
			@Override
			public void execute(Integer versionNum)
			{
				//save all the new data if available
				if (versionNum == 0)
					return;

				//re-add selected events, since events might have been updated
				clearSelectedEvents();
				Set<Event> selectedEvents = Settings.getSelectedEvents(context);
				for (Event selectedEvent : selectedEvents)
					insertToSelectedEvents(selectedEvent);

				Settings.setAllEvents(context);
				Settings.setCategories(context);
				Settings.setVersion(versionNum, context);
			}
		});
	}
	/**
	 * Returns the events for a given day, sorted.
	 */
	public static List<Event> sortedEventsForDate(LocalDate date)
	{
		Map<Integer, Event> eventsForDate = allEvents.get(date);
		if (eventsForDate == null)
			return null;
		List<Event> events = new ArrayList<>(eventsForDate.values());
		Collections.sort(events);
		return events;
	}
}
