package org.cornelldti.cudays.models;

import android.support.annotation.NonNull;

import org.cornelldti.cudays.ScheduleFragment;
import org.cornelldti.cudays.util.Internet;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

/**
 * Data-type that holds all information about an event. Designed to be immutable. This will be downloaded
 * from the database via methods in {@link Internet}, where new events will be compared with saved ones.
 * Notable fields are explained below:
 *
 * {@link #collegeCategory}: The {@link Category#pk} of the {@link Category} this object belongs to.
 * {@link #typeCategory}: Same as collegeCategory.
 * {@link #date}: The date in which this event BEGINS. If this event crosses over midnight, the date
 *                is that of the 1st day.
 * {@link #additional}: Additional information to display in a special format. Formatted like so:
 *                      ## HEADER ## ____BULLET # INFO ____BULLET # INFO.
 * {@link #placeId}: String identifying location of event.
 * {@link #full}: Is this event full.
 * {@link #imagePk}: Pk value of the image this event is linked to.
 *
 * NOTE: Since events can cross over midnight, the {@link #endTime} may not be "after" the {@link #startTime}.
 *       Calculations should take this into account.
 *
 * @see Category
 */
public class Event implements Comparable<Event>
{
	public final String title;
	public final String caption;
	public final String description;
	public final int collegeCategory;
	public final int typeCategory;
	public final LocalDate date;
	public final LocalTime startTime;
	public final LocalTime endTime;
	public final String additional;
	public final String placeId;
	public final boolean full;
	public final int imagePk;
	public final int pk;
	public static final String DISPLAY_TIME_FORMAT = "h:mm a";  //hour:minute AM/PM
	public static final String DISPLAY_PADDED_TIME_FORMAT = "hh:mm a";
	private static final String DATABASE_TIME_FORMAT = "HH:mm:ss";
	private static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd";
	private static final DateTimeFormatter DATABASE_TIME_FORMATTER = DateTimeFormat.forPattern(DATABASE_TIME_FORMAT);
	private static final DateTimeFormatter DATABASE_DATE_FORMATTER = DateTimeFormat.forPattern(DATABASE_DATE_FORMAT);
	private static final String TAG = Event.class.getSimpleName();

	/**
	 * Creates an event object in-app. This should never be done organically (without initial input
	 * from the database in some form), or else we risk becoming out-of-sync with the database.
	 * @param title For example, "New Student Check-In"
	 * @param caption For example, "Bartels Hall"
	 * @param description For example, "You are required to attend New Student Check-In to pick up..."
	 * @param collegeCategory See class description.
	 * @param typeCategory See class description
	 * @param date For example, 7/19/2017
	 * @param startTime For example, 8:00 AM
	 * @param endTime For example, 4:00 PM
	 * @param additional For example, ## All new students are required to attend this program at the following times: ## ____3:30pm # Residents of Balch, Jameson, Risley, Just About Music, Ecology House, and Latino Living Center; on-campus transfers in Call Alumni Auditorium ____5:30pm # Residents of Dickson, McLLU, Donlon, High Rise 5, and Ujamaa; off-campus transfers in Call Alumni Auditorium ____8:00pm # Residents of Townhouses, Low Rises, Court-Kay-Bauer, Mews, Holland International Living Center, and Akwe:kon
	 * @param placeId For example, ChIJndqRYRqC0IkR9J8bgk3mDvU
	 * @param full See class description.
	 * @param imagePk Unique positive ID given to each image starting from 1.
	 * @param pk Unique positive ID given to each event starting from 1.
	 */
	public Event(String title, String caption, String description, int collegeCategory, int typeCategory, LocalDate date, LocalTime startTime, LocalTime endTime, String additional, String placeId, boolean full, int imagePk, int pk)
	{
		this.title = title;
		this.caption = caption;
		this.description = description;
		this.collegeCategory = collegeCategory;
		this.typeCategory = typeCategory;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.additional = additional;
		this.placeId = placeId;
		this.full = full;
		this.imagePk = imagePk;
		this.pk = pk;
	}

	/**
	 * Creates an event object using data downloaded from the database as a {@link JSONObject}.
	 *
	 * @param json JSON with the expected keys and values:
	 *             name => String
	 *             pk => int
	 *             description => String
	 *             category => int
	 *             start_date => date formatted according to {@link #DATABASE_DATE_FORMAT}
	 *             start_time => time formatted according to {@link #DATABASE_TIME_FORMAT}
	 *             end_time => see start_time
	 *             required => boolean
	 *             category_required => boolean
	 */
	public Event(JSONObject json)
	{
		title = json.optString("name");
		pk = json.optInt("pk");
		description = json.optString("description");
		caption = json.optString("location");
		collegeCategory = json.optInt("college_category");
		typeCategory = json.optInt("type_category");
		String startDate = json.optString("start_date");
		String startTime = json.optString("start_time");
		String endTime = json.optString("end_time");
		String placeId = json.optString("place_ID");
		//defaults to cornell's place id
		this.placeId = placeId.isEmpty() ? "ChIJndqRYRqC0IkR9J8bgk3mDvU" : placeId;
		additional = json.optString("additional");
		full = json.optBoolean("full");
		imagePk = json.optInt("image_pk");

		date = LocalDate.parse(startDate, DATABASE_DATE_FORMATTER);
		this.startTime = LocalTime.parse(startTime, DATABASE_TIME_FORMATTER);
		this.endTime = LocalTime.parse(endTime, DATABASE_TIME_FORMATTER);
	}
	/**
	 * Returns {@link #startTime} combined with {@link #date}. Adjusts the date to tomorrow if the event
	 * does not ACTUALLY occur on the {@link #date}, but during midnight of the next day.
	 *
	 * @return DateTime for the start of the event
	 */
	public LocalDateTime startDateTime()
	{
		if (startTime.getHourOfDay() >= ScheduleFragment.START_HOUR)
			return date.toLocalDateTime(startTime);

		LocalDate nextDay = date.plusDays(1);
		return nextDay.toLocalDateTime(startTime);
	}
	public String readableDate()
	{
		return date.toString("EEEE, MMM d");
	}
	/**
	 * Returns the {@link #pk}, which is unique to each {@link Event}.
	 *
	 * @return {@link #pk}
	 */
	@Override
	public int hashCode()
	{
		return pk;
	}
	/**
	 * Returns whether this object has the same {@link #pk} as the given object.
	 *
	 * @param obj {@inheritDoc}
	 * @return See description.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Event))
			return false;
		Event other = (Event) obj;
		return other.pk == pk;
	}
	/**
	 * Compares 2 {@link Event}s using their start times. Useful for ordering chronologically.
	 *
	 * @param o {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	public int compareTo(@NonNull Event o)
	{
		if (!date.isEqual(o.date))
			return date.compareTo(o.date);
		//If this event starts in the next day, and event "o" starts in the previous day
		if (startTime.getHourOfDay() <= ScheduleFragment.END_HOUR && o.startTime.getHourOfDay() >= ScheduleFragment.START_HOUR)
			return 1;
		//If this event starts in the previous day, and event "o" starts in the next day
		if (o.startTime.getHourOfDay() <= ScheduleFragment.END_HOUR && startTime.getHourOfDay() >= ScheduleFragment.START_HOUR)
			return -1;
		//if the times are the same, return 0. This is done because LocalTime.compareTo also looks at things like milliseconds
		if (startTime.isEqual(o.startTime))
			return 0;
		return startTime.compareTo(o.startTime);
	}
	/**
	 * Returns a String containing all relevant info about this object. Each field is separated
	 * by a pipe "|", which David Chu deemed would not be regularly used in a String and thus is a good
	 * delimiter for splitting via {@link String#split(String)}. Useful for saving to file.
	 *
	 * @return {@inheritDoc}
	 * @see #fromString(String)
	 */
	@Override
	public String toString()
	{
		return title + "|" + caption + "|" + description + "|" + collegeCategory + "|" + typeCategory + "|" + DATABASE_DATE_FORMATTER.print(date) +
				"|" + DATABASE_TIME_FORMATTER.print(startTime) + "|" + DATABASE_TIME_FORMATTER.print(endTime) + "|" +
				(full ? 1 : 0) + "|" + pk + "|" + additional + "|" + placeId + "|" + imagePk;
	}
	/**
	 * Returns a {@link Event} from its String representation produced by {@link #toString()}.
	 *
	 * @param string String produced by {@link #toString()}.
	 * @return {@link Event} created from the String.
	 * @see #toString()
	 */
	public static Event fromString(String string)
	{
		String[] parts = string.split("\\|", -1);
		String title = parts[0];
		String caption = parts[1];
		String description = parts[2];
		String collegeCategory = parts[3];
		String typeCategory = parts[4];
		String date = parts[5];
		String startTime = parts[6];
		String endTime = parts[7];
		String full = parts[8];
		String pk = parts[9];
		String additional = parts[10];
		String placeId = parts[11];
		String imagePk = parts[12];
		return new Event(title, caption, description, Integer.valueOf(collegeCategory), Integer.valueOf(typeCategory), LocalDate.parse(date, DATABASE_DATE_FORMATTER),
				LocalTime.parse(startTime, DATABASE_TIME_FORMATTER), LocalTime.parse(endTime, DATABASE_TIME_FORMATTER), additional, placeId, full.equals("1"), Integer.valueOf(imagePk), Integer.valueOf(pk));
	}
}
