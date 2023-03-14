package ensias.android.coloris.ui.wiki;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WikiViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public WikiViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}