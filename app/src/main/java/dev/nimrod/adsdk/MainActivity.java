package dev.nimrod.adsdk;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import dev.nimrod.adsdk_lib.AdSdk;
import dev.nimrod.adsdk_lib.callback.AdCallback;
import dev.nimrod.adsdk_lib.model.Ad;

public class MainActivity extends AppCompatActivity implements AdCallback {
    private static final String TAG = "MainActivity";

    // Buttons
    private MaterialButton main_BTN_playButton;
    private MaterialButton main_BTN_showAdButton;
    private MaterialButton main_BTN_dailyBonus;

    // Stats
    private TextView main_TXT_coins;
    private TextView main_TXT_gems;
    private TextView main_TXT_lives;

    // Game state
    private int coinsCount = 750;
    private int gemsCount = 25;
    private int livesCount = 5;
    private boolean isAdReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize AdSdk
        Log.d(TAG, "Initializing AdSdk with package: " + getPackageName());
        AdSdk.init(this,this);

        Log.d(TAG, "AdSdk initialized");

        findViews();
        initViews();
        initButtons();

        loadAd();
    }

    private void findViews() {
        // Buttons
        main_BTN_playButton = findViewById(R.id.main_BTN_playButton);
        main_BTN_showAdButton = findViewById(R.id.main_BTN_showAdButton);
        main_BTN_dailyBonus = findViewById(R.id.main_BTN_dailyBonus);

        // Stats
        main_TXT_coins = findViewById(R.id.main_TXT_coins);
        main_TXT_gems = findViewById(R.id.main_TXT_gems);
        main_TXT_lives = findViewById(R.id.main_TXT_lives);
    }

    private void initViews() {
        // Initialize UI with current values
        updateStats();
    }

    private void updateStats() {
        main_TXT_coins.setText(String.valueOf(coinsCount));
        main_TXT_gems.setText(String.valueOf(gemsCount));
        main_TXT_lives.setText(livesCount + "/5");
    }


    private void initButtons() {
        main_BTN_playButton.setOnClickListener(view -> {
            if (livesCount <= 0) {
                Toast.makeText(this, "No lives remaining! Watch ad or wait", Toast.LENGTH_SHORT).show();
                return;
            }

            if (AdSdk.isAdReady()) {
                // Deduct a life
                livesCount--;
                updateStats();

                // Show the ad
                AdSdk.showAd(this);

                Toast.makeText(this, "Starting game", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Loading ad before starting ...", Toast.LENGTH_SHORT).show();
                loadAd();
            }
        });

        // Watch ad for reward button
        main_BTN_showAdButton.setOnClickListener(view -> {
            if (AdSdk.isAdReady()) {
                AdSdk.showAd(this);
            } else {
                Toast.makeText(this, "Loading ad...", Toast.LENGTH_SHORT).show();
            }
        });

        main_BTN_dailyBonus.setOnClickListener(view -> {
            Toast.makeText(this, "Daily bonus available in 23h 45m", Toast.LENGTH_SHORT).show();
        });


    }

    private void loadAd() {
        main_BTN_showAdButton.setEnabled(false);
        main_BTN_showAdButton.setText("WATCH AD FOR 100 COINS");
        Log.d(TAG, "Requesting new ad");
        Log.d(TAG, "Initializing AdSdk with package 222: " + getPackageName());
        AdSdk.init(this,this);

    }

    private void giveReward() {
        coinsCount += 100;
        updateStats();
        Toast.makeText(this, "+100 coins for watching ad!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdAvailable(Ad ad) {
        Log.d(TAG, "Ad is available: " + ad.getId());
        main_BTN_showAdButton.setEnabled(true);
        main_BTN_showAdButton.setText("WATCH AD FOR 100 COINS");
        isAdReady = true;
    }

    @Override
    public void onAdFinished() {
        Log.d(TAG, "Ad playback finished");

        // Give reward when ad finishes
        giveReward();

        // Preload next ad
        isAdReady = false;
        loadAd();
    }

    @Override
    public void onNoAvailable(Ad ad) {
        Log.d(TAG, "No ads available");
        main_BTN_showAdButton.setEnabled(true);
        main_BTN_showAdButton.setText("NO ADS AVAILABLE");
        Toast.makeText(this, "No ads available right now", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "Error: " + message);
        main_BTN_showAdButton.setEnabled(true);
        main_BTN_showAdButton.setText("RETRY AD");
        Toast.makeText(this, "Error loading ad: " + message, Toast.LENGTH_SHORT).show();
    }
}