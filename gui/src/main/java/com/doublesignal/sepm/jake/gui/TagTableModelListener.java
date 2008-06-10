package com.doublesignal.sepm.jake.gui;

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

import org.apache.log4j.Logger;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 31, 2008
 * Time: 4:36:55 PM
 */
public class TagTableModelListener implements TableModelListener
{

	private static final Logger log = Logger.getLogger(StatusPanel.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
	
	private IJakeGuiAccess jakeGuiAccess;
	/**
	 * The list of JakeObjects used to create the TableModel
	 */
	private List<JakeObject> jakeObjects;
	/**
	 * Column-Position of the JakeObjectName in the TableModel
	 */
	private int iJakeObjectIdPosition;
	/**
	 * Column-Position of the Tags-Column in the TableModel
	 */
	private int iTagsPosition;

	public TagTableModelListener(IJakeGuiAccess jakeGuiAccess, List<JakeObject> jakeObjects, int iJakeObjectIdPosition, int iTagsPosition)
	{
		this.jakeGuiAccess = jakeGuiAccess;
		this.jakeObjects = jakeObjects;
		this.iJakeObjectIdPosition = iJakeObjectIdPosition;
		this.iTagsPosition = iTagsPosition;
	}

	public void tableChanged(TableModelEvent event)
	{
		if (event.getType() == TableModelEvent.UPDATE && event.getColumn() == iTagsPosition)
		{
			log.debug("handling a tag-change event");
			TableModel tm = (TableModel) event.getSource();
			String jakeObjectName = (String) tm.getValueAt(event.getFirstRow(), iJakeObjectIdPosition);
			String tags = (String) tm.getValueAt(event.getFirstRow(), event.getColumn());
			JakeObject joFound = null;
			log.debug("searching for a jakeObject");
			for (JakeObject obj : jakeObjects)
			{
				if (obj.getName().equals(jakeObjectName))
				{
					joFound = obj;
					break;
				}
			}
			if (joFound != null)
			{

				log.debug("adding tags to jakeObject");
				String[] tagsArray = tags.split("[,\\s]");
				for (String sTag : tagsArray)
				{
					if (sTag.equals(",") || sTag.equals(" "))
					{
						continue;
					}

					Tag tTag = null;
					try
					{
						tTag = new Tag(sTag);
						if (!joFound.getTags().contains(tTag))
						{
							jakeGuiAccess.addTag(joFound, tTag);
						}
					}
					catch (InvalidTagNameException e)
					{
						log.debug("cought an InvalidTagNameException but ignoring "
								+ "it, because it will simply not show up in the gui" +
								" tagname is: " + sTag);
					}
				}

				// remove the non existend tags from the jakeObject

				List<String> tagsFromArray = Arrays.asList(tagsArray);

				log.debug("removing tags from jakeObject");
				Tag[] foundTags = joFound.getTags().toArray(new Tag[joFound.getTags().size()]);
				for (Tag tag : foundTags)
				{
					if (!tagsFromArray.contains(tag.toString()))
					{
						jakeGuiAccess.removeTag(joFound, tag);
					}
				}

				log.debug("creating new tag string");
				String sTags = "";
				Set<Tag> objTags = joFound.getTags();
				for (Tag tag : objTags)
				{
					sTags = tag.toString() + ((!sTags.isEmpty()) ? ", " + sTags : "");
				}

				log.debug("dispatching listener");
				tm.removeTableModelListener(this);
				log.debug("setting value");
				tm.setValueAt(sTags, event.getFirstRow(), event.getColumn());
				log.debug("attaching listener");
				tm.addTableModelListener(this);
			}
			else
			{
				log.debug("found no matching jakeObject");
			}
		}
	}
}
