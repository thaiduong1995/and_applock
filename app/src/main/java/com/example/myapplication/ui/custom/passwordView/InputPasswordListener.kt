package com.example.myapplication.ui.custom.passwordView

interface InputPasswordListener {
    fun onStartInput()
    fun onInputComplete(password: String)
    fun onInputting()
}