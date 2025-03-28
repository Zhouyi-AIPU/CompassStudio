/*******************************************************************************
 * Copyright (c) 2023 shedu01 and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     shedu01 - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline.view;

import java.util.List;

import org.eclipse.nebula.widgets.timeline.ITimed;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;

import com.google.gson.JsonObject;

/**
 * @author shedu01
 * 
 * @description timeline base converter for tecinfoview, convert timeline event to coordinates
 * 
 * @ClassName: TecInfoViewTimeBaseConverter
 */
public class TecInfoViewTimeBaseConverter extends TimeBaseConverter {

	private long initOffset = -1;
	private boolean init = true;

	/**
	 * @param rootFigure
	 */
	public TecInfoViewTimeBaseConverter(RootFigure rootFigure) {
		super(rootFigure);
		// this.fRootFigure = new RootFigure();

	}

	@Override
	public void addEvent(ITimed event) {
		if (fRequiredEventArea == null) {
			fRequiredEventArea = event.getTiming().copy();

			// first event
			setOffset(event.getTiming().left());
			if (fDetailScreenWidth >= 0) {
				// get the largest lane's length
				final List<JsonObject> list = TecInfoView.getTecDurationInfoList();
				if (list == null || list.size() == 0) {
					return;
				}
				JsonObject lastObject = list.get(list.size() - 1);
				Long lastCount = Long.parseLong(lastObject.get("start").toString()) + Long.parseLong(lastObject.get("duration").toString());
				lastCount = (long) (lastCount - initOffset);
				if ((lastCount < 100000000) && (lastCount >= 70000000)) {
					setScaleFactor(0.000004, false);
				} else if ((lastCount < 70000000) && (lastCount >= 35000000)) {
					setScaleFactor(0.000006, false);
				} else if ((lastCount < 35000000) && (lastCount >= 10000000)) {
					setScaleFactor(0.000015, false);
				} else if ((lastCount < 10000000) && (lastCount >= 7000000)) {
					setScaleFactor(0.00004, false);
				} else if ((lastCount < 7000000) && (lastCount >= 3500000)) {
					setScaleFactor(0.00006, false);
				} else if ((lastCount < 3500000) && (lastCount >= 1000000)) {
					setScaleFactor(0.0015, false);
				} else if ((lastCount < 1000000) && (lastCount >= 100000)) {
					setScaleFactor(0.005, false);
				} else if ((lastCount < 100000) && (lastCount >= 35000)) {
					setScaleFactor(0.01, false);
				} else if ((lastCount < 35000) && (lastCount >= 10000)) {
					setScaleFactor(0.03, false);
				} else if ((lastCount < 10000) && (lastCount >= 5000)) {
					setScaleFactor(0.08, false);
				} else if ((lastCount < 5000) && (lastCount >= 1000)) {
					setScaleFactor(0.10, false);
				}
			}
		} else
			fRequiredEventArea.union(event.getTiming());
	}

	// can't fix the start offset, only set the init offfset;
	// the fOffset can't be less than the start offset
	@Override
	public void setOffset(double eventTime) {
		if (initOffset < 0) {
			initOffset = TecInfoView.getTimeevent().getStartTimestamp();
			fOffset = initOffset;
		} else {
			fOffset = eventTime;
			if (fOffset < initOffset)
				fOffset = initOffset;
			adjustInvalidOffset();
		}

		fRootFigure.fireTimebaseChanged();
	}

	// fix the event area left side. the coordinates convert based on the left side
	@Override
	public Timing getEventArea() {
		Timing time = (fRequiredEventArea != null) ? fRequiredEventArea.copy() : new Timing(0);

		if (fOffset < time.left()) {
			time.union(new Timing(fOffset, fRequiredEventArea.getDuration()));
		}

		return time;
	}

	@Override
	public boolean translateEventTime(long eventTime) {
		final Timing eventArea = getEventArea();
		if (eventTime < 0) {
			if (fOffset >= eventArea.left()) // the fOffset uses to be the same with the eventarea.left
				setOffset(fOffset + eventTime);

		} else if (eventTime > 0) {
			if (fOffset < ((eventArea.right()) - getVisibleEventArea().getDuration()))
				setOffset(fOffset + eventTime);

		} else
			return false;

		return true;
	}

	// modify the scalorfactor for some case which the first event is on the right side of the screen
	@Override
	public void setScreenWidth(double screenWidth) {
		super.setScreenWidth(screenWidth);
		// only for the first time.
		if (init) {
			setInitScalorFactorForTecInfoTimeline();
			init = false;
		}

	}

	// interate computation until the appropriate scalefoctor, since the tec info event may not in the
	// screen with zoom out once
	private void setInitScalorFactorForTecInfoTimeline() {
		double screen = getVisibleEventArea().getDuration() + getVisibleEventArea().getTimestamp();
		final List<JsonObject> list = TecInfoView.getTecDurationInfoList();
		if (list == null || list.size() == 0) {
			return;
		}
		JsonObject firstObject = list.get(0);
		Long firstStart = Long.parseLong(firstObject.get("start").toString());
		double ratio = (firstStart - fOffset) / (screen - fOffset);
		if (ratio > 0.4) {
			double a = getScaleFactor() * 0.5;
			setScaleFactor(a, false);
			setInitScalorFactorForTecInfoTimeline();
		} else {
			return;
		}

	}
}
