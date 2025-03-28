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

package org.eclipse.nebula.widgets.timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.widgets.timeline.listeners.TimelineScaler;
import org.eclipse.nebula.widgets.timeline.view.TecInfoRootFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import cn.com.armchina.ide.tools.timeLine.search.ui.SubGraphNameSearach;

public class TimelineComposite extends Composite {

	public RootFigure fRootFigure;
	public final LocalResourceManager fResourceManager;

	public TimelineComposite(Composite parent, int style) {
		super(parent, style);

		fResourceManager = new LocalResourceManager(JFaceResources.getResources(), this);

		final FillLayout layout = new FillLayout();
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		setLayout(layout);

		setBackground(ColorConstants.white);

		final Canvas canvas = new Canvas(this, SWT.DOUBLE_BUFFERED);
		canvas.setBackground(ColorConstants.black);
		final LightweightSystem lightWeightSystem = new LightweightSystem(canvas);

		fRootFigure = new RootFigure(fResourceManager);
		fRootFigure.setFont(parent.getFont());
		lightWeightSystem.setContents(fRootFigure);

		// draw2d does not directly support mouseWheelEvents, so register on canvas
		canvas.addMouseWheelListener(new TimelineScaler(this));
		// canvas.get
		// canvas.addKeyListener(new KeyAdapter);
	}

	public TimelineComposite(Composite parent, int style, boolean b, String path, Map<String, EventFigure> map) {
		super(parent, style);

		fResourceManager = new LocalResourceManager(JFaceResources.getResources(), this);

		final FillLayout layout = new FillLayout();
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		setLayout(layout);

		setBackground(ColorConstants.white);
		MenuItem graphEditeItem = null;
		MenuItem delEditeItem = null;
		MenuItem searchItem = null;
		final Canvas canvas = new Canvas(this, SWT.DOUBLE_BUFFERED);
		if (b) {
			Menu popMenu = new Menu(canvas.getShell(), SWT.POP_UP);

			delEditeItem = new MenuItem(popMenu, SWT.PUSH);
			delEditeItem.setText("&Unselected");

			// Release for 2024_Q3
			// graphEditeItem = new MenuItem(popMenu, SWT.PUSH);
			// graphEditeItem.setText("&Open Model Visualizer Editor");

			searchItem = new MenuItem(popMenu, SWT.PUSH);
			searchItem.setText("&Search Subgraph Name");
			searchItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent me) {
					moveToEvent(map);
				}
			});

			canvas.setMenu(popMenu);
			// ctrl+f
			canvas.addListener(SWT.KeyDown, new Listener() {

				@Override
				public void handleEvent(Event event) {
					if ((event.stateMask == SWT.CTRL) && (event.keyCode == 102)) {
						moveToEvent(map);
					}
				}
			});
		}

		canvas.setBackground(ColorConstants.black);

		final LightweightSystem lightWeightSystem = new LightweightSystem(canvas);

		if (b) {
			fRootFigure = new RootFigure(fResourceManager, path, graphEditeItem, delEditeItem);
		} else
			fRootFigure = new TecInfoRootFigure(fResourceManager);
		fRootFigure.setFont(parent.getFont());
		lightWeightSystem.setContents(fRootFigure);

		// draw2d does not directly support mouseWheelEvents, so register on canvas
		canvas.addMouseWheelListener(new TimelineScaler(this));
		// canvas.get
		// canvas.addKeyListener(new KeyAdapter);
	}

	// search subgraph name
	private void moveToEvent(Map<String, EventFigure> map) {
		List<String> nameList = new ArrayList<>();
		for (Map.Entry<String, EventFigure> entry : map.entrySet()) {
			String key = entry.getKey();
			nameList.add(key);
		}
		SubGraphNameSearach.show(nameList);
		String subName = SubGraphNameSearach.getSugName();
		if (subName.equals("")) {
			return;
		}
		EventFigure eventFigure = map.get(subName);

		eventFigure.setForegroundColor(ColorConstants.yellow);
		eventFigure.setLineWidth(3);
		eventFigure.setAlpha(255);

		Point location = eventFigure.getLocation();
		Rectangle bounds = getBounds();
		boolean contains = bounds.contains(new org.eclipse.swt.graphics.Point(location.x, location.y));

		if (!contains) {
			TimeBaseConverter timeViewDetails = RootFigure.getTimeViewDetails(fRootFigure.fDetailFigure);
			timeViewDetails.translateDetailAreaOffset(location.x - bounds.width / 2);
		}
	}

	public RootFigure getRootFigure() {
		return fRootFigure;
	}

	@Override
	public void dispose() {
		fRootFigure.getStyleProvider().dispose();
		fResourceManager.dispose();

		super.dispose();
	}
}
