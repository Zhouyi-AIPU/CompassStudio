package org.eclipse.nebula.widgets.timeline.view;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.nebula.widgets.timeline.listeners.DetailAreaListener;

/**
 * @ClassName: DetailAreaNoListener
 *
 * @Description:TecTimeLine view remove mouse listener
 *
 * @author Alan Chen
 */
public class DetailAreaNoListener extends DetailAreaListener {

	/**
	 * @param figure
	 */
	public DetailAreaNoListener(Figure figure) {
		super(figure);
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.widgets.timeline.listeners.DetailAreaListener#mousePressed(org.eclipse.draw2d.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent me) {
		// nothing to do
	}
}
