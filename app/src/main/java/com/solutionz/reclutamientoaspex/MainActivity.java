package com.solutionz.reclutamientoaspex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.*;

public class MainActivity extends AppCompatActivity {
    private final static String URL = "https://docs.google.com/forms/d/e/1FAIpQLSdRrrtmMsVUGHPIfcbYFUJMNRR5m8SEwLCijmX5ubw2hT4CHw/viewform";
    Context context;
    WebView webView;
    TextView errorMessage_tv;
    ProgressBar progressBar;
    InternetConnectionDetector connectionDetector;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        connectionDetector = new InternetConnectionDetector(context);

        setViews();
        webViewSettings();

        loadWebPage();


        interstitialAd = new InterstitialAd(this, "399841663784041_399848227116718");
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Toast.makeText(MainActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        interstitialAd.loadAd();
    }

    private void setViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progress);
        errorMessage_tv = findViewById(R.id.errorMessage_tv);
        progressBar.setVisibility(View.GONE);
    }

    private void loadWebPage() {
        errorMessage_tv.setVisibility(View.GONE);
        if (connectionDetector.isConnectingToInternet()) {
            webView.loadUrl(URL);
            webView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        } else {
            onNetworkError();
        }
    }

    private void improveWebViewPerformance() {
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void webViewSettings() {
        improveWebViewPerformance();
        //  webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    switch (error.getErrorCode()) {
                        // case ERROR_FILE_NOT_FOUND:
                        case ERROR_BAD_URL:
                        case ERROR_UNKNOWN:
                        case ERROR_UNSUPPORTED_SCHEME:
                        case ERROR_REDIRECT_LOOP:
                        case ERROR_TOO_MANY_REQUESTS:
                            // case ERROR_HOST_LOOKUP:
                            showLoadingError();
                            break;
                    }
                } else {
                    showLoadingError();
                }

            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                Log.d("usm_progress", "progress= " + progress);
                if (progress == 100 && progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);

                progressBar.setProgress(progress);
            }

        });
    }


    private void showLoadingError() {
        errorMessage_tv.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

    private void onNetworkError() {
        errorMessage_tv.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        errorMessage_tv.setText(getString(R.string.message_network_problem));
        errorMessage_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWebPage();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onBackPressed() {
        if (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }


}


