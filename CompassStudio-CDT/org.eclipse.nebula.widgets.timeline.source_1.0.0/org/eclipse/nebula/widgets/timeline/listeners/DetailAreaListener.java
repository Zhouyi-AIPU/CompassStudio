/*******************************************************************************
 * Copyright (c) 2020 Christian Pontesegger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Christian Pontesegger - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline.listeners;

import java.util.Map;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.widgets.timeline.view.JsonParserTimeLine;
import org.eclipse.nebula.widgets.timeline.view.TecInfoView;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.gson.JsonObject;

import cn.com.armchina.ide.multicore.profiler.view.MultiCorePropertiesView;

/**
 * This listener has 3 tasks:
 * <ul>
 * <li>move x offset of detail area (mouse drag)</li>
 * <li>create cursor (mouse click in empty area)</li>
 * <li>select event (mouse click on event)</li>
 * </ul>
 */
public class DetailAreaListener extends MouseMotionListener.Stub implements MouseListener, MouseMotionListener {

	private final String PROPERTIES_VIEW_ID = "cn.com.armchina.ide.multicore.profiler.properties.view";

	private Point fLocation = null;
	private Figure fFigure;

	private boolean fDragged = false;
	private String jsonPath;
	// Release for 2024_Q3
	// private MenuItem gragphMenuItem;
	private String subName;
	private MenuItem delMenuItem;
	private EventFigure ef;

	private static IFigure currentFigure = null;

	public DetailAreaListener(Figure figure) {
		fFigure = figure;

		figure.addMouseListener(this);
		figure.addMouseMotionListener(this);
	}

	public DetailAreaListener(Figure figure, String path, MenuItem gmi, MenuItem delmi) {
		fFigure = figure;

		figure.addMouseListener(this);
		figure.addMouseMotionListener(this);
		this.jsonPath = path;
		this.delMenuItem = delmi;
		// Release for 2024_Q3
		// this.gragphMenuItem = gmi;
		// gragphMenuItem.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent me) {
		// if (!subName.equals("")) {
		// System.out.println(subName);
		// ef = null;
		// subName = "";
		// }
		// }
		// });
		delMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent me) {
				if (ef != null) {
					ef.setEventColor(ef.getEventColor());
					ef.setLineWidth(2);
					ef = null;
					subName = "";
				}
			}
		});
	}

	@Override
	public void mousePressed(MouseEvent me) {
		fDragged = false;
		fLocation = me.getLocation();

		me.consume();
		ef = null;
		subName = "";
		final IFigure figureUnderCursor = fFigure.findFigureAt(me.x, me.y);
		// add properties view
		if ((me.button == 1) && (figureUnderCursor instanceof EventFigure)) {
			JsonParserTimeLine jsonParser = new JsonParserTimeLine(jsonPath);
			jsonParser.parserProfilerJson();
			EventFigure eventFigure = (EventFigure) figureUnderCursor;
			String subGraphName = eventFigure.getEvent().getTitle();
			final Map<String, String> properties = jsonParser.getSubGraphMap().get(subGraphName);
			final JsonObject tecInfos = jsonParser.getTecTimeLineMap().get(subGraphName);
			IViewPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(PROPERTIES_VIEW_ID);
			if (part == null) {
				try {
					part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PROPERTIES_VIEW_ID);
				} catch (final PartInitException e) {
					// TODO handle this exception (but for now, at least know it happened)
					throw new RuntimeException(e);

				}
			}
			if ((part != null) && (part instanceof MultiCorePropertiesView)) {

				if ((currentFigure == null) || !figureUnderCursor.equals(currentFigure)) {
					((MultiCorePropertiesView) part).refresh(subGraphName, properties, tecInfos);
					currentFigure = figureUnderCursor;
				}

			}
		}
		if ((me.button == 3) && (figureUnderCursor instanceof EventFigure)) {
			ef = (EventFigure) figureUnderCursor;
			subName = ef.getEvent().getTitle();
		}
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		if (!fDragged) {
			final IFigure figureUnderCursor = fFigure.findFigureAt(me.x, me.y);

			if (figureUnderCursor instanceof EventFigure) {
				// selection
				RootFigure.getRootFigure(fFigure).setSelection((EventFigure) figureUnderCursor);
			} else {
				// create cursor
				final long eventTime = RootFigure.getTimeViewDetails(fFigure).screenOffsetToEventTime(me.x);
				RootFigure.getRootFigure(fFigure).createCursor(eventTime);
			}
		}

		if (fLocation != null) {
			fLocation = null;
			me.consume();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.draw2d.MouseMotionListener.Stub#mouseEntered(org.eclipse.draw2d.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent me) {
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		final IFigure figureUnderCursor = fFigure.findFigureAt(me.x, me.y);
		if ((me.button == 1) && (figureUnderCursor instanceof EventFigure)) {
			final ITimelineEvent event = ((EventFigure) figureUnderCursor).getEvent();
			new TecInfoView().show(event, jsonPath);
		}
	}

	@Override
	public void mouseDragged(MouseEvent me) {
		if (fLocation != null) {
			fDragged = true;

			final Point targetLocation = me.getLocation();

			final Dimension offset = fLocation.getDifference(targetLocation);
			if (offset.width() != 0) {
				final TimeBaseConverter timeDetails = RootFigure.getRootFigure(fFigure).getTimeViewDetails();
				if (timeDetails.translateDetailAreaOffset(offset.width()))
					fLocation = targetLocation;

				me.consume();
			}
		}
	}
}
