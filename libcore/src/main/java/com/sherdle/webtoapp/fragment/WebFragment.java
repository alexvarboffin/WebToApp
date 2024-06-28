package com.sherdle.webtoapp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sherdle.webtoapp.App;
import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.activity.MainActivity;
import com.sherdle.webtoapp.util.GetFileInfo;
import com.sherdle.webtoapp.widget.AdvancedWebView;
import com.sherdle.webtoapp.widget.scrollable.ToolbarWebViewScrollListener;
import com.sherdle.webtoapp.widget.webview.WebToAppChromeClient;
import com.sherdle.webtoapp.widget.webview.WebToAppWebClient;

import java.util.concurrent.ExecutionException;

import com.sherdle.webtoapp.R;


public class WebFragment extends Fragment implements AdvancedWebView.Listener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "@@@";

    static String URL = "url";
    public AdvancedWebView browser;
    public WebToAppChromeClient chromeClient;
    public ProgressBar progressBar;
    public FrameLayout rl;
    public SwipeRefreshLayout swipeLayout;
    public WebToAppWebClient webClient;
    public String mainUrl = null;
    public int firstLoad = 0;
    private boolean clearHistory = false;

    @Override
    public void onExternalPageRequest(String str) {
    }

    @Override
    public void onPageError(int i, String str, String str2) {
    }

    public static WebFragment newInstance(String str) {
        WebFragment webFragment = new WebFragment();
        Bundle bundle = new Bundle();
        bundle.putString(URL, str);
        webFragment.setArguments(bundle);
        return webFragment;
    }

    public void setBaseUrl(String str) {
        this.mainUrl = str;
        this.clearHistory = true;
        this.browser.loadUrl(str);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() == null || this.mainUrl != null) {
            return;
        }
        this.mainUrl = getArguments().getString(URL);
        this.firstLoad = 0;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.fragment_observable_web_view, viewGroup, false);
        this.rl = frameLayout;
        this.progressBar = frameLayout.findViewById(R.id.progressbar);
        this.browser = this.rl.findViewById(R.id.scrollable);
        this.swipeLayout = this.rl.findViewById(R.id.swipe_container);
        return this.rl;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (Config.PULL_TO_REFRESH) {
            this.swipeLayout.setOnRefreshListener(this);
        } else {
            this.swipeLayout.setEnabled(false);
        }
        this.browser.setListener(this, this);
        if (MainActivity.getCollapsingActionBar()) {
            ((MainActivity) getActivity()).showToolbar(this);
            this.browser.setOnScrollChangeListener(this.browser, new ToolbarWebViewScrollListener() { // from class: com.sherdle.webtoapp.fragment.WebFragment.1
                @Override // com.sherdle.webtoapp.widget.scrollable.ToolbarWebViewScrollListener
                public void onHide() {
                    ((MainActivity) WebFragment.this.getActivity()).hideToolbar();
                }

                @Override // com.sherdle.webtoapp.widget.scrollable.ToolbarWebViewScrollListener
                public void onShow() {
                    ((MainActivity) WebFragment.this.getActivity()).showToolbar(WebFragment.this);
                }
            });
        }
        this.browser.requestFocus();
        this.browser.getSettings().setJavaScriptEnabled(true);
        this.browser.getSettings().setBuiltInZoomControls(false);
        //this.browser.getSettings().setAppCacheEnabled(true);
        this.browser.getSettings().setDatabaseEnabled(true);
        this.browser.getSettings().setDomStorageEnabled(true);
        this.browser.setGeolocationEnabled(true);
        this.browser.getSettings().setPluginState(WebSettings.PluginState.ON);
        if (Config.MULTI_WINDOWS) {
            this.browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            this.browser.getSettings().setSupportMultipleWindows(true);
        }
        WebToAppWebClient webToAppWebClient = new WebToAppWebClient(this, this.browser);
        this.webClient = webToAppWebClient;
        this.browser.setWebViewClient(webToAppWebClient);
        WebToAppChromeClient webToAppChromeClient = new WebToAppChromeClient(this, this.rl, this.browser, this.swipeLayout, this.progressBar);
        this.chromeClient = webToAppChromeClient;
        this.browser.setWebChromeClient(webToAppChromeClient);
        if (this.webClient.hasConnectivity(this.mainUrl, true)) {
            String pushUrl = ((App) getActivity().getApplication()).getPushUrl();
            if (pushUrl != null) {
                this.browser.loadUrl(pushUrl);
                return;
            } else {
                this.browser.loadUrl(this.mainUrl);
                return;
            }
        }
        try {
            ((MainActivity) getActivity()).hideSplash();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
    public void onRefresh() {
        this.browser.reload();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.browser.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.browser.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.browser.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if (!hasPermissionToDownload(getActivity())) return;

        String filename = null;
        try {
            filename = new GetFileInfo().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (filename == null) {
            String fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(url);
            filename = URLUtil.guessFileName(url, null, fileExtenstion);
        }


        if (AdvancedWebView.handleDownload(getActivity(), url, filename)) {
            Toast.makeText(getActivity(), getResources().getString(com.sherdle.webtoapp.R.string.download_done), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getResources().getString(com.sherdle.webtoapp.R.string.download_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean hasPermissionToDownload(final Activity activity) {
        if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.download_permission_explaination);
        builder.setPositiveButton(R.string.common_permission_grant, new DialogInterface.OnClickListener() { // from class: com.sherdle.webtoapp.fragment.WebFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= 23) {
                    activity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
                }
            }
        });
        builder.create().show();
        return false;
    }

    @Override
    public void onPageStarted(String str, Bitmap bitmap) {
        Log.d(TAG, "onPageStarted: " + firstLoad);
        if (this.firstLoad == 0 && MainActivity.getCollapsingActionBar()) {
            ((MainActivity) getActivity()).showToolbar(this);
            this.firstLoad = 1;
        } else if (this.firstLoad == 0) {
            this.firstLoad = 1;
        }
    }

    @Override
    public void onPageFinished(String str) {
        if (!str.equals(this.mainUrl) && getActivity() != null && (getActivity() instanceof MainActivity)) {
            ((MainActivity) getActivity()).showInterstitial();
        }
        Log.d(TAG, "onPageFinished: ");
        try {
            ((MainActivity) getActivity()).hideSplash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.clearHistory) {
            this.clearHistory = false;
            this.browser.clearHistory();
        }
        hideErrorScreen();
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        this.browser.onActivityResult(i, i2, intent);
    }

    public void shareURL() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", String.format(getString(R.string.share_body), this.browser.getTitle(), getString(R.string.app_name) + " https://play.google.com/store/apps/details?id=" + getActivity().getPackageName()));
        startActivity(Intent.createChooser(intent, getText(R.string.sharetitle)));
    }

    public void showErrorScreen(String str) {
        View findViewById = this.rl.findViewById(R.id.empty_view);
        findViewById.setVisibility(View.VISIBLE);
        ((TextView) findViewById.findViewById(R.id.title)).setText(str);
        // from class: com.sherdle.webtoapp.fragment.WebFragment.3
// android.view.View.OnClickListener
        findViewById.findViewById(R.id.retry_button).setOnClickListener(view -> {
            if (WebFragment.this.browser.getUrl() == null) {
                WebFragment.this.browser.loadUrl(WebFragment.this.mainUrl);
            } else {
                WebFragment.this.browser.loadUrl("javascript:document.open();document.close();");
                WebFragment.this.browser.reload();
            }
        });
    }

    public void hideErrorScreen() {
        View findViewById = this.rl.findViewById(R.id.empty_view);
        if (findViewById.getVisibility() == View.VISIBLE) {
            findViewById.setVisibility(View.GONE);
        }
    }
}
