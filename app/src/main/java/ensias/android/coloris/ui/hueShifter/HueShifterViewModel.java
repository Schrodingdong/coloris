package ensias.android.coloris.ui.hueShifter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HueShifterViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HueShifterViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}