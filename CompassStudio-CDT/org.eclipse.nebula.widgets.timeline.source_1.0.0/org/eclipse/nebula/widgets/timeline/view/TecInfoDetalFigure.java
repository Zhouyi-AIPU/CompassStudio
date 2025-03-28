package org.eclipse.nebula.widgets.timeline.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.DetailFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.TimeAxisFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TracksFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

/**
 * Customized desgin tecTimeLine view
 *
 * @author alache01
 *
 */
public class TecInfoDetalFigure extends DetailFigure {

	/**
	 * @param styleProvider
	 */
	public TecInfoDetalFigure() {

	}

	public TecInfoDetalFigure(ITimelineStyleProvider styleProvider) {
		final BorderLayout layout = new BorderLayout();
		layout.setHorizontalSpacing(0);
		layout.setVerticalSpacing(0);
		setLayoutManager(layout);

		add(new TracksFigure(styleProvider), BorderLayout.CENTER);
		add(new TimeAxisFigure(styleProvider), BorderLayout.TOP);

		new DetailAreaNoListener(this);
	}

	@Override
	public List<Double> getEventTimeMarkerPositions() {
		final List<Double> positions = new ArrayList<>();

		final TimeBaseConverter timeViewDetails = RootFigure.getTimeViewDetails(this);
		final Timing visibleEventArea = timeViewDetails.getVisibleEventArea();

		final double stepSize = getStepSize();
		// final long startValue = (long) ((Math.floor((visibleEventArea.left()) / stepSize) + 1) * stepSize);
		// start with the subgraph node start times
		ITimelineEvent timeEvent = TecInfoView.getTimeevent();
		final long startValue = timeEvent.getStartTimestamp();

		for (long pos = startValue; pos < visibleEventArea.right(); pos += stepSize)
			positions.add((double) pos);

		return positions;
	}

}
