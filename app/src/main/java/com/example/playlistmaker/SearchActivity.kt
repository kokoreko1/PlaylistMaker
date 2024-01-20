package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesAPIService: ITunesAPI = retrofit.create(ITunesAPI::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        val ivNothingWasFound = findViewById<ImageView>(R.id.image_nothing_was_found)
        val tvNothingWasFound = findViewById<TextView>(R.id.text_nothing_was_found)

        val etSearch = findViewById<EditText>(R.id.searchEditText)
        etSearch.setText((application as AppPlaylistMaker).globalVarSavedSearchText)

        // кнопка Обновить при проблемах с соединением
        val btConnectionProblems = findViewById<Button>(R.id.button_update)
        btConnectionProblems.setOnClickListener {
            searchTracks(etSearch.text.toString())
        }

        // кнопка Возврат
        val ivBack = findViewById<ImageView>(R.id.image_back)
        ivBack.setOnClickListener {
            finish()
        }

        // история поиска
        val vgLookFor = findViewById<ViewGroup>(R.id.view_group_history)
        etSearch.setOnFocusChangeListener { view, hasFocus ->
            vgLookFor.visibility = if (hasFocus && etSearch.text.isEmpty()) View.VISIBLE else View.GONE
        }

        val rvTracksHistory = findViewById<RecyclerView>(R.id.recycler_view_tracks_history)
        rvTracksHistory.layoutManager = LinearLayoutManager(this)


        // RecycleView
        val rvTracks = findViewById<RecyclerView>(R.id.recycler_view_tracks)
        rvTracks.layoutManager = LinearLayoutManager(this)

        // кнопка Очистки поля inputEditText
        val btClear = findViewById<ImageView>(R.id.clearIcon)

        btClear.setOnClickListener {

            etSearch.setText("")
            hideSoftKeyboard(it)

            ivNothingWasFound.visibility = View.GONE
            tvNothingWasFound.visibility = View.GONE

            rvTracks.adapter = TracksAdapter(mutableListOf())
        }

        // TextWatcher
        val simpleTextWatcher = object : SimpleTextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btClear.isVisible = !s.isNullOrEmpty()
                vgLookFor.visibility = if (etSearch.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
                saveSearchText(s)
            }
        }

        etSearch.addTextChangedListener(simpleTextWatcher)


        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(etSearch.text.toString())
                true
            }
            false
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val etSearch = findViewById<EditText>(R.id.searchEditText)
        outState.putString("searchEditText", etSearch.getText().toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val etSearch = findViewById<EditText>(R.id.searchEditText)
        etSearch.setText(savedInstanceState.getString("searchEditText"))
    }

    private fun hideSoftKeyboard(view: View) {
        val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun saveSearchText(s: Editable?) {
        (application as AppPlaylistMaker).globalVarSavedSearchText = s.toString()
    }

    private fun searchTracks(wordToSearch: String){

        val ivNothingWasFound = findViewById<ImageView>(R.id.image_nothing_was_found)
        val tvNothingWasFound = findViewById<TextView>(R.id.text_nothing_was_found)

        val ivConnectionProblems = findViewById<ImageView>(R.id.image_connection_problems)
        val tvConnectionProblems = findViewById<TextView>(R.id.text_connection_problems)
        val btConnectionProblems = findViewById<Button>(R.id.button_update)

        // RecycleView
        val rvTracks = findViewById<RecyclerView>(R.id.recycler_view_tracks)


        iTunesAPIService.search(wordToSearch).enqueue(object : Callback<TracksResponse>{

            override fun onResponse(call: Call<TracksResponse>,
                                    response: Response<TracksResponse>) {

                if (response.isSuccessful) {

                    if (response.body()?.results?.size ?: 0 == 0){
                        showMessage(TYPE_MESSAGE_NOTHING_WAS_FOUND)
                    } else {

                        rvTracks.isVisible = true

                        ivNothingWasFound.isVisible = false
                        tvNothingWasFound.isVisible = false

                        ivConnectionProblems.isVisible = false
                        tvConnectionProblems.isVisible = false
                        btConnectionProblems.isVisible = false

                        val tracksAdapter = TracksAdapter(response.body()?.results)
                        rvTracks.adapter = tracksAdapter

                    }

                } else {
                    showMessage(TYPE_MESSAGE_CONNECTION_PROBLEMS)
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showMessage(TYPE_MESSAGE_CONNECTION_PROBLEMS)
            }

            fun showMessage(tpMessage: Int){
                when(tpMessage){
                    TYPE_MESSAGE_CONNECTION_PROBLEMS -> {

                        rvTracks.isVisible = false

                        ivConnectionProblems.isVisible = true
                        tvConnectionProblems.isVisible = true
                        btConnectionProblems.isVisible = true

                    }
                    TYPE_MESSAGE_NOTHING_WAS_FOUND -> {

                        rvTracks.isVisible = false

                        ivNothingWasFound.isVisible = true
                        tvNothingWasFound.isVisible = true

                    }
                }

            }

        })

    }
    companion object {
        const val TYPE_MESSAGE_NOTHING_WAS_FOUND = 1
        const val TYPE_MESSAGE_CONNECTION_PROBLEMS = 2
    }

}