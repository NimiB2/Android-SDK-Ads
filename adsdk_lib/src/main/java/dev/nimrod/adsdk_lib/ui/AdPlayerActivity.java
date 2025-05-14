package dev.nimrod.adsdk_lib.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dev.nimrod.adsdk_lib.manager.AdPreloadManager;
import dev.nimrod.adsdk_lib.util.EventEnum;
import dev.nimrod.adsdk_lib.R;
import dev.nimrod.adsdk_lib.manager.AdManager;
import dev.nimrod.adsdk_lib.model.Ad;

public class AdPlayerActivity extends AppCompatActivity {
    private static final String TAG = "AdPlayerActivity";

    private VideoView videoView;
    private FrameLayout videoContainer;
    private Button skipButton;
    private Button exitButton;
    private ProgressBar loadingProgressBar;
    private View endCardView;
    private boolean skipped = false;
    private boolean eventSent = false;
    private int currentPosition = 0;
    private Ad ad;
    private AdManager adManager;
    private boolean videoCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen flags
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ad_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find views
        videoView = findViewById(R.id.ad_player_video);
        videoContainer = findViewById(R.id.video_container);
        skipButton = findViewById(R.id.ad_player_skip_button);
        exitButton = findViewById(R.id.ad_player_exit_button);
        loadingProgressBar = findViewById(R.id.ad_player_loading);
        endCardView = findViewById(R.id.ad_end_card);
        endCardView.setVisibility(View.GONE);

        // Get AdManager instance
        adManager = AdManager.getInstance();

        // Get ad from AdManager
        ad = adManager.getCurrentAd();

        if (ad != null) {
            // Log the ad info
            Log.d(TAG, "Ad displayed in AdPlayerActivity: " + (ad.getId() != null ? ad.getId() : "N/A"));

            // Setup the video player and buttons
            setupVideoPlayer();
            setupButtons();
        } else {
            Log.e(TAG, "No ad data available");
            finish();
        }
    }

    private void setupVideoPlayer() {
        if (videoView != null && ad.getVideoUrl() != null) {
            try {
                Uri videoUri = Uri.parse(ad.getVideoUrl());
                videoView.setVideoURI(videoUri);

                // Set completion listener
                videoView.setOnCompletionListener(mp -> {
                    videoCompleted = true;
                    adManager.createEvent(EventEnum.VIEW);
                    adManager.notifyAdFinished();
                    showEndCard();
                });

                // Set prepared listener
                videoView.setOnPreparedListener(mp -> {
                    loadingProgressBar.setVisibility(View.GONE);

                    // Scale video to fit container
                    int videoWidth = mp.getVideoWidth();
                    int videoHeight = mp.getVideoHeight();

                    if (videoWidth > 0 && videoHeight > 0) {
                        int containerWidth = videoContainer.getWidth();
                        int containerHeight = videoContainer.getHeight();
                        float scaleX = (float) containerWidth / videoWidth;
                        float scaleY = (float) containerHeight / videoHeight;
                        float scale = Math.min(scaleX, scaleY);
                        int newWidth = (int) (videoWidth * scale);
                        int newHeight = (int) (videoHeight * scale);

                        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
                        layoutParams.width = newWidth;
                        layoutParams.height = newHeight;
                        videoView.setLayoutParams(layoutParams);
                    }

                    // Start playback
                    mp.start();

                    // Let AdManager start tracking watch time
                    adManager.startWatchTimeTracking();

                    // Show exit button after exitTime
                    long exitTimeMs = (long) (ad.getExitTime() * 1000);
                    if (exitTimeMs > 0) {
                        videoView.postDelayed(() -> {
                            if (!isFinishing()) {
                                exitButton.setAlpha(1f);
                                exitButton.setVisibility(View.VISIBLE);
                                exitButton.setTranslationZ(100f);
                                exitButton.bringToFront();
                                exitButton.invalidate();
                            }
                        }, exitTimeMs);
                    }
                });

                // Set error listener
                videoView.setOnErrorListener((mp, what, extra) -> {
                    loadingProgressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Video playback error: " + what + ", " + extra);
                    Toast.makeText(AdPlayerActivity.this, "Error playing video", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                });

                videoView.requestFocus();

            } catch (Exception e) {
                Log.e(TAG, "Error setting video URI", e);
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Error loading video", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, "No video URL provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showEndCard() {
        if (endCardView == null || ad == null) return;

        // Dim the video
        videoView.animate().alpha(0.5f).setDuration(400).start();

        // Show end card
        endCardView.setAlpha(0f);
        endCardView.setVisibility(View.VISIBLE);
        endCardView.animate().alpha(1f).setDuration(400).start();

        // Set performer name
        TextView advertiserName = endCardView.findViewById(R.id.advertiser_name);
        Button openLinkButton = endCardView.findViewById(R.id.open_link_button);
        advertiserName.setText(ad.getPerformerName());

        // Handle Visit button
        openLinkButton.setOnClickListener(v -> {
            adManager.createEvent(EventEnum.CLICK);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ad.getTargetUrl()));
            startActivity(browserIntent);
            finish();
        });

        // Hide skip, show exit
        skipButton.setVisibility(View.GONE);
        exitButton.setAlpha(1f);
        exitButton.setVisibility(View.VISIBLE);
        exitButton.setTranslationZ(100f);
        exitButton.bringToFront();
        exitButton.invalidate();
    }

    private void setupButtons() {
        // Skip button
        if (skipButton != null) {
            long skipTimeMs = Math.max(1000, (long) (ad.getSkipTime() * 1000));
            skipButton.setVisibility(View.INVISIBLE);

            skipButton.postDelayed(() -> {
                skipButton.setVisibility(View.VISIBLE);
            }, skipTimeMs);

            skipButton.setOnClickListener(v -> {
                skipped = true;
                adManager.createEvent(EventEnum.SKIP);
                eventSent = true;
                adManager.notifyAdSkipped();
                finish();
            });
        }

        // Exit button
        if (exitButton != null) {
            long exitTimeMs = Math.max(1000, (long) (ad.getExitTime() * 1000));
            exitButton.setVisibility(View.INVISIBLE);

            exitButton.postDelayed(() -> {
                exitButton.setVisibility(View.VISIBLE);
            }, exitTimeMs);


            exitButton.setOnClickListener(v -> {
                if (!videoCompleted) {
                    adManager.createEvent(EventEnum.EXIT);
                }
                eventSent = true;
                adManager.notifyAdExited();
                finish();
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            currentPosition = videoView.getCurrentPosition();
            videoView.pause();
        }
        adManager.pauseWatchTimeTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null && currentPosition > 0 && !videoCompleted) {
            videoView.seekTo(currentPosition);
            videoView.start();
        }
        adManager.resumeWatchTimeTracking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (videoView != null) {
            videoView.stopPlayback();
        }

        // Only send exit event if no other event was sent (back button, etc)
        if (!eventSent && !videoCompleted && ad != null) {
            adManager.createEvent(EventEnum.EXIT);
            adManager.notifyAdExited();
        }
    }

    // Start activity with AdManager
    public static void start(Activity host, AdManager adManager) {
        Intent intent = new Intent(host, AdPlayerActivity.class);
        host.startActivity(intent);
    }
}