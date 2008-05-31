package com.doublesignal.sepm.jake.gui;

import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchJakeObjectException;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Date;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 31, 2008
 * Time: 5:09:20 PM
 */
public class FilesPanel extends JPanel
{
	JakeGui jakeGui;
	private static Logger log = Logger.getLogger(FilesPanel.class);
	private JPanel filesPanel;
	private JScrollPane filesScrollPane;
	private JXTable filesTable;
	private IJakeGuiAccess jakeGuiAccess;
	public FilesPanel(JakeGui jakeGui)
	{
		this.jakeGui = jakeGui;
		this.jakeGuiAccess = jakeGui.getJakeGuiAccess();
		initComponents();
	}

	public FilterPipeline getFilters() {return filesTable.getFilters();}

	public void setFilters(FilterPipeline filterPipeline) {filesTable.setFilters(filterPipeline);}

	private void initComponents()
	{
		filesPanel = new JPanel();
		filesScrollPane = new JScrollPane();
		filesTable = new JXTable();

								{
							filesPanel.setLayout(new BorderLayout());

							//======== filesScrollPane ========
							{
								//---- filesTable ----
//								filesTable.setComponentPopupMenu(filesPopupMenu); // TODO!!!
								filesTable.setColumnControlVisible(true);
								filesTable.setHighlighters(HighlighterFactory.createSimpleStriping());


								String[] fileListCaptions = new String[]{
										"Name", "Size", "Tags", "Sync Status", "Last Changed", "User"
								};


								DefaultTableModel fileListTableModel = new DefaultTableModel(
										fileListCaptions, 0

								)
								{
									boolean[] columnEditable = new boolean[]{
											false, false, true, false, false, false
									};

									@Override
									public boolean isCellEditable(int rowIndex, int columnIndex)
									{
										return columnEditable[columnIndex];
									}


								};


								java.util.List<JakeObject> files = null;
								try
								{
									files = jakeGuiAccess.getJakeObjectsByPath("/");
								}
								catch (NoSuchJakeObjectException e)
								{
									log.warn("Got a NoSuchJakeObjectException from jakeGuiAccess");
									//e.printStackTrace();
								}

								ProjectMember pmLastModifier;
								Date dLastModified;
								long lFileSize;
								String sFileSizeUnity;
								String sLastModifier;
								String sOnlineStatus;
								String sTags;

								for (JakeObject obj : files)
								{
									lFileSize = jakeGuiAccess.getFileSize((FileObject) obj);

									sFileSizeUnity = "Bytes";
									if (lFileSize > 1024)
									{
										lFileSize /= 1024;
										sFileSizeUnity = "KB";
									}
									if (lFileSize > 1024)
									{
										lFileSize /= 1024;
										sFileSizeUnity = "MB";
									}
									if (lFileSize > 1024)
									{
										lFileSize /= 1024;
										sFileSizeUnity = "GB";
									}

									pmLastModifier = jakeGuiAccess.getLastModifier(obj);
									dLastModified = jakeGuiAccess.getLastModified(obj);

									sLastModifier = (pmLastModifier.getNickname().isEmpty()) ?
											pmLastModifier.getUserId()
											:
											pmLastModifier.getNickname();

									sTags = "";
									for (Tag tag : obj.getTags())
									{
										sTags = tag.toString() + ((!sTags.isEmpty()) ? ", " + sTags : "");
									}
									sOnlineStatus = "online";

									fileListTableModel.addRow(
											new String[]{
													obj.getName(),
													lFileSize + " " + sFileSizeUnity,
													sTags,
													sOnlineStatus,
													dLastModified.toString(),
													sLastModifier
											});
								}


								TagTableModelListener TagfileListTableModelListener = new TagTableModelListener(
										jakeGuiAccess,
										files,
										0,
										2);

								fileListTableModel.addTableModelListener(TagfileListTableModelListener);


								filesTable.setModel(fileListTableModel);
								{
									TableColumnModel cm = filesTable.getColumnModel();
									cm.getColumn(0).setPreferredWidth(245);
									cm.getColumn(1).setPreferredWidth(50);
									cm.getColumn(2).setPreferredWidth(75);
								}
								filesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
								filesTable.setPreferredScrollableViewportSize(new Dimension(450, 379));
								filesScrollPane.setViewportView(filesTable);
							}
							filesPanel.add(filesScrollPane, BorderLayout.CENTER);
						}
	}


}
