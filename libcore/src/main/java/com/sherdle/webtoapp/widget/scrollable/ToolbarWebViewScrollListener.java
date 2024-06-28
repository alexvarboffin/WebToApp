package com.sherdle.webtoapp.widget.scrollable;

import com.sherdle.webtoapp.widget.AdvancedWebView;


public abstract class ToolbarWebViewScrollListener implements AdvancedWebView.ScrollInterface {
    private static final int HIDE_THRESHOLD = 150;
    private int mScrolledDistance = 0;
    private boolean mControlsVisible = true;

    public abstract void onHide();

    public abstract void onShow();

    @Override // com.sherdle.webtoapp.widget.AdvancedWebView.ScrollInterface
    public void onScrollChanged(AdvancedWebView advancedWebView, int i, int i2, int i3, int i4) {
        if (advancedWebView.getScrollY() == 0) {
            if (!this.mControlsVisible) {
                onShow();
                this.mControlsVisible = true;
            }
        } else {
            int i5 = this.mScrolledDistance;
            if (i5 > HIDE_THRESHOLD && this.mControlsVisible) {
                onHide();
                this.mControlsVisible = false;
                this.mScrolledDistance = 0;
            } else if (i5 < -150 && !this.mControlsVisible) {
                onShow();
                this.mControlsVisible = true;
                this.mScrolledDistance = 0;
            }
        }
        boolean z = this.mControlsVisible;
        if ((!z || i2 - i4 <= 0) && (z || i2 - i4 >= 0)) {
            return;
        }
        this.mScrolledDistance += i2 - i4;
    }
}
