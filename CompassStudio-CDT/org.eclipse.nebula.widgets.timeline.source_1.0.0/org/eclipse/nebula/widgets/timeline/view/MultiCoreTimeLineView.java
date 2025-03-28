package org.eclipse.nebula.widgets.timeline.view;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimeline;
import org.eclipse.nebula.widgets.timeline.ITrack;
import org.eclipse.nebula.widgets.timeline.jface.TimelineViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Subgraph timeLine view
 *
 * @author alache01
 *
 */
public class MultiCoreTimeLineView {
	private TimelineViewer timelineViewer;

	MultiCoreTimeLineView() {

	}

	public MultiCoreTimeLineView(Composite composite, String hardwareJsonPath) {
		timelineViewer = null;
		timelineViewer = new TimelineViewer(composite, SWT.NULL, true, hardwareJsonPath);

		ITimeline model = (ITimeline) timelineViewer.getInput();

		JsonParserTimeLine jsonParser = new JsonParserTimeLine(hardwareJsonPath);
		jsonParser.parserProfilerJson();

		List<ILane> lanes = null;
		if (jsonParser.target.equals("X2_1204MP3")) {
			ITrack track0 = model.createTrack("core0");
			ITrack track1 = model.createTrack("core1");
			ITrack track2 = model.createTrack("core2");

			ILane core0 = track0.createLane();

			ILane core1 = track1.createLane();
			ILane core2 = track2.createLane();

			lanes = Arrays.asList(core0, core1, core2);
		} else if (jsonParser.target.equals("X3_1304MP2")) {
			ITrack track0 = model.createTrack("core0");
			ITrack track1 = model.createTrack("core1");

			ILane core0 = track0.createLane();

			ILane core1 = track1.createLane();

			lanes = Arrays.asList(core0, core1);
		} else {
			ITrack track0 = model.createTrack("core0");
			ILane core0 = track0.createLane();

			lanes = Arrays.asList(core0);
		}

		List<TenerImp> tensorList = jsonParser.getTenerList();
		for (TenerImp tenerImp : tensorList) {
			String tips = "";
			ILane lane = null;
			if (tenerImp.getCoreId().equals("0")) {
				lane = lanes.get(0);
			}
			if (tenerImp.getCoreId().equals("1")) {
				lane = lanes.get(1);
			}
			if (tenerImp.getCoreId().equals("2")) {
				lane = lanes.get(2);
			}

			tips = tips + "Cycles" + "=" + tenerImp.getCycles() + "\n";
			tips = tips + "AIPU Read Bytes" + "=" + readSize(tenerImp.getAIPU_Read_Bytes()) + "\n";
			tips = tips + "AIPU Write Bytes" + "=" + readSize(tenerImp.getAIPU_Write_Bytes()) + "\n";
			tips = tips + "BW_RW(GB/s)" + "=" + doubleFormat(tenerImp.getBW_RW()) + "\n";
			tips = tips + "BW_R(GB/s)" + "=" + doubleFormat(tenerImp.getBW_R()) + "\n";
			tips = tips + "BW_W(GB/s)" + "=" + doubleFormat(tenerImp.getBW_W()) + "\n";
			tips = tips + "MAC Utilization" + "=" + doubleFormat(tenerImp.getMAC_Utilization()) + "\n";
			tips = tips + "Input Shapes" + "=" + tenerImp.getInput_Shapes() + "\n";
			tips = tips + "Out Shapes" + "=" + tenerImp.getOutput_Shapes() + "\n";
			tips = tips + "Layer Name" + "=" + tenerImp.getLayerName();

			lane.createEvent(tenerImp.getLayerName(), tips.trim(), tenerImp.getStart(), tenerImp.getCycles(), TimeUnit.NANOSECONDS);
		}

		model.createCursor(122, TimeUnit.NANOSECONDS);
		timelineViewer.refresh();
	}

	public String readSize(long size) {
		if (size <= 0) {
			return "0";
		}
		String[] unitBytes = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,###.##").format(size / Math.pow(1024, digitGroups)) + " " + unitBytes[digitGroups];
	}

	public String doubleFormat(double num) {
		if (num <= 0) {
			return "0";
		}
		return String.format("%.2f", num) + " %";
	}

	public TimelineViewer getTimelineViewer() {
		return timelineViewer;
	}

	public void setTimelineViewer(TimelineViewer timelineViewer) {
		this.timelineViewer = timelineViewer;
	}

}
