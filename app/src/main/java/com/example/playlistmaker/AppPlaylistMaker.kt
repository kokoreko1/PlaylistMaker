package com.example.playlistmaker

import android.app.Application

class AppPlaylistMaker: Application() {

    var globalVarSavedSearchText = ""

    val globalValRoundingRadius = 10

    override fun onCreate() {
        super.onCreate()
        // initialization code here
    }

}