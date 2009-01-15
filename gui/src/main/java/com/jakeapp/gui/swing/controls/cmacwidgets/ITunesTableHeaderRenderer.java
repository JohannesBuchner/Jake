package com.jakeapp.gui.swing.controls.cmacwidgets;

import com.explodingpixels.macwidgets.MacColorUtils;
import com.explodingpixels.macwidgets.MacFontUtils;
import com.explodingpixels.macwidgets.MacPainterFactory;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.plaf.EmphasizedLabelUI;
import com.explodingpixels.painter.Painter;
import com.explodingpixels.widgets.WindowUtils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ITunesTableHeaderRenderer extends JComponent
		  implements TableCellRenderer {

	private JTable fTable;

	private JLabel fLabel = MacWidgetFactory.makeEmphasizedLabel(new JLabel(),
			  EmphasizedLabelUI.DEFAULT_FOCUSED_FONT_COLOR, UNFOCUSED_FONT_COLOR,
			  EmphasizedLabelUI.DEFAULT_EMPHASIS_COLOR);

	private int fSelectedColumn = -1;

	private int fPressedColumn = -1;

	private boolean fIsColumnPressed = false;

	private boolean fIsColumnSelected = false;

	private static Color LEFT_BORDER_COLOR = new Color(255, 255, 255, 77);

	private static Color RIGHT_BORDER_COLOR = new Color(0, 0, 0, 51);

	private static Color UNFOCUSED_FONT_COLOR = new Color(0x8f8f8f);

	ITunesTableHeaderRenderer() {
		setOpaque(false);
		setFont(MacFontUtils.ITUNES_TABLE_HEADER_FONT);
		init();
	}

	ITunesTableHeaderRenderer(JTable table) {
		table.getTableHeader().addMouseListener(new HeaderClickHandler());
		fTable = table;
		setLayout(new BorderLayout());
		add(fLabel, BorderLayout.CENTER);
		init();
	}

	private void init() {
		setBorder(BorderFactory.createCompoundBorder(
				  BorderFactory.createMatteBorder(0, 0, 1, 0,
							 MacColorUtils.LEOPARD_BORDER_COLOR),
				  BorderFactory.createEmptyBorder(1, 5, 0, 5)));
	}

	public Component getTableCellRendererComponent(
			  JTable table, Object value, boolean isSelected, boolean hasFocus,
			  int row, int column) {

		int modelColumn = fTable.convertColumnIndexToModel(column);

		if (value != null) fLabel.setText(value.toString());
		fLabel.setFont(fTable.getTableHeader().getFont());
		fIsColumnSelected = isColumnSelected(modelColumn);
		fIsColumnPressed = isColumnPressed(modelColumn);

		return this;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D graphics2d = (Graphics2D) g.create();
		Painter<Component> painter = getBackgroundPainter();
		painter.paint(graphics2d, this, getWidth(), getHeight());

		super.paintComponent(g);

		graphics2d.setColor(LEFT_BORDER_COLOR);
		graphics2d.drawLine(0, 0, 0, getHeight() - getInsets().bottom);
		graphics2d.setColor(RIGHT_BORDER_COLOR);
		graphics2d.drawLine(getWidth() - 1, 0, getWidth() - 1,
				  getHeight() - getInsets().bottom);

		graphics2d.dispose();

	}

	private Painter<Component> getBackgroundPainter() {
		Painter<Component> retVal;
		boolean windowHasFocus = WindowUtils.isParentWindowFocused(this);
		if (windowHasFocus && fIsColumnPressed && fIsColumnSelected) {
			retVal = MacPainterFactory.createIAppPressedSelectedHeaderPainter();
		} else if (windowHasFocus && fIsColumnPressed) {
			retVal = MacPainterFactory.createIAppPressedUnselectedHeaderPainter();
		} else if (windowHasFocus && fIsColumnSelected) {
			retVal = MacPainterFactory.createIAppUnpressedSelectedHeaderPainter();
		} else {
			retVal = MacPainterFactory.createIAppUnpressedUnselectedHeaderPainter();
		}
		return retVal;
	}

	private boolean isColumnSelected(int column) {
		return column == fSelectedColumn;
	}

	private boolean isColumnPressed(int column) {
		return column == fPressedColumn;
	}

	private class HeaderClickHandler extends MouseAdapter {

		private boolean mouseEventIsPerformingPopupTrigger = false;

		public void mouseClicked(MouseEvent mouseEvent) {
			// if the MouseEvent is popping up a context menu, do not sort
			if (mouseEventIsPerformingPopupTrigger) return;

			// if the cursor indicates we're resizing columns, do not sort
			if (fTable.getTableHeader().getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) {
				return;
			}

			final TableColumnModel columnModel = fTable.getColumnModel();
			int viewColumn = columnModel.getColumnIndexAtX(mouseEvent.getX());
			fSelectedColumn = fTable.convertColumnIndexToModel(viewColumn);

			fTable.getTableHeader().repaint();
		}

		public void mousePressed(MouseEvent mouseEvent) {
			this.mouseEventIsPerformingPopupTrigger = mouseEvent.isPopupTrigger();

			if (fTable.getTableHeader().getCursor() != Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) {
				final TableColumnModel columnModel = fTable.getColumnModel();
				int viewColumn = columnModel.getColumnIndexAtX(mouseEvent.getX());
				fPressedColumn = fTable.convertColumnIndexToModel(viewColumn);

				fTable.getTableHeader().repaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			fPressedColumn = -1;
			fTable.getTableHeader().repaint();
		}
	}
}
