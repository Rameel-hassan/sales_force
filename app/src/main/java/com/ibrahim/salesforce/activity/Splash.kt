package com.ibrahim.salesforce.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.ibrahim.salesforce.R
import com.ibrahim.salesforce.network.AppWebServices
import com.ibrahim.salesforce.utilities.AppKeys
import com.ibrahim.salesforce.utilities.AppPreference

class Splash : Activity() {
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        imageView = findViewById(R.id.logo)
        if (AppPreference.getValue(this, AppKeys.KEY_BASE_URL) == null)
            AppPreference.saveValue(this, AppWebServices.BASE_URL, AppKeys.KEY_BASE_URL)
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate)
        imageView.startAnimation(animation)

        val myThread = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(2000)
                    startActivity(Intent(applicationContext, FragmentShownActivity::class.java))
                    finish()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }
        myThread.start()
        Log.d("class_name", this.javaClass.simpleName)
    }
}