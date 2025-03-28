package org.eclipse.nebula.widgets.timeline.view;

/**
 * @ClassName: TenerImp
 *
 * @Description:define subgraph timeLine event
 *
 * @author Alan Chen
 */
public class TenerImp {
	public String coreId;
	public Long cycles;
	public Long start;
	public String end;
	public String layerName;
	public String Input_Shapes;
	public String Output_Shapes;
	public Long AIPU_Read_Bytes;
	public Long AIPU_Write_Bytes;
	public double BW_RW;
	public double BW_R;
	public double BW_W;
	public double MAC_Utilization;

	public String getOutput_Shapes() {
		return Output_Shapes;
	}

	public void setOutput_Shapes(String output_Shapes) {
		Output_Shapes = output_Shapes;
	}

	public String getInput_Shapes() {
		return Input_Shapes;
	}

	public void setInput_Shapes(String input_Shapes) {
		Input_Shapes = input_Shapes;
	}

	public Long getAIPU_Read_Bytes() {
		return AIPU_Read_Bytes;
	}

	public void setAIPU_Read_Bytes(Long aIPU_Read_Bytes) {
		AIPU_Read_Bytes = aIPU_Read_Bytes;
	}

	public Long getAIPU_Write_Bytes() {
		return AIPU_Write_Bytes;
	}

	public void setAIPU_Write_Bytes(Long aIPU_Write_Bytes) {
		AIPU_Write_Bytes = aIPU_Write_Bytes;
	}

	public double getBW_RW() {
		return BW_RW;
	}

	public void setBW_RW(double bW_rw) {
		BW_RW = bW_rw;
	}

	public double getBW_R() {
		return BW_R;
	}

	public void setBW_R(double bW_R) {
		BW_R = bW_R;
	}

	public double getBW_W() {
		return BW_W;
	}

	public void setBW_W(double bW_W) {
		BW_W = bW_W;
	}

	public double getMAC_Utilization() {
		return MAC_Utilization;
	}

	public void setMAC_Utilization(double mAC_Utilization) {
		MAC_Utilization = mAC_Utilization;
	}

	/**
	 * @param coreId
	 *            the coreId to set
	 */
	public void setCoreId(String coreId) {
		this.coreId = coreId;
	}

	/**
	 * @param cycles
	 *            the cycles to set
	 */
	public void setCycles(Long cycles) {
		this.cycles = cycles;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(Long start) {
		this.start = start;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 * @param layerName
	 *            the layerName to set
	 */
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	/**
	 * @return the coreId
	 */
	public String getCoreId() {
		return coreId;
	}

	/**
	 * @return the cycles
	 */
	public Long getCycles() {
		return cycles;
	}

	/**
	 * @return the start
	 */
	public Long getStart() {
		return start;
	}

	/**
	 * @return the end
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * @return the layerName
	 */
	public String getLayerName() {
		return layerName;
	}
}
