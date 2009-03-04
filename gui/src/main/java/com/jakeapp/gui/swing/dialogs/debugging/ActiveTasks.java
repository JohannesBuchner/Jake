package com.jakeapp.gui.swing.dialogs.debugging;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.StringUtilities;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author studpete
 */
public class ActiveTasks extends JXPanel {
	private JXList tasks;
	private TaskListModel model = new TaskListModel();
	private static ActiveTasks instance;
	private Timer timer;
	private List<String> history = new ArrayList<String>();

	public ActiveTasks() {
		instance = this;
		initComponents();

		timer = new Timer(50, new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				model.fireUpdate();
			}
		});
	}

	private void initComponents() {
		this.setLayout(new MigLayout("wrap, fill, ins 0"));

		this.tasks = new JXList();
		this.tasks.setHighlighters(HighlighterFactory.createSimpleStriping());
		this.tasks.setModel(model);
		this.tasks.setCellRenderer(new TaskListCellRenderer());

		this.add(tasks, "grow");
	}

	public void updateTasks() {
		try {
			this.model.fireUpdate();
			this.tasks.invalidate();
			this.tasks.updateUI();
		} catch (Exception ex) {
			// don't care
		}
	}

	public static void tasksUpdated() {
		if (instance != null) {
			instance.updateTasks();
		}
	}

	public static void createDialog() {
		JDialog dlg = new JDialog(JakeMainApp.getFrame(), "Jake Background Tasks");
		dlg.add(new ActiveTasks());
		dlg.setMinimumSize(new Dimension(250, 400));
		dlg.pack();
		dlg.setVisible(true);
	}


	private class TaskListModel extends AbstractListModel {
		public TaskListModel() {
		}

		@Override public int getSize() {
			return JakeExecutor.getTasks().size() + history.size();
		}

		@Override public Object getElementAt(int index) {
			int realSize = JakeExecutor.getTasks().size();
			if (index < JakeExecutor.getTasks().size()) {
				return JakeExecutor.getTasks().entrySet().toArray()[index];
			} else {
				return history.get(index - realSize);
			}
		}

		public void fireUpdate() {
			try {
				fireContentsChanged(this, 0, getSize());
			} catch (Exception e) {
				// i can't hear you ;o)
			}
		}
	}

	private static class TaskListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
						// value to display
						int index,		// cell index
						boolean iss,	 // is the cell selected
						boolean chf)	 // the list and the cell have the focus
		{
			String valStr;

			if (value instanceof Map.Entry) {
				Map.Entry<String, Runnable> runner = (Map.Entry<String, Runnable>) value;

				valStr = StringUtilities
								.htmlize(runner.getValue() + "<br> + " + arrayToString(getAllFields(
												runner.getValue().getClass()), "<br>", runner.getValue()));
			} else {
				valStr = value.toString();
			}

			/* The DefaultListCellRenderer class will take care of
								* the JLabels text property, it's foreground and background
								* colors, and so on.
								*/
			super.getListCellRendererComponent(list, valStr, index, iss, chf);
			return this;
		}
	}

	private static Field[] getAllFields(Class<?> clazz) {
		List<Field> allFields = new ArrayList<Field>();
		for (Class<?> superThing : getAllSupers(clazz)) {
			for (Field f : superThing.getDeclaredFields()) {
				allFields.add(f);
			}
		}
		return allFields.toArray(new Field[allFields.size()]);
	}

	private static Set<Class<?>> getAllSupers(Class<?> clazz) {
		Set<Class<?>> result = new HashSet<Class<?>>();
		if (clazz != null) {
			result.add(clazz);
			//result.addAll(getAllSupers(clazz.getSuperclass()));
			//for (Class<?> interfaceClass : clazz.getInterfaces()) {
			//	result.addAll(getAllSupers(interfaceClass));
			//}
		}
		return result;
	}

	private static String arrayToString(Field[] a, String separator, Object inst) {
		StringBuffer result = new StringBuffer();
		if (a.length > 0) {
			result.append(explainField(a[0], inst));
			for (int i = 1; i < a.length; i++) {
				result.append(separator);
				result.append(explainField(a[i], inst));
			}
		}
		return result.toString();
	}

	private static String explainField(Field f, Object classInstance) {
		try {
			if (!f.isAccessible()) {
				f.setAccessible(true);
			}
			return f.getName() + " = <b>" + f.get(classInstance) + "</b>";
		} catch (IllegalAccessException e) {
			return f.toString() + "(illegal)";
		}
	}
}

