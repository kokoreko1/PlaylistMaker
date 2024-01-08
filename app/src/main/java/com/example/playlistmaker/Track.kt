package com.example.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track (val trackName: String,
                  val artistName: String,
                  @SerializedName("trackTimeMillis")
                  val trackTime: Int,
                  val artworkUrl100: String)
