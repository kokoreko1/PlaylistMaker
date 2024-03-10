package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TracksAdapter(private val tracks: MutableList<Track>?) : RecyclerView.Adapter<TracksViewHolder> () {

    var onTrackClickListenerAdapter: OnTrackClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_view, parent, false)
        return TracksViewHolder(view)
    }
    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {

        val track: Track = tracks?.get(position) ?: Track("","",0,"",0,"","","","")

        holder.bind(track)

        holder.itemView.setOnClickListener{
            onTrackClickListenerAdapter?.onTrackClick(track)
        }
    }
    override fun getItemCount(): Int{
        return tracks?.size ?:0
    }

}
