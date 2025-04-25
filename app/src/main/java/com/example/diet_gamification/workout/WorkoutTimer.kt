package com.example.diet_gamification.workout

import android.os.CountDownTimer

class WorkoutTimer(private val duration: Int, private val rest: Int, private val onTick: (Long) -> Unit, private val onFinish: () -> Unit) {
    private val totalTime = (duration + rest) * 1000L

    fun start() {
        object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(millisUntilFinished)
            }

            override fun onFinish() {
                onFinish()
            }
        }.start()
    }
}
