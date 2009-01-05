package com.jakeapp.gui.swing.helpers;

import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import java.awt.*;

public class TagSearcher {
   public TagSearcher(JTextComponent comp) {
      this.comp = comp;
      this.painter = new TagHighlighter.TagHighlightPainter(
            Color.red);
   }

   // Search for a word and return the offset of the
   // first occurrence. Highlights are added for all
   // occurrences found.
   public int search(String word) {
      int firstOffset = -1;
      Highlighter highlighter = comp.getHighlighter();

      // Remove any existing highlights for last word
      Highlighter.Highlight[] highlights = highlighter.getHighlights();
      for (Highlighter.Highlight h : highlights) {
         if (h.getPainter() instanceof TagHighlighter.TagHighlightPainter) {
            highlighter.removeHighlight(h);
         }
      }

      if (word == null || word.equals("")) {
         return -1;
      }

      // Look for the word we are given - insensitive search

      String content;
      try {
         Document d = comp.getDocument();
         content = d.getText(0, d.getLength()).toLowerCase();
      } catch (BadLocationException e) {
         // Cannot happen
         return -1;
      }

      /*
      word = word.toLowerCase();
      int lastIndex = 0;
      int wordSize = word.length();

      while ((lastIndex = content.indexOf(word, lastIndex)) != -1) {
         int endIndex = lastIndex + wordSize;
         try {
            highlighter.addHighlight(lastIndex, endIndex, painter);
         } catch (BadLocationException e) {
            // Nothing to do
         }
         if (firstOffset == -1) {
            firstOffset = lastIndex;
         }
         lastIndex = endIndex;
      }
      */

      int lastIndex = 0;
      for (int currentPos = 0; currentPos < content.length(); currentPos++) {
         char currentChar = content.charAt(currentPos);
         if (currentChar == ' ') {
            try {
               highlighter.addHighlight(lastIndex, currentPos - 1, painter);
            } catch (BadLocationException e) {
               // Ignore
            }
            lastIndex = currentPos + 1;
         }
         if (currentPos == content.length() - 1) {
            try {
               highlighter.addHighlight(lastIndex, currentPos, painter);
            } catch (BadLocationException e) {
               // Ignore
            }
         }
      }

      return firstOffset;
   }

   protected JTextComponent comp;

   protected Highlighter.HighlightPainter painter;

}