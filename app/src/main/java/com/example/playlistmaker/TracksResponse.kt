package com.example.playlistmaker

data class TracksResponse(
    val resultCount: Int,
    val results: MutableList<Track>
)