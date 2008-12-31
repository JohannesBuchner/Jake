package com.jakeapp.gui.swing;

public class JakeGuiStartHelper {

    private static ICoreAccess coreAccess;


    public static void launch(ICoreAccess coreAccess)
    {
        JakeMainApp app = new JakeMainApp();
        JakeMainApp.main(null);
        app.setCore(coreAccess);
      
    }

}
