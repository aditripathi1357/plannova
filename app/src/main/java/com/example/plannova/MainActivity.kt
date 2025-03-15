package com.example.plannova

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)



        // Reference to the ImageView
        val imageView = findViewById<ImageView>(R.id.imageView)

        // Animate the scale (grow from inside to outside)
        val animator = ValueAnimator.ofFloat(0.1f, 1.5f) // Scale from 0.1x to 1.5x
        animator.duration = 2000 // Animation duration in milliseconds
        animator.interpolator = AccelerateInterpolator() // Smooth acceleration

        animator.addUpdateListener { animation ->
            val scale = animation.animatedValue as Float
            imageView.scaleX = scale
            imageView.scaleY = scale
        }

        animator.start()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        },3000)
    }
}