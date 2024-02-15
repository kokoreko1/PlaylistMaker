package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapterHistory(private val tracks: MutableList<Track>?) : RecyclerView.Adapter<TracksViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_view, parent, false)
        return TracksViewHolder(view)
    }
    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks?.get(position) ?: Track("","",0,"",0))
    }
    override fun getItemCount(): Int{
        return tracks?.size ?:0
    }
}
