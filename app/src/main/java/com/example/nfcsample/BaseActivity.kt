package com.example.nfcsample

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity: AppCompatActivity() {

    abstract fun initViews()

    abstract fun initInteractions()
}