package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

const val TAG = "GameViewModel"

//Vibration pattern:
private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)


class GameViewModel : ViewModel() {

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 10000L
    }

    //Initialise timer:
    private val timer: CountDownTimer

    private var _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    // Transformations.map() to get the current time in string format:
    val currentTimeString: LiveData<String> = Transformations.map(currentTime){
        DateUtils.formatElapsedTime(it)
    }

    // The current word
    private var _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    // The current score
    private var _score = MutableLiveData<Int>() // MutableLiveData is always null and need to be initialised.
    val score: LiveData<Int>
        get() = _score

    private var _eventGameFinished = MutableLiveData<Boolean>()
    val eventGameFinished: LiveData<Boolean>
        get() = _eventGameFinished

    // Event that triggers the phone to buzz using different patterns, determined by BuzzType
    private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        Log.i(TAG, "GameViewModel is created")

        resetList()
        nextWord()

        _eventGameFinished.value = false
        _score.value = 0 // Initialise the "score" using the value function.
      //  _word.value = "" // Initialise the "word" using the value function.
        _currentTime.value = 0
        //Create the countDown timer:
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onFinish() {
                _currentTime.value = DONE
                _eventGameFinished.value = true

                _eventBuzz.value = BuzzType.GAME_OVER
            }

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = (millisUntilFinished / ONE_SECOND)

            }
        }

        timer.start()

    }

    override fun onCleared() {
        super.onCleared()

        Log.i(TAG, "GameViewModel is destroyed")
        timer.cancel()
    }


    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            // When there is no word in the list: Game is finished.
//            _eventGameFinished.value = true
            resetList() // No need to worry about game ending. Because we are adding timer to end the game.
        }
//        else {
        _word.value = wordList.removeAt(0)
//        }
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        _score.value = score.value?.minus(1) // Null check and decrement
        nextWord()
    }

    fun onCorrect() {
        _score.value = score.value?.plus(1)  // Null check and increment
        _eventBuzz.value = BuzzType.CORRECT
        nextWord()
    }

    fun onGameFinished() {
        _eventGameFinished.value = false // Change the current state of viewModel when the game finishes.
    }

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

}