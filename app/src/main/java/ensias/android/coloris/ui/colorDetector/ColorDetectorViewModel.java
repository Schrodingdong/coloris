package ensias.android.coloris.ui.colorDetector;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ColorDetectorViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ColorDetectorViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}