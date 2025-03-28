package org.eclipse.nebula.widgets.timeline.view;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;

/**
 * Customized rootFigure of tecTimeLine view
 *
 * @author alache01
 *
 */
public class TecInfoRootFigure extends RootFigure {
	public TecInfoRootFigure() {

	}

	/**
	 * Customized overview figure of TecTimeLine view
	 *
	 * @param resourceManager
	 */
	public TecInfoRootFigure(ResourceManager resourceManager) {
		fResourceManager = resourceManager;
		setStyleProvider(null);

		fTimeViewDetails = new TecInfoViewTimeBaseConverter(this);

		final BorderLayout layout = new BorderLayout();
		layout.setVerticalSpacing(0);
		setLayoutManager(layout);

		setOpaque(true);
		updateStyle(fStyleProvider);

		fDetailFigure = new TecInfoDetalFigure(getStyleProvider());
		add(fDetailFigure, BorderLayout.CENTER);

		fOverviewFigure = new TecOverviewFigure(getStyleProvider());
		add(fOverviewFigure, BorderLayout.BOTTOM);

	}

}
