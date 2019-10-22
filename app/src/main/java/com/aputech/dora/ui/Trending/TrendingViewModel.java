package com.aputech.dora.ui.Trending;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrendingViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public TrendingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Trending fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
