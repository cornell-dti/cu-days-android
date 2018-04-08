package org.cornelldti.cudays;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.cornelldti.cudays.models.Event;

/**
 * Holds data and reference pointers to {@link View}s for an {@link Event}. Its physical representation
 * is in {@link R.layout#cell_feed}.
 *
 * {@link #event}: The event that this object currently represents.
 * {@link #context}: To be used in {@link #onClick(View)}
 *
 * @see FeedAdapter
 */
public class FeedCell extends RecyclerView.ViewHolder implements View.OnClickListener
{
	private final TextView startTimeText;
	private final TextView endTimeText;
	private final TextView titleText;
	private final TextView captionText;
	private final Context context;
	private Event event;
	private static final String	TAG = FeedCell.class.getSimpleName();

	/**
	 * Stores pointers to all the subviews and sets up listeners.
	 * @param itemView {@inheritDoc}
	 */
	public FeedCell(View itemView)
	{
		super(itemView);
		startTimeText = itemView.findViewById(R.id.startTimeText);
		endTimeText = itemView.findViewById(R.id.endTimeText);
		titleText = itemView.findViewById(R.id.titleText);
		captionText = itemView.findViewById(R.id.captionText);
		itemView.setOnClickListener(this);
		context = itemView.getContext();
	}
	/**
	 * Sets the current event to display. Time display formats are specified here: {@link Event#DISPLAY_TIME_FORMAT}.
	 *
	 * @param event The {@link Event} this cell will represent as long as it is visible.
	 */
	public void configure(Event event)
	{
		this.event = event;
		startTimeText.setText(event.startTime.toString(Event.DISPLAY_PADDED_TIME_FORMAT));
		endTimeText.setText(event.endTime.toString(Event.DISPLAY_PADDED_TIME_FORMAT));
		titleText.setText(event.title);
		captionText.setText(event.caption);

		if (event.full)
		{
			titleText.setAlpha(0.5f);
		}
		else
		{
			titleText.setAlpha(1);
		}
	}
	/**
	 * This object has been clicked. Open the details page.
	 * @param v Clicked view.
	 */
	@Override
	public void onClick(View v)
	{
		DetailsActivity.startWithEvent(event, context);
	}
}
