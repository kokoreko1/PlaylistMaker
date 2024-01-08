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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracks_view, parent, false)
        return TracksViewHolder(view)
    }
    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks?.get(position) ?: Track("","",0,""))
    }
    override fun getItemCount(): Int{
        return tracks?.size ?:0
    }
}

class TracksViewHolder(trackView: View): RecyclerView.ViewHolder(trackView) {

    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)

    private val trackImage: ImageView = itemView.findViewById(R.id.track_icon)

    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    fun bind(track: Track) {

        val application = AppPlaylistMaker()

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = dateFormat.format(track.trackTime)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.barsik)
            .centerCrop()
            .transform(RoundedCorners(application.globalValRoundingRadius))
            .into(trackImage)

    }
}