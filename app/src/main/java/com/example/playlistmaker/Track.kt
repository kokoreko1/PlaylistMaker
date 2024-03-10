package com.example.playlistmaker

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Track (
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis")
    val trackTime: Int,
    val artworkUrl100: String,
    val trackId: Long,

    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
){
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}
