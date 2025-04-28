package dev.nimrod.adsdk_lib.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EndCardView extends FrameLayout {
    public EndCardView(@NonNull Context context) {
        super(context);
        init();
    }

    public EndCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // TODO: inflate overlay layout (advertiserName, button to open targetUrl)
    }
}