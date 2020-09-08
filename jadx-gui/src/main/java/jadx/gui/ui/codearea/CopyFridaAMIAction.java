package jadx.gui.ui.codearea;

import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

import jadx.api.JavaClass;
import jadx.api.JavaField;
import jadx.api.JavaMethod;
import jadx.api.JavaNode;
import jadx.gui.treemodel.*;
import org.jetbrains.annotations.Nullable;

import jadx.gui.ui.UsageDialog;
import jadx.gui.utils.NLS;

import javax.swing.JOptionPane;

public final class CopyFridaAMIAction extends JNodeMenuAction<JNode> {
	private static final long serialVersionUID = 4692546569977976322L;

	public CopyFridaAMIAction(CodeArea codeArea) {
		super(NLS.str("popup.copy_frida_ami_action"), codeArea);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (node == null) {
			JOptionPane.showMessageDialog(null, "Node was null :(", "Error", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		String traceCommand = "";
		String id = "";

		if (node instanceof JMethod) {
			traceCommand = "traceMethod";
			JavaMethod javaMethod = (JavaMethod) node.getJavaNode();
			id = node.getJavaNode().getFullName();
		} else if (node instanceof JField) {
			traceCommand = "traceMethod";
			JavaField javaField = (JavaField) node.getJavaNode();
			id = node.getJavaNode().getFullName() + "*";
		} else if (node instanceof JClass) {
			traceCommand = "trace";
			JavaNode javaNode = node.getJavaNode();
			id = javaNode.getFullName();
			if (javaNode instanceof JavaClass) {
				JavaClass javaClass = (JavaClass) javaNode;
				id = javaClass.getRawName();
			}
		} else if (node instanceof JPackage) {
			JOptionPane.showMessageDialog(null, "Cannot AMI packages :(", "Error", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		boolean isStatic = true;
		int paramCount = 3;
		String[] argTypes = {"WebView", "String", "String"};
		String returnType = "String";

		String output = String.format("// This %s function has %d param%s of type: %s\n",
				isStatic ? "static" : "instanced",
				paramCount,
				paramCount == 1 ? "" : "s",
				String.join(", ", argTypes)
				);
		if (returnType != "") {
			output += String.format("// The function returns %s\n", returnType);
		}
		output += "Java.perform(() => {\n";
		output += "});";

		StringSelection selection = new StringSelection(String.format("%s('%s');", traceCommand, id));
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	@Nullable
	@Override
	public JNode getNodeByOffset(int offset) {
		return codeArea.getJNodeAtOffset(offset);
	}
}
