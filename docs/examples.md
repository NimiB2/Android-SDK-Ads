```markdown
# AdSDK Integration Examples

This page provides practical examples for integrating the AdSDK into your Android application.

## Basic Implementation

Here's a complete example of a simple activity that implements AdSDK:

```java
public class GameActivity extends AppCompatActivity implements AdCallback {
    
    private Button watchAdButton;
    private TextView coinsTextView;
    private int coins = 100;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        // Find views
        watchAdButton = findViewById(R.id.watch_ad_button);
        coinsTextView = findViewById(R.id.coins_text_view);
        
        // Initialize AdSDK
        AdSdk.init(this, this);
        
        // Update UI
        updateCoinsDisplay();
        
        // Set up button click listener
        watchAdButton.setOnClickListener(v -> {
            if (AdSdk.isAdReady()) {
                AdSdk.showAd(this);
                watchAdButton.setEnabled(false);
                watchAdButton.setText("Loading Ad...");
            } else {
                Toast.makeText(this, "No ads available right now", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateCoinsDisplay() {
        coinsTextView.setText("Coins: " + coins);
    }
    
    // AdCallback implementation
    
    @Override
    public void onAdAvailable(Ad ad) {
        watchAdButton.setEnabled(true);
        watchAdButton.setText("Watch Ad for 50 Coins");
    }
    
    @Override
    public void onAdFinished() {
        // Reward the player
        coins += 50;
        updateCoinsDisplay();
        
        Toast.makeText(this, "+50 coins for watching the ad!", Toast.LENGTH_SHORT).show();
        
        // Re-enable the button
        watchAdButton.setEnabled(true);
        watchAdButton.setText("Watch Another Ad");
    }
    
    @Override
    public void onNoAvailable(Ad ad) {
        watchAdButton.setEnabled(false);
        watchAdButton.setText("No Ads Available");
    }
    
    @Override
    public void onError(String message) {
        Log.e("AdSDK", "Error: " + message);
        watchAdButton.setEnabled(true);
        watchAdButton.setText("Try Again");
    }
}
```

## Example: Using AdSDK as a Reward System

This example shows how to implement AdSDK as a reward system when a player runs out of lives:

```java
public class GameOverActivity extends AppCompatActivity implements AdCallback {
    
    private Button watchAdButton;
    private Button quitButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        
        watchAdButton = findViewById(R.id.watch_ad_button);
        quitButton = findViewById(R.id.quit_button);
        
        // Initialize AdSDK
        AdSdk.init(this, this);
        
        watchAdButton.setOnClickListener(v -> {
            if (AdSdk.isAdReady()) {
                AdSdk.showAd(this);
            } else {
                Toast.makeText(this, "Ad not available, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
        
        quitButton.setOnClickListener(v -> {
            finish();
        });
    }
    
    @Override
    public void onAdAvailable(Ad ad) {
        watchAdButton.setEnabled(true);
        watchAdButton.setText("Watch Ad for Extra Life");
    }
    
    @Override
    public void onAdFinished() {
        // Player gets a second chance
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("EXTRA_LIFE", true);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onNoAvailable(Ad ad) {
        watchAdButton.setEnabled(false);
        watchAdButton.setText("No Ads Available");
    }
    
    @Override
    public void onError(String message) {
        Log.e("AdSDK", "Error: " + message);
        watchAdButton.setText("Try Again");
    }
}
```

## Example: Preloading Ads for Later Use

This example demonstrates how to preload ads when the app starts:

```java
public class MyApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize AdSDK early to preload ads
        AdSdk.init(getApplicationContext(), new AdCallback() {
            @Override
            public void onAdAvailable(Ad ad) {
                Log.d("AdSDK", "Ad preloaded successfully");
            }
            
            @Override
            public void onAdFinished() {
                // This won't be called during preloading
            }
            
            @Override
            public void onNoAvailable(Ad ad) {
                Log.d("AdSDK", "No ads available for preloading");
            }
            
            @Override
            public void onError(String message) {
                Log.e("AdSDK", "Error preloading ad: " + message);
            }
        });
    }
}
```

## Best Practices

- **Initialize Early**: Initialize the SDK in your Application class to preload ads
- **Check Availability**: Always use `AdSdk.isAdReady()` before showing ads
- **Handle Callbacks**: Implement all callback methods properly
- **UI Feedback**: Update your UI to reflect ad status (available, loading, unavailable)
- **Error Handling**: Log errors and provide fallback options when ads aren't available
- **Reward Users**: Make sure to reward users only when `onAdFinished()` is called

## Next Steps

- Review the [API Reference](api-reference.md) for detailed SDK documentation
- Set up the [Ad Server](../api-server/setup.md) to manage ad campaigns
```
