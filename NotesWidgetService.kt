package com.days.notes

import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class NotesWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return NotesRemoteViewsFactory(applicationContext)
    }
}

class NotesRemoteViewsFactory(private val context: android.content.Context) :
    RemoteViewsService.RemoteViewsFactory {

    private var notes: List<String> = emptyList()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        notes = NotesRepository.getNotes(context, NotesRepository.todayKey())
    }

    override fun onDestroy() {}

    override fun getCount(): Int = notes.size

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_note_item)
        views.setTextViewText(R.id.widgetNoteText, notes[position])
        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
