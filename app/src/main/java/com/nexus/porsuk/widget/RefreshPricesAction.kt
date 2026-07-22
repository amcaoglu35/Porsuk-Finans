package com.nexus.porsuk.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

class RefreshPricesAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Here you would trigger WorkManager to refresh prices
        // For now, just update the widget to show we reacted
        PorsukWidget().update(context, glanceId)
    }
}
