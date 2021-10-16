package com.rafsan.text_classification

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.rafsan.text_classification.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private val TAG = "TextClassificationDemo"

    private lateinit var binding: ActivityMainBinding
    private var executorService: ExecutorService? = null

    // TODO 5: Define a NLClassifier variable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.v(TAG, "onCreate")
        executorService = Executors.newSingleThreadExecutor()
        binding.predictButton.setOnClickListener(
            View.OnClickListener { v: View? ->
                classify(
                    binding.inputText.getText().toString()
                )
            })

        // TODO 3: Call the method to download TFLite model
    }

    /** Send input text to TextClassificationClient and get the classify messages.  */
    private fun classify(text: String) {
        executorService?.execute {

            // TODO 7: Run sentiment analysis on the input text

            // TODO 8: Convert the result to a human-readable text
            val textToShow = "Text classification result.\n"

            // Show classification result on screen
            showResult(textToShow)
        }
    }

    /** Show classification result on the screen.  */
    private fun showResult(textToShow: String) {
        // Run on UI thread as we'll updating our app UI
        runOnUiThread {

            // Append the result to the UI.
            binding.resultTextView.append(textToShow)

            // Clear the input text.
            binding.inputText.text.clear()

            // Scroll to the bottom to show latest entry's classification result.
            binding.scrollView.post { binding.scrollView.fullScroll(View.FOCUS_DOWN) }
        }
    }

    // TODO 2: Implement a method to download TFLite model from Firebase
}