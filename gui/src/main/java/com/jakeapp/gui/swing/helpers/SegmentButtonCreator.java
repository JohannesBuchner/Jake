/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jakeapp.gui.swing.helpers;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author studpete
 */
public class SegmentButtonCreator {
    // Create a Layout component that will ensure the buttons abut each other
    public static JComponent createLayoutComponent(List<JButton> segmentButtons) {
        Box layoutBox = Box.createHorizontalBox();
        for (JButton button : segmentButtons) {
            layoutBox.add(button);
        }
        return layoutBox;
    }

    public static JToggleButton createSegmentButton(String style, String position, ButtonGroup buttonGrp) {
        JToggleButton button = new JToggleButton();
        button.putClientProperty("JButton.buttonType", style);
        button.putClientProperty("JButton.segmentPosition", position);
        button.setFocusPainted(false);
        buttonGrp.add(button);
        return button;
    }

    // Bottleneck for creating the buttons for the button group
    public static List<JToggleButton> createSegmentButtonsWithStyle(int numButtons, ButtonGroup buttonGrp, String style) {
        // Allocate a list of JButtons
        List<JToggleButton> buttons = new ArrayList<JToggleButton>();
        if (numButtons == 1) {
            // If 1 button is requested, then it gets the "only" segment position
            buttons.add(createSegmentButton(style, "only", buttonGrp));
        } else {
            // If more than 1 button is requested, then
            // the first one gets "first" the last one gets "last" and the rest get "middle"
            buttons.add(createSegmentButton(style, "first", buttonGrp));
            for (int i = 0; i < numButtons - 2; ++i) {
                buttons.add(createSegmentButton(style, "middle", buttonGrp));
            }
            buttons.add(createSegmentButton(style, "last", buttonGrp));
        }
        return buttons;
    }

    // Convenience methods that pass in the correct button style for each segmented button style
    public static List<JToggleButton> createSegmentedButtons(int numButtons, ButtonGroup buttonGroup) {
        return createSegmentButtonsWithStyle(numButtons, buttonGroup, "segmented");
    }

    public static List<JToggleButton> createSegmentedRoundRectButtons(int numButtons, ButtonGroup buttonGroup) {
        return createSegmentButtonsWithStyle(numButtons, buttonGroup, "segmentedRoundRect");
    }

    public static List<JToggleButton> createSegmentedCapsuleButtons(int numButtons, ButtonGroup buttonGroup) {
        return createSegmentButtonsWithStyle(numButtons, buttonGroup, "segmentedCapsule");
    }

    public static List<JToggleButton> createSegmentedTexturedButtons(int numButtons, ButtonGroup buttonGroup) {
        return createSegmentButtonsWithStyle(numButtons, buttonGroup, "segmentedTextured");
    }

}
