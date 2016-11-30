package nielsen.guiltmotivator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by DHZ_Bill on 11/29/16.
 */
public final class NotificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationEventReceiver.setupAlarm(context);
    }
}
