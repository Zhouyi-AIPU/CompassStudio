package org.eclipse.nebula.widgets.timeline.view;

import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimeline;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.ITrack;
import org.eclipse.nebula.widgets.timeline.jface.TimelineViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cn.com.armchina.ide.utils.Messages;

/**
 * TecTimeLine view
 *
 * @author alache01
 *
 */
public class TecInfoView extends Shell {

	public TecInfoView shell;
	private Map<String, JsonObject> tecTimeLineMap;
	private static List<JsonObject> tecDurationInfoList;
	public static ITimelineEvent timeevent; // get the start time as the offset
	private Map<ILane, List> laneModelMap = new HashMap<ILane, List>();
	private static Map<String, String> colorMap;

	public TecInfoView() {

	}

	public void show(ITimelineEvent event, String path) {
		Display display = Display.getDefault();
		timeevent = (ITimelineEvent) event;
		if (Messages.BUILD_INFO != null && Messages.BUILD_INFO.equals(Messages.Release_Internal)) {
			shell = new TecInfoView(display, event.getTitle(), path, "internal");
		} else
			shell = new TecInfoView(display, event.getTitle(), path);
		URL imageURL = Platform.getBundle("cn.com.armchina.gbuild.ui").getEntry("icons/sample.png");
		shell.setImage(ImageDescriptor.createFromURL(imageURL).createImage());
		shell.setText("Tec info view");

		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
		int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
		int shellH = shell.getBounds().height;
		int shellW = shell.getBounds().width;
		if (shellH > screenH)
			shellH = screenH;
		if (shellW > screenW)
			shellW = screenW;
		shell.setLocation(((screenW - shellW) / 2), ((screenH - shellH) / 2));

		shell.open();
		shell.layout();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public TecInfoView(Display display, String name, String jsonPath, String internal) {
		super(display, SWT.SHELL_TRIM | SWT.APPLICATION_MODAL | SWT.ON_TOP);
		setSize(800, 780);
		setLayout(new FillLayout(SWT.VERTICAL));
		Composite composite_main = new Composite(this, SWT.NONE);
		composite_main.setLayout(new FillLayout());
		TimelineViewer timelineViewer = null;
		timelineViewer = new TimelineViewer(composite_main, SWT.NULL, false, jsonPath);
		JsonParserTimeLine jsonParser = new JsonParserTimeLine(jsonPath);
		jsonParser.parserProfilerJson();
		tecTimeLineMap = jsonParser.getTecTimeLineMap();
		ITimeline model = (ITimeline) timelineViewer.getInput();

		ITrack tec0 = model.createTrack("tec0");
		ITrack aiff0 = model.createTrack("aiff0");
		ITrack dma0 = model.createTrack("dma0");
		ITrack custom0 = model.createTrack("custom0");
		ITrack tec1 = model.createTrack("tec1");
		ITrack aiff1 = model.createTrack("aiff1");
		ITrack dma1 = model.createTrack("dma1");
		ITrack custom1 = model.createTrack("custom1");
		ITrack tec2 = model.createTrack("tec2");
		ITrack aiff2 = model.createTrack("aiff2");
		ITrack dma2 = model.createTrack("dma2");
		ITrack custom2 = model.createTrack("custom2");
		ITrack tec3 = model.createTrack("tec3");
		ITrack aiff3 = model.createTrack("aiff3");
		ITrack dma3 = model.createTrack("dma3");
		ITrack custom3 = model.createTrack("custom3");

		ILane t0 = tec0.createLane();
		ILane t1 = tec1.createLane();
		ILane t2 = tec2.createLane();
		ILane t3 = tec3.createLane();

		ILane a0 = aiff0.createLane();
		ILane a1 = aiff1.createLane();
		ILane a2 = aiff2.createLane();
		ILane a3 = aiff3.createLane();

		ILane d0 = dma0.createLane();
		ILane d1 = dma1.createLane();
		ILane d2 = dma2.createLane();
		ILane d3 = dma3.createLane();

		ILane c0 = custom0.createLane();
		ILane c1 = custom1.createLane();
		ILane c2 = custom2.createLane();
		ILane c3 = custom3.createLane();

		// List<ILane> lanes = Arrays.asList(t0, a0, d0, t1, a1, d1, t2, a2, d2, t3, a3, d3);
		List<ILane> lanes = Arrays.asList(t0, a0, d0, c0, t1, a1, d1, c1, t2, a2, d2, c2, t3, a3, d3, c3);

		// each lane with its' event models
		for (int i = 0; i < lanes.size(); i++) {
			List<JsonObject> eventModel = new ArrayList<JsonObject>();
			laneModelMap.put(lanes.get(i), eventModel);
		}

		// prepare data for tecTimeLine view
		JsonObject stampArray = tecTimeLineMap.get(name);
		tecDurationInfoList = new ArrayList<>();
		for (Map.Entry<String, JsonElement> s : stampArray.entrySet()) {
			String stampK = s.getKey();
			for (int i = 0; i < 4; i++) {
				if (stampK.equals("tec" + i)) {
					ILane lane = lanes.get(4 * i);
					JsonElement tecInfo = s.getValue();
					if (((JsonArray) tecInfo).size() != 0) {
						for (int l = 0; l < ((JsonArray) tecInfo).size(); l++) {
							String tips = "";
							JsonObject tecInfoObj = ((JsonArray) tecInfo).get(l).getAsJsonObject();
							for (String key : tecInfoObj.keySet()) {
								String message = key + "=" + tecInfoObj.get(key) + "\n";
								tips = tips + message;
							}
							tips = tips.trim();
							lane.createEvent("", tips, Long.parseLong(tecInfoObj.get("start").toString()),
									Long.parseLong(tecInfoObj.get("duration").toString()), TimeUnit.NANOSECONDS);
							laneModelMap.get(lane).add(tecInfoObj);
						}
					}
				}
				if (stampK.equals("aiff" + i)) {
					ILane lane = lanes.get(4 * i + 1);
					JsonElement aiffInfo = s.getValue();
					if (((JsonArray) aiffInfo).size() != 0) {
						for (int l = 0; l < ((JsonArray) aiffInfo).size(); l++) {
							String tips = "";
							JsonObject aiffObj = ((JsonArray) aiffInfo).get(l).getAsJsonObject();
							for (String key : aiffObj.keySet()) {
								String message = key + "=" + aiffObj.get(key) + "\n";
								tips = tips + message;
							}
							tips = tips.trim();
							lane.createEvent("", tips, Long.parseLong(aiffObj.get("start").toString()), Long.parseLong(aiffObj.get("duration").toString()),
									TimeUnit.NANOSECONDS);
							laneModelMap.get(lane).add(aiffObj);
						}
					}
				}
				if (stampK.equals("dma" + i)) {
					ILane lane = lanes.get(4 * i + 2);
					JsonElement dmaInfo = s.getValue();
					if (((JsonArray) dmaInfo).size() != 0) {
						for (int l = 0; l < ((JsonArray) dmaInfo).size(); l++) {
							String tips = "";
							JsonObject dmaObj = ((JsonArray) dmaInfo).get(l).getAsJsonObject();
							for (String key : dmaObj.keySet()) {
								String message = key + "=" + dmaObj.get(key) + "\n";
								tips = tips + message;
							}
							tips = tips.trim();
							lane.createEvent("", tips, Long.parseLong(dmaObj.get("start").toString()), Long.parseLong(dmaObj.get("duration").toString()),
									TimeUnit.NANOSECONDS);
							laneModelMap.get(lane).add(dmaObj);
						}
					}
				}
				if (stampK.equals("custom" + i)) {
					ILane lane = lanes.get(4 * i + 3);
					JsonObject customInfo = (JsonObject) s.getValue();
					// int colorIndex = 0;
					for (Map.Entry<String, JsonElement> e : customInfo.entrySet()) {
						String id = e.getKey();
						JsonArray je = (JsonArray) e.getValue();

						setColorCode(id);

						if ((je.size() != 0)) {
							for (int l = 0; l < je.size(); l++) {
								String tips = "";
								JsonObject customObj = je.get(l).getAsJsonObject();
								for (String key : customObj.keySet()) {
									String message = key + "=" + customObj.get(key) + "\n";
									tips = tips + message;
								}
								tips = tips.trim();
								lane.createEvent("label:" + id, tips, Long.parseLong(customObj.get("start").toString()),
										Long.parseLong(customObj.get("duration").toString()), TimeUnit.NANOSECONDS).setColorCode(colorMap.get(id));
								laneModelMap.get(lane).add(customObj);
							}
						}
					}
				}
			}
		}
		setTecDurationInfoList(getLargestLane());
		model.createCursor(122, TimeUnit.NANOSECONDS);
		timelineViewer.refresh();
	}

	// random color for "custom view"
	public void setColorCode(String id) {
		if (colorMap == null) {
			colorMap = new HashMap<String, String>();
		}
		if (!colorMap.containsKey(id)) {
			Random random = new Random();
			int red = random.nextInt(256);
			int green = random.nextInt(256);
			int blue = random.nextInt(256);
			colorMap.put(id, String.format("#%02X%02X%02X", red, green, blue));
		}
	}

	public TecInfoView(Display display, String name, String jsonPath) {
		super(display, SWT.SHELL_TRIM | SWT.APPLICATION_MODAL | SWT.ON_TOP);
		setSize(800, 650);
		setLayout(new FillLayout(SWT.VERTICAL));
		Composite composite_main = new Composite(this, SWT.NONE);
		composite_main.setLayout(new FillLayout());
		TimelineViewer timelineViewer = null;
		timelineViewer = new TimelineViewer(composite_main, SWT.NULL, false, jsonPath);
		JsonParserTimeLine jsonParser = new JsonParserTimeLine(jsonPath);
		jsonParser.parserProfilerJson();
		tecTimeLineMap = jsonParser.getTecTimeLineMap();
		ITimeline model = (ITimeline) timelineViewer.getInput();

		ITrack tec0 = model.createTrack("tec0");
		ITrack aiff0 = model.createTrack("aiff0");
		ITrack dma0 = model.createTrack("dma0");
		ITrack tec1 = model.createTrack("tec1");
		ITrack aiff1 = model.createTrack("aiff1");
		ITrack dma1 = model.createTrack("dma1");
		ITrack tec2 = model.createTrack("tec2");
		ITrack aiff2 = model.createTrack("aiff2");
		ITrack dma2 = model.createTrack("dma2");
		ITrack tec3 = model.createTrack("tec3");
		ITrack aiff3 = model.createTrack("aiff3");
		ITrack dma3 = model.createTrack("dma3");

		ILane t0 = tec0.createLane();
		ILane t1 = tec1.createLane();
		ILane t2 = tec2.createLane();
		ILane t3 = tec3.createLane();

		ILane a0 = aiff0.createLane();
		ILane a1 = aiff1.createLane();
		ILane a2 = aiff2.createLane();
		ILane a3 = aiff3.createLane();

		ILane d0 = dma0.createLane();
		ILane d1 = dma1.createLane();
		ILane d2 = dma2.createLane();
		ILane d3 = dma3.createLane();

		List<ILane> lanes = Arrays.asList(t0, a0, d0, t1, a1, d1, t2, a2, d2, t3, a3, d3);

		// each lane with its' event models
		for (int i = 0; i < lanes.size(); i++) {
			List<JsonObject> eventModel = new ArrayList<JsonObject>();
			laneModelMap.put(lanes.get(i), eventModel);
		}

		// prepare data for tecTimeLine view
		JsonObject stampArray = tecTimeLineMap.get(name);
		tecDurationInfoList = new ArrayList<>();
		for (Map.Entry<String, JsonElement> s : stampArray.entrySet()) {
			String stampK = s.getKey();
			for (int i = 1; i <= 4; i++) {
				if (stampK.equals("tec" + (i - 1))) {
					ILane lane = lanes.get(3 * (i - 1));
					JsonElement tecInfo = s.getValue();
					if (((JsonArray) tecInfo).size() != 0) {
						for (int l = 0; l < ((JsonArray) tecInfo).size(); l++) {
							String tips = "";
							JsonObject tecInfoObj = ((JsonArray) tecInfo).get(l).getAsJsonObject();
							for (String key : tecInfoObj.keySet()) {
								String message = key + "=" + tecInfoObj.get(key) + "\n";
								tips = tips + message;
							}
							tips = tips.trim();
							lane.createEvent("", tips, Long.parseLong(tecInfoObj.get("start").toString()),
									Long.parseLong(tecInfoObj.get("duration").toString()), TimeUnit.NANOSECONDS);
							laneModelMap.get(lane).add(tecInfoObj);
						}
					}
				}
				if (stampK.equals("aiff" + (i - 1))) {
					ILane lane = lanes.get((3 * i) - 2);
					JsonElement aiffInfo = s.getValue();
					if (((JsonArray) aiffInfo).size() != 0) {
						for (int l = 0; l < ((JsonArray) aiffInfo).size(); l++) {
							String tips = "";
							JsonObject aiffObj = ((JsonArray) aiffInfo).get(l).getAsJsonObject();
							for (String key : aiffObj.keySet()) {
								String message = key + "=" + aiffObj.get(key) + "\n";
								tips = tips + message;
							}
							tips = tips.trim();
							lane.createEvent("", tips, Long.parseLong(aiffObj.get("start").toString()), Long.parseLong(aiffObj.get("duration").toString()),
									TimeUnit.NANOSECONDS);
							laneModelMap.get(lane).add(aiffObj);
						}
					}
				}
				if (stampK.equals("dma" + (i - 1))) {
					ILane lane = lanes.get((3 * i) - 1);
					JsonElement dmaInfo = s.getValue();
					if (((JsonArray) dmaInfo).size() != 0) {
						for (int l = 0; l < ((JsonArray) dmaInfo).size(); l++) {
							String tips = "";
							JsonObject dmaObj = ((JsonArray) dmaInfo).get(l).getAsJsonObject();
							for (String key : dmaObj.keySet()) {
								String message = key + "=" + dmaObj.get(key) + "\n";
								tips = tips + message;
							}
							tips = tips.trim();
							lane.createEvent("", tips, Long.parseLong(dmaObj.get("start").toString()), Long.parseLong(dmaObj.get("duration").toString()),
									TimeUnit.NANOSECONDS);
							laneModelMap.get(lane).add(dmaObj);
						}
					}
				}
			}
		}
		setTecDurationInfoList(getLargestLane());
		model.createCursor(122, TimeUnit.NANOSECONDS);
		timelineViewer.refresh();
	}

	@Override
	protected void checkSubclass() {
	}

	public static ITimelineEvent getTimeevent() {
		return timeevent;
	}

	public static void setTimeevent(ITimelineEvent timeevent) {
		TecInfoView.timeevent = timeevent;
	}

	private List<JsonObject> getLargestLane() {
		Collection<List> all = laneModelMap.values();
		List<JsonObject> largestLanes = new ArrayList<JsonObject>();
		for (List list : all) {
			if (list.size() > largestLanes.size()) {
				largestLanes = list;
			}
		}
		return largestLanes;
	}

	public static List<JsonObject> getTecDurationInfoList() {
		return tecDurationInfoList;
	}

	public static void setTecDurationInfoList(List<JsonObject> tecDurationInfoList) {
		TecInfoView.tecDurationInfoList = tecDurationInfoList;
	}

}
