package com.jakeapp.gui.swing.controls;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import javax.swing.table.TableCellRenderer;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import java.awt.*;

import com.jakeapp.gui.swing.helpers.TagHighlighter;
import com.jakeapp.gui.swing.helpers.TagSearcher;

/**
 * Editor of a TagSet within a table cell
 * <p/>
 * ATTENTION: THIS IS A CONSTRUCTION SITE! WEAR HARD HATS AT ALL TIMES!
 */
public class TagSetEditor extends JTextField {
   public TagSetEditor() {
      super("tag1 tag2 tag3");
      this.setBorder(null);
      this.setMinimumSize(new Dimension(100, 30));

      Highlighter highlighter = new TagHighlighter(null);
      TagSearcher searcher = new TagSearcher(this);
      this.setHighlighter(highlighter);
      searcher.search("tag1");
   }
}
