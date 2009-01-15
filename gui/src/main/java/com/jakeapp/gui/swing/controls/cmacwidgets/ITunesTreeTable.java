package com.jakeapp.gui.swing.controls.cmacwidgets;

import com.explodingpixels.data.Rating;
import com.explodingpixels.macwidgets.ITunesRatingTableCellRenderer;
import com.explodingpixels.macwidgets.MacFontUtils;
import com.explodingpixels.widgets.TableUtils;
import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ITunesTreeTable extends JXTreeTable {

	private static final Color ITUNES_SCROLLPANE_BORDER_COLOR = new Color(0x555555);
	private static final Color ITUNES_TABLE_SELECTION_ACTIVE_FOREGROUND_COLOR = Color.WHITE;
	private static final Color ITUNES_TABLE_SELECTION_INACTIVE_FOREGROUND_COLOR = Color.BLACK;
	private static final Color ITUNES_TABLE_SELECTION_ACTIVE_BACKGROUND_COLOR = new Color(0x3d80df);
	private static final Color ITUNES_TABLE_SELECTION__INACTIVE_BACKGROUND_COLOR = new Color(192, 192, 192);
	private static final Color ITUNES_TABLE_GRID_COLOR = new Color(0xd9d9d9);
	private static final Color ITUNES_TABLE_SELECTED_FOCUSED_GRID_COLOR = new Color(0x346dbe);
	private static final Color ITUNES_TABLE_SELECTED_UNFOCUSED_GRID_COLOR = new Color(0xacacac);
	private static final Color ITUNES_TABLE_SELECTION_ACTIVE_BORDER_COLOR = new Color(125, 170, 234);
	private static final Color ITUNES_TABLE_SELECTION_INACTIVE_BORDER_COLOR = new Color(224, 224, 224);
	private static final Color ITUNES_ROW_COLOR = new Color(241, 245, 250);
	private static final Color ITUNES_RATING_DOT_SELECTED_INACTIVE_COLOR = new Color(0x999999);
	private static final Color ITUNES_RATING_DOT_SELECTED_ACTIVE_COLOR = new Color(255, 255, 255, 150);

	private final ITunesTableHeaderRenderer fHeaderRenderer =
			  new ITunesTableHeaderRenderer(this);

	public ITunesTreeTable() {
		super();
		init();
	}

	private void init() {
		setFont(MacFontUtils.ITUNES_FONT);
		setShowVerticalLines(true);
		setShowHorizontalLines(false);
		setGridColor(ITUNES_TABLE_GRID_COLOR);
		setRowHeight(17);
		setIntercellSpacing(new Dimension(0, 0));
		adjustColumnWidths();
		installCellRenderers();
		getTableHeader().setDefaultRenderer(fHeaderRenderer);

		addFocusListener(createFocusListener());

	}

	/**
	 * Creates a {@link java.awt.event.FocusListener} that repaints the selection on focus
	 * gained and focus lost events.
	 *
	 * @return a {@code FocusListener} that repaints the selecion on focus state
	 *         changes.
	 */
	private FocusListener createFocusListener() {
		return new FocusListener() {
			public void focusGained(FocusEvent e) {
				TableUtils.repaintSelection(ITunesTreeTable.this);
			}

			public void focusLost(FocusEvent e) {
				TableUtils.repaintSelection(ITunesTreeTable.this);
			}
		};
	}

	private void adjustColumnWidths() {
//        TableColumn ratingColumn = getColumn("Rating");
//        ratingColumn.setPreferredWidth(78);
//        ratingColumn.setResizable(false);
	}

	private void installCellRenderers() {
		setDefaultRenderer(Rating.class, new ITunesRatingTableCellRenderer());
	}

	private Color getRowColor(int row) {
		return row % 2 == 0
				  ? ITUNES_ROW_COLOR : getBackground();
	}

	@Override
	protected void configureEnclosingScrollPane() {
		super.configureEnclosingScrollPane();
		Container p = getParent();

		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				ITunesTableHeaderRenderer renderer = new ITunesTableHeaderRenderer();
				renderer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
						  ITUNES_SCROLLPANE_BORDER_COLOR));
				scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, renderer);
				scrollPane.setBorder(BorderFactory.createEmptyBorder());
			}
		}
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component component = super.prepareRenderer(renderer, row, column);

		// determine if the row is selected.
		boolean rowSelected = isRowSelected(row);

		// if the row ins't selected, override the background color.
		if (!rowSelected) {
			component.setBackground(getRowColor(row));
		}

		// if the cell renderer is a JComponent, set its border.
		if (component instanceof JComponent) {
			JComponent jComponent = (JComponent) component;

			// create a border to pad the left and right sides of a cell.
			Border spacer = BorderFactory.createEmptyBorder(0, 5, 0, 5);

			// if the row is selected and this table has foucs, use the light
			//   blue to draw a single line border below the component.
			// else if the row is selected and this table does not have focus,
			//   use a light gray to draw a single line border below the
			//   component.
			// else use the spacer border.
			if (rowSelected && hasFocus()) {
				Border rightBorder = BorderFactory.createMatteBorder(0, 0, 0, 1,
						  ITUNES_TABLE_SELECTED_FOCUSED_GRID_COLOR);
				Border compoundBorder = BorderFactory.createCompoundBorder(rightBorder, spacer);
				jComponent.setBorder(BorderFactory.createCompoundBorder(
						  BorderFactory.createMatteBorder(0, 0, 1, 0,
									 ITUNES_TABLE_SELECTION_ACTIVE_BORDER_COLOR),
						  compoundBorder));
			} else if (rowSelected) {
				Border rightBorder = BorderFactory.createMatteBorder(0, 0, 0, 1,
						  ITUNES_TABLE_SELECTED_UNFOCUSED_GRID_COLOR);
				Border compoundBorder = BorderFactory.createCompoundBorder(rightBorder, spacer);
				jComponent.setBorder(BorderFactory.createCompoundBorder(
						  BorderFactory.createMatteBorder(0, 0, 1, 0,
									 ITUNES_TABLE_SELECTION_INACTIVE_BORDER_COLOR),
						  compoundBorder));
			} else {
				Border rightBorder = BorderFactory.createMatteBorder(0, 0, 0, 1,
						  ITUNES_TABLE_SELECTION_INACTIVE_BORDER_COLOR);
				Border compoundBorder = BorderFactory.createCompoundBorder(rightBorder, spacer);
				jComponent.setBorder(compoundBorder);
			}

		}

		return component;
	}

	@Override
	public Color getSelectionForeground() {
		return hasFocus()
				  ? ITUNES_TABLE_SELECTION_ACTIVE_FOREGROUND_COLOR
				  : ITUNES_TABLE_SELECTION_INACTIVE_FOREGROUND_COLOR;
	}

	@Override
	public Color getSelectionBackground() {
		return hasFocus()
				  ? ITUNES_TABLE_SELECTION_ACTIVE_BACKGROUND_COLOR
				  : ITUNES_TABLE_SELECTION__INACTIVE_BACKGROUND_COLOR;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		// change the clip, as the table painting pipeline tries to constrict
		// the clip bounds, which would negate our painting below.
		g.setClip(0, 0, getWidth(), getHeight());
		// paint the empty row backgrounds, if any need to be painted.
		paintEmptyRows(g);

	}

	protected void paintEmptyRows(Graphics g) {
		Graphics newGraphics = g.create();

		// grab the y coordinate of the top of the first non-existent row (also
		// can be thought of as the bottom of the last row).
		int firstNonExistentRowY = getRowCount() * getRowHeight();

		// only paint the region within the clipp bounds.
		Rectangle clip = newGraphics.getClipBounds();

		// iterate over each non-existent row, if any exist, painting the
		// appropriate background color for each.
		for (int y = firstNonExistentRowY; y < getSize().height; y += getRowHeight()) {
			int row = y / getRowHeight();
			newGraphics.setColor(getRowColor(row));
			newGraphics.fillRect(clip.x, y, clip.width, getRowHeight());
		}

		// paint the column grid dividers for the non-existent rows.
		int x = 0;
		for (int i = 0; i < getColumnCount(); i++) {
			TableColumn column = getColumnModel().getColumn(i);
			// increase the x position by the width of the current column.
			x += column.getWidth();
			newGraphics.setColor(ITUNES_TABLE_GRID_COLOR);
			// draw the grid line (not sure what the -1 is for, but BasicTableUI
			// also does it.
			newGraphics.drawLine(x - 1, firstNonExistentRowY, x - 1, getHeight());
		}

		newGraphics.dispose();
	}

	///////////////////////////////////////////////////////////////////////////
	// Scrollable interface overrides.
	///////////////////////////////////////////////////////////////////////////

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return getParent() instanceof JViewport
				  && getPreferredSize().height < getParent().getHeight();
	}

}