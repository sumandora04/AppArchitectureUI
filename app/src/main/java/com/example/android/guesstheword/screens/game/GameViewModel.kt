package com.example.android.guesstheword.screens.game

import android.util.Log
import androidx.lifecycle.ViewModel

const val TAG ="GameViewModel"
class GameViewModel:ViewModel() {
    init {
        Log.i(TAG, "GameViewModel is created")
    }

    override fun onCleared() {
        super.onCleared()

        Log.i(TAG, "GameViewModel is destroyed")
    }

}