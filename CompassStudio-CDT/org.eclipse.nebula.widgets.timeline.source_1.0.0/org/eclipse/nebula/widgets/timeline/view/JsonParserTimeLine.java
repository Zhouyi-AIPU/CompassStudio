package org.eclipse.nebula.widgets.timeline.view;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @ClassName: JsonParserTimeLine
 *
 * @Description:prepare date for multi-core profiler timeLine view by parser profile.json
 *
 * @author Alan Chen
 */
public class JsonParserTimeLine {
	private JsonObject json;
	private Set<Entry<String, JsonElement>> entrySet = null;
	public Map<String, Map<String, String>> subGraphMap;
	public Map<String, JsonObject> tecTimeLineMap;
	public String target = "";

	public Map<String, JsonObject> getTecTimeLineMap() {
		return tecTimeLineMap;
	}

	public void setTecTimeLineMap(Map<String, JsonObject> tecTimeLineMap) {
		this.tecTimeLineMap = tecTimeLineMap;
	}

	public static List<TenerImp> tenerList;

	public String hardwareJsonPath;

	public List<TenerImp> getTenerList() {
		return tenerList;
	}

	public void setTenerList(List<TenerImp> tenerList) {
		JsonParserTimeLine.tenerList = tenerList;
	}

	public JsonParserTimeLine(String path) {
		this.hardwareJsonPath = path;
	}

	public Map<String, Map<String, String>> getSubGraphMap() {
		return subGraphMap;
	}

	public void setSubGraphMap(Map<String, Map<String, String>> subGraphMap) {
		this.subGraphMap = subGraphMap;
	}

	/*
	 * parser profiler data of profile.json
	 */
	public void parserProfilerJson() {
		JsonParser parse = new JsonParser();
		tenerList = new ArrayList<>();

		try {
			json = (JsonObject) parse.parse(new FileReader(hardwareJsonPath));
			entrySet = json.getAsJsonObject().entrySet();
			subGraphMap = new HashMap<>();
			tecTimeLineMap = new HashMap<>();
			for (Map.Entry<String, JsonElement> entry : entrySet) {

				Object obj = json.get(entry.getKey());
				if (obj instanceof JsonObject) {
					JsonObject keyData = json.get(entry.getKey()).getAsJsonObject();
					for (Map.Entry<String, JsonElement> e : keyData.entrySet()) {
						// get target
						if (e.getKey().equals("Target")) {
							target = e.getValue().getAsString();
						}
						JsonElement JE = e.getValue();
						if (JE instanceof JsonArray) {
							for (int i = 0; i < ((JsonArray) JE).size(); i++) {
								JsonObject oo = ((JsonArray) JE).get(i).getAsJsonObject();
								for (Map.Entry<String, JsonElement> o : oo.entrySet()) {
									String k = o.getKey();
									if (k.equals("nodes")) {
										JsonElement nodeInfo = o.getValue();
										for (int y = 0; y < ((JsonArray) nodeInfo).size(); y++) {
											JsonObject nodeInfoObj = ((JsonArray) nodeInfo).get(y).getAsJsonObject();
											TenerImp tenser = new TenerImp();

											// parser subgraph timeLine view data
											tenser.setCycles(Long.parseLong(nodeInfoObj.get("Cycles").toString()));
											tenser.setStart(Long.parseLong(nodeInfoObj.get("start").toString()));
											tenser.setCoreId(nodeInfoObj.get("core_id").toString());
											tenser.setLayerName(nodeInfoObj.get("Layer Name").toString());
											tenser.setEnd(nodeInfoObj.get("end").toString());

											tenser.setAIPU_Read_Bytes(Long.parseLong(nodeInfoObj.get("AIPU Read Bytes").toString()));
											tenser.setAIPU_Write_Bytes(Long.parseLong(nodeInfoObj.get("AIPU Write Bytes").toString()));
											tenser.setBW_RW(Double.parseDouble(nodeInfoObj.get("BW_RW(GB/s)").toString()));
											tenser.setBW_R(Double.parseDouble(nodeInfoObj.get("BW_R(GB/s)").toString()));
											tenser.setBW_W(Double.parseDouble(nodeInfoObj.get("BW_W(GB/s)").toString()));
											tenser.setMAC_Utilization(Double.parseDouble(nodeInfoObj.get("MAC Utilization").toString()));
											tenser.setInput_Shapes(nodeInfoObj.get("Input Shapes").toString());
											tenser.setOutput_Shapes(nodeInfoObj.get("Output Shapes").toString());

											tenerList.add(tenser);

											// parser subgraph profiler data for properties view
											Map<String, String> subGraphInfoMap = new HashMap<>();
											for (Map.Entry<String, JsonElement> e1 : nodeInfoObj.entrySet()) {
												String key = e1.getKey();
												if (key.equals("stamp")) {
													continue;
												}
												JsonElement val = e1.getValue();
												subGraphInfoMap.put(key, val.toString());
											}

											subGraphMap.put(nodeInfoObj.get("Layer Name").toString(), subGraphInfoMap);
											// parser tecInfo profiler data for tecInfoTimeLine view
											JsonObject tecInfo = nodeInfoObj.get("stamp").getAsJsonObject();
											tecTimeLineMap.put(nodeInfoObj.get("Layer Name").toString(), tecInfo);

										}
									}
								}
							}

						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// set subgraph properties map data
		setSubGraphMap(subGraphMap);
		// set subgraph timeLine view map data
		setTenerList(tenerList);
		// set tec timeLine view map data
		setTecTimeLineMap(tecTimeLineMap);
	}

}
