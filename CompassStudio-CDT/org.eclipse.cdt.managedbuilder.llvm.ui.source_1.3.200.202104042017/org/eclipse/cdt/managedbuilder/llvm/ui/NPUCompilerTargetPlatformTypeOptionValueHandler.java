package org.eclipse.cdt.managedbuilder.llvm.ui;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IFolderInfo;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;

/**
 * 
 * @author shedu01
 * 
 *         change the lib path according to the target type
 */
//CUSTOMIZATION
public class NPUCompilerTargetPlatformTypeOptionValueHandler implements IManagedOptionValueHandler {

	private static String linktoolId = "cdt.managedbuilder.llvm.aipu.c.linker.base";
	private static String linklibOptionId = "llvm.c.link.option.paths";

	@Override
	public boolean handleValue(IBuildObject configuration, IHoldsOptions holder, IOption option, String extraArgument,
			int event) {


		String eventLabel = "???"; //$NON-NLS-1$

		switch (event) {
		case EVENT_OPEN:
			eventLabel = "EVENT_OPEN"; //$NON-NLS-1$
			break;
		case EVENT_APPLY:
			eventLabel = "EVENT_APPLY";
			String optionValue = option.getValue().toString();
			try {
				if (configuration instanceof IFolderInfo) {
					IFolderInfo con = (IFolderInfo) configuration;
					ITool linktool = null;
					ITool[] tools = con.getTools();
					for (int i = 0; i < tools.length; i++) {
						// tools[i] is a instance, its superclass is the class
						String toolbaseID = tools[i].getSuperClass().getBaseId().toLowerCase();
						String toolparentBaseId = tools[i].getSuperClass().getSuperClass().getBaseId().toLowerCase();

						if (toolbaseID.matches(linktoolId) || toolparentBaseId.matches(linktoolId)) {
							linktool = tools[i];
							break;
						}
					}

					IOption[] linkoptions = linktool.getOptions();
					for (int j = 0; j < linkoptions.length; j++) {
						IOption o = linkoptions[j];
						if (o.getBaseId().toLowerCase().matches(linklibOptionId)) {

							String[] newValue = setlinkLibValue(optionValue, o.getBasicStringListValue());
							con.setOption(linktool, o, newValue);

						}

					}

				}
			} catch (Exception e) {

				e.printStackTrace();
			}

			break;
		case EVENT_SETDEFAULT:
			eventLabel = "EVENT_SETDEFAULT";
			break;
		case EVENT_CLOSE:
			eventLabel = "EVENT_CLOSE";
			break;
		}

	return false;

	}

	@Override
	public boolean isDefaultValue(IBuildObject configuration, IHoldsOptions holder, IOption option,
			String extraArgument) {

		return false;
	}

	@Override
	public boolean isEnumValueAppropriate(IBuildObject configuration, IHoldsOptions holder, IOption option,
			String extraArgument, String enumValue) {

		return true;
	}

	private String[] setlinkLibValue(String targetType, String[] oldvalue) {

		String llvm_lib_path = "${" + LlvmEnvironmentVariableSupplier.ENV_VAR_NAME_LIBRARY_PATH + "}";
		String tmp = "";
		String target = targetType.substring(targetType.lastIndexOf(".") + 1);
		if (target.toLowerCase().matches("z2_\\d\\d\\d\\d")) {
			tmp = llvm_lib_path + File.separator + "z2";
		} else if (target.toLowerCase().matches("z3_\\d\\d\\d\\d")) {
			tmp = llvm_lib_path + File.separator + "z3";
		} else if (target.toLowerCase().matches("x1_\\d\\d\\d\\d")) {
			tmp = llvm_lib_path + File.separator + "x1";
		}

		List<String> newValues = new ArrayList<String>();

		// do not delete user defined lib path
		for (int i = 0; i < oldvalue.length; i++) {
			if (!oldvalue[i].contains(llvm_lib_path)) {
				newValues.add(oldvalue[i]);
			}
		}

		newValues.add(llvm_lib_path);
		newValues.add(tmp);

		return newValues.toArray(new String[0]);

	}


}
