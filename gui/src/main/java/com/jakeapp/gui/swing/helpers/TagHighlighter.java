package com.jakeapp.gui.swing.helpers;

import javax.swing.text.*;
import java.awt.*;

// Some stuff copied from
// http://www.java2s.com/Code/Java/Swing-JFC/JTextPaneHighlightExample.htm

public class TagHighlighter extends DefaultHighlighter {
   public TagHighlighter(Color c) {
      painter = (c == null ? sharedPainter : new TagHighlightPainter(c));
   }

   // Convenience method to add a highlight with
   // the default painter.
   public Object addHighlight(int p0, int p1) throws BadLocationException {
      return addHighlight(p0, p1, painter);
   }

   public void setDrawsLayeredHighlights(boolean newValue) {
      // Illegal if false - we only support layered highlights
      if (!newValue) {
         throw new IllegalArgumentException(
               "UnderlineHighlighter only draws layered highlights");
      }
      super.setDrawsLayeredHighlights(true);
   }

   // Painter for underlined highlights
   public static class TagHighlightPainter extends
         LayeredHighlighter.LayerPainter {
      public TagHighlightPainter(Color c) {
         color = c;
      }

      public void paint(Graphics g, int offs0, int offs1, Shape bounds,
                        JTextComponent c) {
         // Do nothing: this method will never be called
      }

      public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds,
                              JTextComponent c, View view) {
         g.setColor(new Color(0, 51, 102));

         Rectangle alloc = null;
         if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
            if (bounds instanceof Rectangle) {
               alloc = (Rectangle) bounds;
            } else {
               alloc = bounds.getBounds();
            }
         } else {
            try {
               Shape shape = view.modelToView(offs0,
                     Position.Bias.Forward, offs1,
                     Position.Bias.Backward, bounds);
               alloc = (shape instanceof Rectangle) ? (Rectangle) shape
                     : shape.getBounds();
            } catch (BadLocationException e) {
               return null;
            }
         }

         FontMetrics fm = c.getFontMetrics(c.getFont());
         int baseline = alloc.y + alloc.height - fm.getDescent() + 1;
         g.drawRect(alloc.x, baseline - 12, alloc.width + 4, 15);


         return alloc;
      }

      protected Color color; // The color for the underline
   }

   // Shared painter used for default highlighting
   protected static final Highlighter.HighlightPainter sharedPainter = new TagHighlightPainter(
         null);

   // Painter used for this highlighter
   protected Highlighter.HighlightPainter painter;
}
