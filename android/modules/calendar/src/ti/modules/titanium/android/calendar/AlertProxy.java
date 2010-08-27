package ti.modules.titanium.android.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiContext;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class AlertProxy extends KrollProxy {

	public static final int STATE_SCHEDULED = 0;
	public static final int STATE_FIRED = 1;
	public static final int STATE_DISMISSED = 2;
	
	protected String id, eventId;
	protected Date begin, end;
	protected Date alarmTime;
	protected int state, minutes;
	
	public AlertProxy(TiContext context) {
		super(context);
	}
	
	public static String getAlertsUri() {
		return CalendarProxy.getBaseCalendarUri() + "/calendar_alerts";
	}
	
	public static String getAlertsInstanceUri() {
		return CalendarProxy.getBaseCalendarUri() + "/calendar_alerts/by_instance";
	}
	
	public static ArrayList<AlertProxy> queryAlerts(TiContext context, String query, String queryArgs[], String orderBy) {
		ArrayList<AlertProxy> alerts = new ArrayList<AlertProxy>();
		ContentResolver contentResolver = context.getActivity().getContentResolver();
		
		Cursor cursor = contentResolver.query(Uri.parse(getAlertsUri()),
			new String[] { "_id", "event_id", "begin", "end", "alarmTime", "state", "minutes"},
			query, queryArgs, orderBy);
		
		if (cursor!=null)
		{
			while (cursor.moveToNext()) {
				AlertProxy alert = new AlertProxy(context);
				alert.id = cursor.getString(0);
				alert.eventId = cursor.getString(1);
				alert.begin = new Date(cursor.getLong(2));
				alert.end = new Date(cursor.getLong(3));
				alert.alarmTime = new Date(cursor.getLong(4));
				alert.state = cursor.getInt(5);
				alert.minutes = cursor.getInt(6);
				alerts.add(alert);
			}

			cursor.close();
		}
		
		return alerts;
	}
	
	public static ArrayList<AlertProxy> getAlertsForEvent(TiContext context, EventProxy event) {
		return queryAlerts(context, "event_id = ?", new String[] { event.getId() }, "alarmTime ASC,begin ASC,title ASC");
	}
	
	public static AlertProxy createAlert(TiContext context, EventProxy event, int minutes) {
		ContentResolver contentResolver = context.getActivity().getContentResolver();
		ContentValues values = new ContentValues();
		
		Calendar alarmTime = Calendar.getInstance();
		alarmTime.setTime(event.getBegin());
		alarmTime.add(Calendar.MINUTE, -minutes);
		
		values.put("event_id", event.getId());
		values.put("begin", event.getBegin().getTime());
		values.put("end", event.getEnd().getTime());
		values.put("alarmTime", alarmTime.getTimeInMillis());
		values.put("state", STATE_SCHEDULED);
		values.put("minutes", minutes);
		values.put("creationTime", System.currentTimeMillis());
		values.put("receivedTime", 0);
		values.put("notifyTime", 0);
		
		Uri alertUri = contentResolver.insert(Uri.parse(getAlertsUri()), values);
		String alertId = alertUri.getLastPathSegment();
		
		AlertProxy alert = new AlertProxy(context);
		alert.id = alertId;
		alert.begin = event.getBegin();
		alert.end = event.getEnd();
		alert.alarmTime = alarmTime.getTime();
		alert.state = STATE_SCHEDULED;
		alert.minutes = minutes;
		alert.registerAlertIntent(context);
		return alert;
	}
	
	protected static final String EVENT_REMINDER_ACTION = "android.intent.action.EVENT_REMINDER";
	protected void registerAlertIntent(TiContext context) {
//		Uri uri = ContentUris.withAppendedId(Uri.parse(getAlertsUri()), Long.parseLong(id));
//		Intent intent = new Intent(EVENT_REMINDER_ACTION);
		Intent intent = new Intent(context.getActivity(), AlarmReceiver.class);
//		intent.setAction("" + Math.random());
		// intent.setData(uri);
		// intent.putExtra("beginTime", begin.getTime());
		// intent.putExtra("endTime", end.getTime());
		PendingIntent sender = PendingIntent.getBroadcast(context.getActivity(), 0, intent,
			PendingIntent.FLAG_CANCEL_CURRENT);
		// PendingIntent sender = PendingIntent.getActivity(context.getActivity(), 0, intent,
		// 		PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager manager = (AlarmManager) 
			getTiContext().getActivity().getSystemService(Context.ALARM_SERVICE);
		
//		manager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTime()+2000, sender);
		manager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),sender);
	}

	public String getId() {
		return id;
	}

	public String getEventId() {
		return eventId;
	}
	
	public Date getBegin() {
		return begin;
	}

	public Date getEnd() {
		return end;
	}

	public Date getAlarmTime() {
		return alarmTime;
	}

	public int getState() {
		return state;
	}

	public int getMinutes() {
		return minutes;
	}
	
	
}