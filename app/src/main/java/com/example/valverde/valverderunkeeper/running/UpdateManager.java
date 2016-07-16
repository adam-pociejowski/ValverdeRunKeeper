package com.example.valverde.valverderunkeeper.running;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.example.valverde.valverderunkeeper.R;
import com.example.valverde.valverderunkeeper.notifications.RunningSpeaker;
import com.example.valverde.valverderunkeeper.notifications.SpeakingManager;
import com.example.valverde.valverderunkeeper.settings.Settings;
import com.example.valverde.valverderunkeeper.settings.SettingsManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateManager {
    private TrackerBroadcasterReceiver receiver;
    private SpeakingManager speakingManager;
    private TempoChartChangeNotifier tempoChartNotifier;
    private Settings settings;
    private Context context;

    public UpdateManager(Context context, RelativeLayout layout) {
        this.context = context;
        settings = SettingsManager.getSettings(context);
        startSpeakingManager();
        startTempoChartNotifier(layout);
        startTrackerReceiver();
    }

    public void notifyDistance(double distance) {
        if (settings.getSoundNotifications())
            speakingManager.notifyDistance(distance);
        tempoChartNotifier.notifyDistance(distance);
    }

    public void close() {
        if (speakingManager != null)
            speakingManager.close();
        if (receiver != null)
            context.unregisterReceiver(receiver);
    }

    public void speak(String text) {
        speakingManager.speak(text);
    }

    private void startSpeakingManager() {
        if (settings.getSoundNotifications()) {
            speakingManager = new RunningSpeaker(context);
            speakingManager.setDistanceNotifyInterval(settings.getSoundNotificationDistanceInterval());
        }
    }

    private void startTrackerReceiver() {
        if (settings.isScreenLockSupport()) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_ANSWER);
            filter.addAction("android.media.VOLUME_CHANGED_ACTION");
            receiver = new TrackerBroadcasterReceiver();
            context.registerReceiver(receiver, filter);
        }
    }

    private void startTempoChartNotifier(RelativeLayout layout) {
        tempoChartNotifier = new TempoChartChangeNotifier(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ABOVE, R.id.bottomLayout);
        params.addRule(RelativeLayout.BELOW, R.id.topLayout);
        layout.addView(tempoChartNotifier.getChart(), params);
    }
}