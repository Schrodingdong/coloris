package ensias.android.coloris.util;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.Set;

/**
 * Class for instanciating popup windows, makes use of the <b>Factory Design Pattern</b>
 * */
public class CenterPopupWindowFactory {
    /**
     * static function to create and display a popup window, given :
     * <ul>
     *     <li>Context</li>
     *     <li>rootView - the view which the popup will be attached to</li>
     *     <li>bundle - contains all the additional info to put within a popup</li>
     * </ul>
     * */
    public static void instanciatePopup(Context applicationContext, View rootView, Bundle bundle) {
        Set<String> bundleKeys = bundle.keySet();
        if(bundleKeys.contains(PopupBundleKeys.IMAGE_URI.name())){
            new ColorSegmentationPopup(applicationContext,rootView,bundle).createPopup();
        }
    }
}
