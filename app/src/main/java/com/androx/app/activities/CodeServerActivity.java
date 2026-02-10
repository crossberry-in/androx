package com.androx.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public final class CodeServerActivity extends AppCompatActivity {

    WebView mWebView;
    private static final String CODE_SERVER_URL = "http://127.0.0.1:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final RelativeLayout progressLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setLayoutParams(lParams);
        progressLayout.addView(progressBar);

        mWebView = new WebView(this);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        
        // Enable local storage and database
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        
        // Set custom user agent to prevent issues with code-server
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString(userAgent + " Androx/CodeServer");

        setContentView(progressLayout);

        mWebView.setWebChromeClient(new WebChromeClient() {
            private ProgressBar mProgressBar;

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (mProgressBar == null) {
                    mProgressBar = new ProgressBar(CodeServerActivity.this);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 4);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    view.addView(mProgressBar, params);
                }
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(ProgressBar.GONE);
                    setContentView(mWebView);
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                setContentView(mWebView);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://127.0.0.1:8080") || url.startsWith("http://localhost:8080")) {
                    view.loadUrl(url);
                    return false;
                }
                // Open external links in browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
                startActivity(browserIntent);
                return true;
            }
        });

        mWebView.loadUrl(CODE_SERVER_URL);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }
}
