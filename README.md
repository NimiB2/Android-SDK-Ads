# AdSDK – Android Video Ad Library

## Overview
**AdSDK** is an Android SDK that lets developers preload, display, and track video advertisements inside their apps in just a few lines of code.  
A lightweight demo app is included to showcase typical integration flows.

For advanced campaign management a companion Flask + MongoDB backend is available, but server details are documented separately so this README stays focused on the SDK itself.

---

## Documentation
Full guides and API reference are available at **[AdSDK Documentation](https://nimib2.github.io/AdSDK/)**.

---

## 📸 Screenshots

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/4532ea8f-8649-4407-9acf-2eff2a21c572" width="230"/><br/>
      <sub><b>Ad&nbsp;Integration&nbsp;Flow</b></sub>
    </td>
    <td width="25"></td>  <!-- spacer -->
    <td align="center">
      <img src="https://github.com/user-attachments/assets/4f8bf291-5716-49d8-92ef-102c9d977545" width="230"/><br/>
      <sub><b>SDK&nbsp;User&nbsp;Interface</b></sub>
    </td>
    <td width="25"></td>  <!-- spacer -->
    <td align="center">
      <img src="https://github.com/user-attachments/assets/0cceb809-3828-4f93-8983-4f6e6eebfff9" width="230"/><br/>
      <sub><b>End-Card&nbsp;Screen</b></sub>
    </td>
  </tr>
</table>

---

## 🎥 Sample Video

[![Watch demo](https://raw.githubusercontent.com/<user>/<repo>/main/demo-thumb.png)](https://res.cloudinary.com/dyr4cxjrs/video/upload/v1745858282/AD-SDK_btcpu1.mp4)


---

## Features

### Android SDK (`adsdk_lib`)
- Preload and cache ads for a seamless user experience.
- Display video ads with configurable **skip** and **exit** timers.
- Interactive end‑card with a call‑to‑action button.
- Built‑in event tracking: **view**, **click**, **skip**, **exit**.
- Reward‑system hooks for incentivised ads.
- Automatic lifecycle management (pause / resume tracking).

### Server (`flask‑ad‑server`)
- Advertiser & ad management endpoints.
- Centralised event logging and daily aggregation.
- Swagger‑generated API docs for easy exploration.

---

## Tech Stack
- **Android SDK:** Java, Retrofit, Gson.
- **Backend:** Python, Flask, MongoDB, Flasgger.
- **Database:** MongoDB Atlas.

---

## Android SDK – Quick Integration

1. **Add the dependency** (to be published soon via GitHub Packages or JitPack).
2. **Initialise the SDK** in your `Application` or first `Activity`:

```java
AdSdk.init(context, new AdCallback() {
    @Override
    public void onAdAvailable(Ad ad) { /* handle available ad */ }
    @Override
    public void onAdFinished() { /* reward the user */ }
    @Override
    public void onNoAvailable(Ad ad) { /* handle no ad */ }
    @Override
    public void onError(String message) { /* handle errors */ }
});
```

3. **Show an ad** when ready:

```java
if (AdSdk.isAdReady()) {
    AdSdk.showAd(activity);
}
```

4. **Grant rewards** inside `onAdFinished()`.

---

## License
Copyright 2025 Nimrod Bar

Licensed for use with the AdSDK Project (the "Software").

You may use, modify, and distribute this Software for personal, academic, or commercial purposes, subject to the following conditions:

- Retain this copyright notice and a copy of the license in all copies or substantial portions of the Software.
- Do **not** create or distribute a competing advertising SDK or ad‑serving platform without prior written permission from the copyright holder.
- The Software is provided **"AS IS"**, without warranty of any kind.
