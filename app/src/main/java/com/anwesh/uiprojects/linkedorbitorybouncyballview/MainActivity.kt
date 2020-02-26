package com.anwesh.uiprojects.linkedorbitorybouncyballview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.orbitorybouncyballview.OrbitoryBouncyBallView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OrbitoryBouncyBallView.create(this)
    }
}
