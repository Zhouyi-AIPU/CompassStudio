package cn.com.armchina.gbuild.ui.jfreeswt.utils;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.Serializable;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.Zoomable;

/**
 * A class that handles mouse wheel events for the {@link ChartPanel} class.
 */
public class MouseWheelHandler implements MouseWheelListener, Serializable {

	/** The chart panel. */
	private ChartComposite chartPanel;

	/** The zoom factor. */
	double zoomFactor;

	/**
	 * Creates a new instance for the specified chart panel.
	 *
	 * @param chartPanel the chart panel ({@code null} not permitted).
	 */
	public MouseWheelHandler(ChartComposite chartPanel) {
		this.chartPanel = chartPanel;
		this.zoomFactor = 0.10;
		this.chartPanel.addMouseWheelListener(this);
	}

	/**
	 * Returns the current zoom factor. The default value is 0.10 (ten percent).
	 *
	 * @return The zoom factor.
	 *
	 * @see #setZoomFactor(double)
	 */
	public double getZoomFactor() {
		return this.zoomFactor;
	}

	/**
	 * Sets the zoom factor.
	 *
	 * @param zoomFactor the zoom factor.
	 *
	 * @see #getZoomFactor()
	 */
	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}

	/**
	 * Handles a mouse wheel event from the underlying chart panel.
	 *
	 * @param e the event.
	 */
	/**
	 * Handle the case where a plot implements the {@link Zoomable} interface.
	 *
	 * @param zoomable the zoomable plot.
	 * @param e        the mouse wheel event.
	 */
	private void handleZoomable(Zoomable zoomable, MouseEvent e) {
		// don't zoom unless the mouse pointer is in the plot's data area
		ChartRenderingInfo info = this.chartPanel.getChartRenderingInfo();
		PlotRenderingInfo pinfo = info.getPlotInfo();
//        this.chartPanel.translateScreenToJava2D(null);
		Point2D p = this.chartPanel.translateScreenToJava2D(new Point(e.x, e.y));
		if (!pinfo.getDataArea().contains(p)) {
			return;
		}

		Plot plot = (Plot) zoomable;
		// do not notify while zooming each axis
		boolean notifyState = plot.isNotify();
		plot.setNotify(false);
		int clicks = e.count;
		double zf = 1.0 + this.zoomFactor;
		if (clicks < 0) {
			zf = 1.0 / zf;
		}
		if (chartPanel.isDomainZoomable()) {
			zoomable.zoomDomainAxes(zf, pinfo, p, true);
		}
		if (chartPanel.isRangeZoomable()) {
			zoomable.zoomRangeAxes(zf, pinfo, p, true);
		}
		plot.setNotify(notifyState); // this generates the change event too
	}

	@Override
	public void mouseScrolled(MouseEvent e) {
		// TODO Auto-generated method stub
		JFreeChart chart = this.chartPanel.getChart();
		if (chart == null) {
			return;
		}
		Plot plot = chart.getPlot();
		if (plot instanceof Zoomable) {
			Zoomable zoomable = (Zoomable) plot;
			handleZoomable(zoomable, e);
		} else if (plot instanceof PiePlot) {
			PiePlot pp = (PiePlot) plot;
			pp.handleMouseWheelRotation(e.count);
		}
	}

}
