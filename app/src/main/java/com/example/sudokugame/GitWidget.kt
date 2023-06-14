package com.example.sudokugame

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class GitWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }


}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val remoteViews = RemoteViews(context.packageName, R.layout.git_widget)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse("https://insideandroid.in")
    val pendingIntent = PendingIntent.getActivity(context,0,intent,0)

    remoteViews.setOnClickPendingIntent(R.id.gitButton, pendingIntent)
    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
}