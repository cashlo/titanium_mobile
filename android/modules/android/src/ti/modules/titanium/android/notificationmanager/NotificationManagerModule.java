/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.modules.titanium.android.notificationmanager;

import org.appcelerator.kroll.KrollInvocation;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiContext;

import ti.modules.titanium.android.AndroidModule;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;

@Kroll.module(parentModule=AndroidModule.class)
public class NotificationManagerModule extends KrollModule
{
	private static final String LCAT = "TiNotificationManager";

	protected static final int PENDING_INTENT_FOR_ACTIVITY = 0;
	protected static final int PENDING_INTENT_FOR_SERVICE = 1;
	protected static final int PENDING_INTENT_FOR_BROADCAST = 2;
	protected static final int PENDING_INTENT_MAX_VALUE = PENDING_INTENT_FOR_SERVICE;
	
	@Kroll.constant public static final int DEFAULT_ALL = Notification.DEFAULT_ALL;
	@Kroll.constant public static final int DEFAULT_LIGHTS = Notification.DEFAULT_LIGHTS;
	@Kroll.constant public static final int DEFAULT_SOUND = Notification.DEFAULT_SOUND;
	@Kroll.constant public static final int DEFAULT_VIBRATE = Notification.DEFAULT_VIBRATE;
	@Kroll.constant public static final int FLAG_AUTO_CANCEL = Notification.FLAG_AUTO_CANCEL;
	@Kroll.constant public static final int FLAG_INSISTENT = Notification.FLAG_INSISTENT;
	@Kroll.constant public static final int FLAG_NO_CLEAR = Notification.FLAG_NO_CLEAR;
	@Kroll.constant public static final int FLAG_ONGOING_EVENT = Notification.FLAG_ONGOING_EVENT;
	@Kroll.constant public static final int FLAG_ONLY_ALERT_ONCE = Notification.FLAG_ONLY_ALERT_ONCE;
	@Kroll.constant public static final int FLAG_SHOW_LIGHTS = Notification.FLAG_SHOW_LIGHTS;
	@Kroll.constant public static final int STREAM_DEFAULT = Notification.STREAM_DEFAULT;
	
	public NotificationManagerModule(TiContext tiContext)
	{
		super(tiContext);
	}

	// Kept for compatibility with 1.5.x
	public NotificationProxy createNotification(KrollInvocation invocation, Object[] args)
	{
		NotificationProxy notification = new NotificationProxy(invocation.getTiContext());
		notification.handleCreationArgs(this, args);
		return notification;
	}

	private NotificationManager getManager(KrollInvocation invocation)
	{
		return (NotificationManager) invocation.getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
	}
	
	@Kroll.method
	public void cancel(KrollInvocation invocation, int id)
	{
		getManager(invocation).cancel(id);
	}
	
	@Kroll.method
	public void cancelAll(KrollInvocation invocation)
	{
		getManager(invocation).cancelAll();
	}
	
	@Kroll.method
	public void notify(KrollInvocation invocation, int id, NotificationProxy notificationProxy)
	{
		getManager(invocation).notify(id, notificationProxy.getNotification());
	}	
}
