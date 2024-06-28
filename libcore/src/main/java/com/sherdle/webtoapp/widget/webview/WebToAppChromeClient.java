package com.sherdle.webtoapp.widget.webview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.activity.MainActivity;
import com.sherdle.webtoapp.fragment.WebFragment;
import com.sherdle.webtoapp.widget.AdvancedWebView;
import com.sherdle.webtoapp.R;


public class WebToAppChromeClient extends WebChromeClient {
    protected AdvancedWebView browser;
    protected FrameLayout container;
    protected WebFragment fragment;
    public View mCustomView;
    public WebChromeClient.CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;
    protected WebView popupView;
    public ProgressBar progressBar;
    public SwipeRefreshLayout swipeLayout;

    public WebToAppChromeClient(WebFragment webFragment, FrameLayout frameLayout, AdvancedWebView advancedWebView, SwipeRefreshLayout swipeRefreshLayout, ProgressBar progressBar) {
        this.fragment = webFragment;
        this.container = frameLayout;
        this.browser = advancedWebView;
        this.swipeLayout = swipeRefreshLayout;
        this.progressBar = progressBar;
    }

    @Override // android.webkit.WebChromeClient
    public boolean onCreateWindow(WebView webView, boolean z, boolean z2, Message message) {
        this.browser.setVisibility(8);
        WebView webView2 = new WebView(this.fragment.getActivity());
        this.popupView = webView2;
        webView2.getSettings().setJavaScriptEnabled(true);
        this.popupView.setWebChromeClient(this);
        this.popupView.setWebViewClient(new WebToAppWebClient(this.fragment, this.popupView));
        this.popupView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        this.container.addView(this.popupView);
        ((WebView.WebViewTransport) message.obj).setWebView(this.popupView);
        message.sendToTarget();
        return true;
    }

    @Override // android.webkit.WebChromeClient
    public void onCloseWindow(WebView webView) {
        this.popupView.setVisibility(8);
        this.browser.setVisibility(0);
    }

    @Override // android.webkit.WebChromeClient
    public void onProgressChanged(WebView webView, int i) {
        SwipeRefreshLayout swipeRefreshLayout;
        if (Config.LOAD_AS_PULL && (swipeRefreshLayout = this.swipeLayout) != null) {
            swipeRefreshLayout.setRefreshing(true);
            if (i == 100) {
                this.swipeLayout.setRefreshing(false);
                return;
            }
            return;
        }
        this.progressBar.setProgress(0);
        this.progressBar.setVisibility(0);
        this.progressBar.setProgress(i);
        this.progressBar.incrementProgressBy(i);
        if (i > 99) {
            this.progressBar.setVisibility(8);
            SwipeRefreshLayout swipeRefreshLayout2 = this.swipeLayout;
            if (swipeRefreshLayout2 != null && swipeRefreshLayout2.isRefreshing()) {
                this.swipeLayout.setRefreshing(false);
            }
        }
        if (i > 80) {
            try {
                ((MainActivity) this.fragment.getActivity()).hideSplash();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override // android.webkit.WebChromeClient
    public void onPermissionRequest(PermissionRequest permissionRequest) {
        if (Build.VERSION.SDK_INT >= 21) {
            permissionRequest.grant(permissionRequest.getResources());
        }
    }

    @Override // android.webkit.WebChromeClient
    public void onReceivedTitle(WebView webView, String str) {
        ((MainActivity) this.fragment.getActivity()).setTitle(this.browser.getTitle());
    }

    @Override // android.webkit.WebChromeClient
    public Bitmap getDefaultVideoPoster() {
        if (this.fragment.getActivity() == null) {
            return null;
        }
        return BitmapFactory.decodeResource(this.fragment.getActivity().getApplicationContext().getResources(), R.drawable.vert_loading);
    }

    @Override // android.webkit.WebChromeClient
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback customViewCallback) {
        if (this.mCustomView != null) {
            onHideCustomView();
            return;
        }
        this.mCustomView = view;
        view.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        if (Build.VERSION.SDK_INT >= 11) {
            this.mOriginalSystemUiVisibility = this.fragment.getActivity().getWindow().getDecorView().getSystemUiVisibility();
        }
        this.mOriginalOrientation = this.fragment.getActivity().getRequestedOrientation();
        this.mCustomViewCallback = customViewCallback;
        ((FrameLayout) this.fragment.getActivity().getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
        this.fragment.getActivity().getWindow().getDecorView().setSystemUiVisibility(3846);
        this.fragment.getActivity().setRequestedOrientation(0);
    }

    @Override // android.webkit.WebChromeClient
    public void onHideCustomView() {
        ((FrameLayout) this.fragment.getActivity().getWindow().getDecorView()).removeView(this.mCustomView);
        this.mCustomView = null;
        if (Build.VERSION.SDK_INT >= 11) {
            this.fragment.getActivity().getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
        }
        this.fragment.getActivity().setRequestedOrientation(this.mOriginalOrientation);
        this.mCustomViewCallback.onCustomViewHidden();
        this.mCustomViewCallback = null;
    }

    public View getCustomView() {
        return this.mCustomView;
    }

    public WebChromeClient.CustomViewCallback getCustomViewCallback() {
        return this.mCustomViewCallback;
    }
}
