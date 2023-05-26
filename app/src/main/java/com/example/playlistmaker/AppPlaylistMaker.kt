package com.example.playlistmaker

import android.app.Application

class AppPlaylistMaker: Application() {

    var globalVarSavedSearchText = ""

    override fun onCreate() {
        super.onCreate()
        // initialization code here
    }

}