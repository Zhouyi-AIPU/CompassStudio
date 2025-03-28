/*******************************************************************************
 * Copyright (c) 2023 alache01 and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     alache01 - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline.view;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

/**
 * Customized height of overviewFigure
 *
 * @author alache01
 *
 */
public class TecOverviewFigure extends OverviewFigure {
	public int fPreferredHeight = 120;
	public final int TEC_Y_PADDING = 2;

	/**
	 * @param styleProvider
	 */
	public TecOverviewFigure(ITimelineStyleProvider styleProvider) {
		super(styleProvider);
	}

	@Override
	protected void layout() {
		super.layout();

		final TimeBaseConverter timeConverter = RootFigure.getTimeViewDetails(this);
		timeConverter.setOverviewScreenWidth(getBounds().width() - getInsets().getWidth());
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		final int laneCount = RootFigure.getLaneCount(this);
		final int requiredHeight = (laneCount * fEventHeight) + ((laneCount + 1) * TEC_Y_PADDING) + getInsets().getHeight();

		return new Dimension(wHint, Math.max(requiredHeight, this.fPreferredHeight));
	}

}
