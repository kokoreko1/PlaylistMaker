package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesAPIService: ITunesAPI = retrofit.create(ITunesAPI::class.java)

    private val gson = Gson()

    private lateinit var sharedPrefs: SharedPreferences

    private val tracksAdapter = TracksAdapter(mutableListOf())

//    private var tracksHistory: MutableList<Track> = mutableListOf()

    private val tracksAdapterHistory = TracksAdapterHistory(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        val ivBack = findViewById<ImageView>(R.id.image_back)
        val etSearch = findViewById<EditText>(R.id.searchEditText)
        val btClear = findViewById<ImageView>(R.id.clearIcon)

        val rvTracks = findViewById<RecyclerView>(R.id.recycler_view_tracks)

        val ivNothingWasFound = findViewById<ImageView>(R.id.image_nothing_was_found)
        val tvNothingWasFound = findViewById<TextView>(R.id.text_nothing_was_found)
        val btConnectionProblems = findViewById<Button>(R.id.button_update)

        val vgHistory = findViewById<ViewGroup>(R.id.view_group_history)
        val rvTracksHistory = findViewById<RecyclerView>(R.id.recycler_view_tracks_history)

        val btClearHistory = findViewById<Button>(R.id.button_clear_the_history)

        // Создание экземпляра Shared Preferences.
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)

        val tracksAdapterHistory = TracksAdapterHistory(getLocalTracks())
        rvTracksHistory.adapter = tracksAdapterHistory

        // Заполнение поля поиска сохраненной переменной globalVarSavedSearchText
        etSearch.setText((application as AppPlaylistMaker).globalVarSavedSearchText)

        rvTracks.layoutManager = LinearLayoutManager(this)

        rvTracksHistory.layoutManager = LinearLayoutManager(this)
        rvTracksHistory.adapter = tracksAdapterHistory

        // Нажатие на кнопку возврат.
        ivBack.setOnClickListener {
            finish()
        }

        // Нажатие на кнопку "Обновить при проблемах с соединением".
        btConnectionProblems.setOnClickListener {
            // Получение трэков с сервера.
            searchTracks(etSearch.text.toString())
        }

        // Нажатие на кнопку Очистить историю.
        btClearHistory.setOnClickListener {
            saveLocalTracks(mutableListOf())
            vgHistory.isVisible = false
        }


        // Если фокус устанавливается на поле поиска и текст пустой,
        // тогда отражается группа элементов История поиска.
        etSearch.setOnFocusChangeListener { view, hasFocus ->

            val localTracks = getLocalTracks()

            if (etSearch.hasFocus() && etSearch.text.isEmpty() == true && localTracks.size > 0) {
                rvTracksHistory.adapter = TracksAdapterHistory(localTracks)
                vgHistory.isVisible = true
            } else {
                vgHistory.isVisible = false
            }

        }


        // Нажатие на кнопку очистки поля поиска
        btClear.setOnClickListener {

            // очищение поля поиска
            etSearch.setText("")

            // скрытие клавиатуры
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

            // скрытие картинки и текста "Ничего не найдено"
            ivNothingWasFound.visibility = View.GONE
            tvNothingWasFound.visibility = View.GONE

            rvTracks.adapter = tracksAdapter
        }

        // TextWatcher
        val simpleTextWatcher = object : SimpleTextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                // Кнопка очистить видна только, если в поле поиска введены символы
                btClear.isVisible = !s.isNullOrEmpty()

                // Группа истории выбранных трэков видна только тогда,
                // когда фокус установлен на поле поиска и поле поиска пустое

                val localTracks = getLocalTracks()

                if (etSearch.hasFocus() && s?.isEmpty() == true && localTracks.size > 0) {

                    rvTracksHistory.adapter = TracksAdapterHistory(localTracks)
                    vgHistory.isVisible = true

                } else {
                    vgHistory.isVisible = false
                }

            }

            // После введения символов сохраним их в переменной
            override fun afterTextChanged(s: Editable?) {
                (application as AppPlaylistMaker).globalVarSavedSearchText = s.toString()
            }

        }

        // Слушатель изменения текста в поле поиска
        etSearch.addTextChangedListener(simpleTextWatcher)


        // Нажатие на кнопку Done на клавиатуре появившейся при вводе в поле поиск.
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // получить трэки с сервера
                searchTracks(etSearch.text.toString())
                true
            }
            false
        }

    }

    // При смене ориентации экрана сохраняем последнюю строку поиска
    override fun onSaveInstanceState(outState: Bundle) {

        super.onSaveInstanceState(outState)

        val etSearch = findViewById<EditText>(R.id.searchEditText)
        outState.putString(SEARCH_EDIT_TEXT, etSearch.getText().toString())

    }

    // При смене ориентации экрана восстанавливаем последнюю строку поиска
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        super.onRestoreInstanceState(savedInstanceState)

        val etSearch = findViewById<EditText>(R.id.searchEditText)
        etSearch.setText(savedInstanceState.getString(SEARCH_EDIT_TEXT,""))

    }

    // Функция получения трэков с сервера и отражения их в списке
    private fun searchTracks(wordToSearch: String) {

        val ivNothingWasFound = findViewById<ImageView>(R.id.image_nothing_was_found)
        val tvNothingWasFound = findViewById<TextView>(R.id.text_nothing_was_found)

        val ivConnectionProblems = findViewById<ImageView>(R.id.image_connection_problems)
        val tvConnectionProblems = findViewById<TextView>(R.id.text_connection_problems)
        val btConnectionProblems = findViewById<Button>(R.id.button_update)

        val rvTracks = findViewById<RecyclerView>(R.id.recycler_view_tracks)

        iTunesAPIService.search(wordToSearch).enqueue(object : Callback<TracksResponse> {

            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                if (response.isSuccessful) {
                    if ((response.body()?.results?.size ?: 0) == 0) {

                        // Если ничего на сервере не найдено, показываем сообщение Ничего не найдено.
                        showMessage(TYPE_MESSAGE_NOTHING_WAS_FOUND)

                    } else {

                        // Если с сервера получены данные, тогда список трэков делаем видимым,
                        // а вспомогательные сообщения невидимыми.
                        rvTracks.isVisible = true
                        val tracksAdapter = TracksAdapter(response.body()?.results)

                        // Обработка нажатия на элемент списка найденных трэков.
                        tracksAdapter.onTrackClickListenerAdapter =
                            OnTrackClickListener{
                                addTrackInHistoryList(it)
                            }

                        rvTracks.adapter = tracksAdapter

                        ivNothingWasFound.isVisible = false
                        tvNothingWasFound.isVisible = false
                        ivConnectionProblems.isVisible = false
                        tvConnectionProblems.isVisible = false
                        btConnectionProblems.isVisible = false

                    }
                } else {
                    // при ошибках отражаем сообщение Проблемы с соединением
                    showMessage(TYPE_MESSAGE_CONNECTION_PROBLEMS)
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                // при сбое отражаем сообщение Проблемы с соединением
                showMessage(TYPE_MESSAGE_CONNECTION_PROBLEMS)
            }

            fun showMessage(tpMessage: Int) {
                when (tpMessage) {
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

    private fun addTrackInHistoryList(newTrack: Track) {

        val localTracks = getLocalTracks()

        // Нет сохраненных трэков в SharedPref.
        if (localTracks != null) {
            if (localTracks.isEmpty()) {
                localTracks.add(newTrack)
            } else {

                val foundTrack = localTracks.find{
                    it == newTrack
                }

                if (foundTrack == null) {

                    // Если в списке 10 трэков, тогда предварительно удаляем один трэк.
                    if (localTracks.size == 10) {
                        localTracks.removeAt(localTracks.size - 1)
                    }

                } else {
                    // Если выбранный трэк уже есть в списке истории,
                    // тогда удаляем его и добавляем первым в список.
                    localTracks.remove(newTrack)
                }

                localTracks.add(0, newTrack)

            }
        }

        if (localTracks != null) {
            saveLocalTracks(localTracks)
        }

    }

    fun saveLocalTracks(tracksList: MutableList<Track>) {
        val json: String = gson.toJson(tracksList)
        sharedPrefs
            .edit()
            .putString(TRACKS_HISTORY, json)
            .apply()
    }

    fun getLocalTracks(): MutableList<Track> {

        var tracksList: MutableList<Track> = mutableListOf()

        val json: String? = sharedPrefs.getString(TRACKS_HISTORY, null)

        if (json != null) {
            tracksList = gson.fromJson(json, object : TypeToken<MutableList<Track>>() {}.type)
        }

        return tracksList
    }

    companion object {

        const val SEARCH_EDIT_TEXT = "search_edit_text"

        const val TYPE_MESSAGE_NOTHING_WAS_FOUND = 1
        const val TYPE_MESSAGE_CONNECTION_PROBLEMS = 2

        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val TRACKS_HISTORY = "tracks_history"

    }
}
