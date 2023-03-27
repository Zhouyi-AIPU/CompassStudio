package org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.launch;

import java.util.Map;

import org.eclipse.cdt.debug.ui.IPinProvider.IPinElementColorDescriptor;
import org.eclipse.cdt.dsf.debug.service.IProcesses.IThreadDMData;
import org.eclipse.cdt.dsf.debug.ui.viewmodel.launch.ExecutionContextLabelText;
import org.eclipse.cdt.dsf.debug.ui.viewmodel.launch.ILaunchVMConstants;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IPropertiesUpdate;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelAttribute;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelColumnInfo;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelImage;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelText;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.PropertiesBasedLabelProvider;
import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;

import cn.com.armchina.toolchain.core.dsf.service.INPUThreadDMData;

/**
 * @ClassName NPUTecVMNode.java
 *
 * @Description:
 *
 * @author shedu01
 *
 */
public class NPUTecVMNode extends ThreadVMNode {


	public NPUTecVMNode(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session);
	}

	protected IElementLabelProvider createLabelProvider() {
		PropertiesBasedLabelProvider provider = new PropertiesBasedLabelProvider();

		provider.setColumnInfo(PropertiesBasedLabelProvider.ID_COLUMN_NO_COLUMNS, new LabelColumnInfo(new LabelAttribute[] {
				// Text is made of the thread name followed by its state and state change reason.
				new GdbExecutionContextLabelText(MessagesForGdbLaunchVM.NPUTecInfoVMNode_No_columns__text_format,
						new String[] { ExecutionContextLabelText.PROP_NAME_KNOWN, PROP_NAME, ExecutionContextLabelText.PROP_ID_KNOWN,
								ILaunchVMConstants.PROP_ID, IGdbLaunchVMConstants.PROP_OS_ID_KNOWN, IGdbLaunchVMConstants.PROP_OS_ID,
								IGdbLaunchVMConstants.PROP_CORES_ID_KNOWN, IGdbLaunchVMConstants.PROP_CORES_ID, ILaunchVMConstants.PROP_IS_SUSPENDED,
								ExecutionContextLabelText.PROP_STATE_CHANGE_REASON_KNOWN, ILaunchVMConstants.PROP_STATE_CHANGE_REASON,
								ExecutionContextLabelText.PROP_STATE_CHANGE_DETAILS_KNOWN, ILaunchVMConstants.PROP_STATE_CHANGE_DETAILS,
						}),
				new LabelText(MessagesForGdbLaunchVM.ThreadVMNode_No_columns__Error__label, new String[0]),
				/* RUNNING THREAD - RED PIN */
				new LabelImage(CDTSharedImages.getImageDescriptor(CDTSharedImages.IMG_THREAD_RUNNING_R_PINNED)) {
					{
						setPropertyNames(new String[] { ILaunchVMConstants.PROP_IS_SUSPENDED, IGdbLaunchVMConstants.PROP_PINNED_CONTEXT,
								IGdbLaunchVMConstants.PROP_PIN_COLOR });
					}

					@Override
					public boolean isEnabled(IStatus status, Map<String, Object> properties) {
						Boolean prop = (Boolean) properties.get(ILaunchVMConstants.PROP_IS_SUSPENDED);
						Boolean pin_prop = (Boolean) properties.get(IGdbLaunchVMConstants.PROP_PINNED_CONTEXT);
						Object pin_color_prop = properties.get(IGdbLaunchVMConstants.PROP_PIN_COLOR);
						return (prop != null && pin_prop != null && pin_color_prop != null)
								? !prop.booleanValue() && pin_prop.booleanValue() && pin_color_prop.equals(IPinElementColorDescriptor.RED)
								: false;
					}
				},
				/* RUNNING THREAD - GREEN PIN */
				new LabelImage(CDTSharedImages.getImageDescriptor(CDTSharedImages.IMG_THREAD_RUNNING_G_PINNED)) {
					{
						setPropertyNames(new String[] { ILaunchVMConstants.PROP_IS_SUSPENDED, IGdbLaunchVMConstants.PROP_PINNED_CONTEXT,
								IGdbLaunchVMConstants.PROP_PIN_COLOR });
					}

					@Override
					public boolean isEnabled(IStatus status, Map<String, Object> properties) {
						Boolean prop = (Boolean) properties.get(ILaunchVMConstants.PROP_IS_SUSPENDED);
						Boolean pin_prop = (Boolean) properties.get(IGdbLaunchVMConstants.PROP_PINNED_CONTEXT);
						Object pin_color_prop = properties.get(IGdbLaunchVMConstants.PROP_PIN_COLOR);
						return (prop != null && pin_prop != null && pin_color_prop != null)
								? !prop.booleanValue() && pin_prop.booleanValue() && pin_color_prop.equals(IPinElementColorDescriptor.GREEN)
								: false;
					}
				},
				/* RUNNING THREAD - BLUE PIN */
				new LabelImage(CDTSharedImages.getImageDescriptor(CDTSharedImages.IMG_THREAD_RUNNING_B_PINNED)) {
					{
						setPropertyNames(new String[] { ILaunchVMConstants.PROP_IS_SUSPENDED, IGdbLaunchVMConstants.PROP_PINNED_CONTEXT,
								IGdbLaunchVMConstants.PROP_PIN_COLOR });
					}

					@Override
					public boolean isEnabled(IStatus status, Map<String, Object> properties) {
						Boolean prop = (Boolean) properties.get(ILaunchVMConstants.PROP_IS_SUSPENDED);
						Boolean pin_prop = (Boolean) properties.get(IGdbLaunchVMConstants.PROP_PINNED_CONTEXT);
						Object pin_color_prop = properties.get(IGdbLaunchVMConstants.PROP_PIN_COLOR);
						return (prop != null && pin_prop != null && pin_color_prop != null)
								? !prop.booleanValue() && pin_prop.booleanValue() && pin_color_prop.equals(IPinElementColorDescriptor.BLUE)
								: false;
					}
				},
				/* RUNNING THREAD - NO PIN */
				new LabelImage(DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_OBJS_THREAD_RUNNING)) {
					{
						setPropertyNames(new String[] { ILaunchVMConstants.PROP_IS_SUSPENDED });
					}

					@Override
					public boolean isEnabled(IStatus status, java.util.Map<String, Object> properties) {
						// prop has been seen to be null during session shutdown [313823]
						Boolean prop = (Boolean) properties.get(ILaunchVMConstants.PROP_IS_SUSPENDED);
						return (prop != null) ? !prop.booleanValue() : false;
					}
				},
				/* SUSPENDED THREAD - RED PIN */
				new LabelImage(CDTSharedImages.getImageDescriptor(CDTSharedImages.IMG_THREAD_SUSPENDED_R_PINNED)) {
					{
						setPropertyNames(new String[] { IGdbLaunchVMConstants.PROP_PINNED_CONTEXT, IGdbLaunchVMConstants.PROP_PIN_COLOR });
					}

					@Override
					public boolean isEnabled(IStatus status, Map<String, Object> properties) {
						Boolean pin_prop = (Boolean) properties.get(IGdbLaunchVMConstants.PROP_PINNED_CONTEXT);
						Object pin_color_prop = properties.get(IGdbLaunchVMConstants.PROP_PIN_COLOR);
						return (pin_prop != null && pin_color_prop != null)
								? pin_prop.booleanValue() && pin_color_prop.equals(IPinElementColorDescriptor.RED)
								: false;
					}
				},
				/* SUSPENDED THREAD - GREEN PIN */
				new LabelImage(CDTSharedImages.getImageDescriptor(CDTSharedImages.IMG_THREAD_SUSPENDED_G_PINNED)) {
					{
						setPropertyNames(new String[] { IGdbLaunchVMConstants.PROP_PINNED_CONTEXT, IGdbLaunchVMConstants.PROP_PIN_COLOR });
					}

					@Override
					public boolean isEnabled(IStatus status, Map<String, Object> properties) {
						Boolean pin_prop = (Boolean) properties.get(IGdbLaunchVMConstants.PROP_PINNED_CONTEXT);
						Object pin_color_prop = properties.get(IGdbLaunchVMConstants.PROP_PIN_COLOR);
						return (pin_prop != null && pin_color_prop != null)
								? pin_prop.booleanValue() && pin_color_prop.equals(IPinElementColorDescriptor.GREEN)
								: false;
					}
				},
				/* SUSPENDED THREAD - BLUE PIN */
				new LabelImage(CDTSharedImages.getImageDescriptor(CDTSharedImages.IMG_THREAD_SUSPENDED_B_PINNED)) {
					{
						setPropertyNames(new String[] { IGdbLaunchVMConstants.PROP_PINNED_CONTEXT, IGdbLaunchVMConstants.PROP_PIN_COLOR });
					}

					@Override
					public boolean isEnabled(IStatus status, Map<String, Object> properties) {
						Boolean pin_prop = (Boolean) properties.get(IGdbLaunchVMConstants.PROP_PINNED_CONTEXT);
						Object pin_color_prop = properties.get(IGdbLaunchVMConstants.PROP_PIN_COLOR);
						return (pin_prop != null && pin_color_prop != null)
								? pin_prop.booleanValue() && pin_color_prop.equals(IPinElementColorDescriptor.BLUE)
								: false;
					}
				},
				/* SUSPENDED THREAD - NO PIN */
				new LabelImage(DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_OBJS_THREAD_SUSPENDED)) {
					{
						setPropertyNames(new String[] { ExecutionContextLabelText.PROP_ID_KNOWN });
					}

					@Override
					public boolean isEnabled(IStatus status, Map<String, Object> properties) {
						// X1 don't show tec node
						String known = (String) properties.get(ExecutionContextLabelText.PROP_ID_KNOWN);
						if (known != null && known.equals("0"))
							return false;
						return true;
					}
				},
		}));
		return provider;
	}

	@Override
	protected void fillThreadDataProperties(IPropertiesUpdate update, IThreadDMData data) {
		if (data.getName() != null && data.getName().length() > 0) {
			update.setProperty(PROP_NAME, data.getName());
		}
		update.setProperty(IGdbLaunchVMConstants.PROP_OS_ID, data.getId());

		if (data instanceof INPUThreadDMData) {
			INPUThreadDMData npuDMData = (INPUThreadDMData) data;
			if (npuDMData.getTecID().equals("") || npuDMData.getWorkGroup().equals("") || npuDMData.getWorkItem().equals("")) {
				// X1 don't show tec node
				update.setProperty(ILaunchVMConstants.PROP_ID, "");
				update.setProperty(ExecutionContextLabelText.PROP_ID_KNOWN, "0");

				return;

			}

			update.setProperty(ILaunchVMConstants.PROP_ID, getTecinfo(npuDMData.getTecID()));
		}
	}

	private String getTecinfo(String tecinfo) {
		String lastStr = "";
		if (tecinfo != null && !tecinfo.equals("")) {
			tecinfo = tecinfo.substring(1);
			tecinfo = tecinfo.substring(0, tecinfo.length() - 1);
			String[] vals = tecinfo.split(",");
			lastStr = "#Cluster:" + vals[0] + "  Core:" + vals[1] + "  Tec:" + vals[2];
		}
		return lastStr;
	}

}
