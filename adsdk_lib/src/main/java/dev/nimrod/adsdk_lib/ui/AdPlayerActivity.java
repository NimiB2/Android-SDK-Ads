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

/**
 * Full-screen activity for displaying video advertisements.
 * Handles video playback, user interactions, event tracking, and lifecycle management.
 * Supports skip/exit buttons with configurable timing and displays end cards with call-to-action.
 */
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

        findViews();
        initializeAdFlow();

    }

    private void findViews() {
        videoView = findViewById(R.id.ad_player_video);
        videoContainer = findViewById(R.id.video_container);
        skipButton = findViewById(R.id.ad_player_skip_button);
        exitButton = findViewById(R.id.ad_player_exit_button);
        loadingProgressBar = findViewById(R.id.ad_player_loading);
        endCardView = findViewById(R.id.ad_end_card);
    }

    private void initializeAdFlow() {
        hideEndCard();
        loadCurrentAd();
        handleAd();
    }

    private void hideEndCard() {
        endCardView.setVisibility(View.GONE);
    }

    private void loadCurrentAd() {
        // Get AdManager instance
        adManager = AdManager.getInstance();
        // Get ad from AdManager
        ad = adManager.getCurrentAd();
    }

    private void handleAd() {
        if (ad != null) {
            logAdInfo();
            setupVideoPlayer();
            setupButtons();
        } else {
            Log.e(TAG, "No ad data available");
            finish();
        }
    }

    private void logAdInfo() {
        String adId = ad.getId() != null ? ad.getId() : "N/A";
        Log.d(TAG, "Ad displayed in AdPlayerActivity: " + adId);
    }

    /**
     * Configures the video player with proper scaling, event listeners, and timing controls.
     * Handles video preparation, playback start, error handling, and button timing.
     */
    private void setupVideoPlayer() {
        if (videoView != null && ad.getVideoUrl() != null) {
            try {
                Uri videoUri = Uri.parse(ad.getVideoUrl());
                videoView.setVideoURI(videoUri);

                // Set completion listener
                videoView.setOnCompletionListener(mp -> {
                    videoCompleted = true;
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

    /**
     * Displays the end card overlay after video completion.
     * Shows advertiser information and call-to-action button, dims the video background.
     */

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

    /**
     * Configures skip and exit buttons with timing based on ad settings.
     * Skip button appears after skipTime, exit button appears after exitTime.
     * Handles event tracking for user interactions.
     */
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
                if (!eventSent) {
                    if (videoCompleted) {
                        adManager.createEvent(EventEnum.VIEW);  // Send VIEW when exiting after completion
                    } else {
                        adManager.createEvent(EventEnum.EXIT);  // Send EXIT if not completed
                    }
                    eventSent = true;
                }
                adManager.notifyAdExited();
                finish();
            });
        }
    }

    /**
     * Launches the ad player activity.
     *
     * @param host      The activity to launch from
     * @param adManager The ad manager instance (parameter retained for interface consistency)
     */
    public static void start(Activity host, AdManager adManager) {
        Intent intent = new Intent(host, AdPlayerActivity.class);
        host.startActivity(intent);
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

        if (!eventSent && ad != null) {
            if (!videoCompleted) {
                adManager.createEvent(EventEnum.EXIT);
                adManager.notifyAdExited();
            } else {
                adManager.createEvent(EventEnum.VIEW);
                adManager.notifyAdFinished();
            }
        }
    }
}