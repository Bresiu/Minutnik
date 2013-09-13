package pl.bresiu.minutnik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_ALARM = "pl.bresiu.minutnik";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String action = bundle.getString(ACTION_ALARM);
        if (action.equals(ACTION_ALARM)) {
            Intent inService = new Intent(context, TaskService.class);
            context.startService(inService);
        }
    }
}
