package com.rafsan.text_classification

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.rafsan.text_classification.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private val TAG = "TextClassificationDemo"

    private lateinit var binding: ActivityMainBinding
    private var executorService: ExecutorService? = null
    private lateinit var downloadTrace: Trace
    private val firebasePerformance = FirebasePerformance.getInstance()

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
        setupTextClassifier()
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

    private fun setupTextClassifier() {
        // Add these lines to create and start the trace
        downloadTrace = firebasePerformance.newTrace("download_model")
        downloadTrace.start()
        downloadModel("sentiment_analysis")
    }

    private fun downloadModel(modelName: String) {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(
                modelName, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions
            )
            .addOnSuccessListener { model: CustomModel? ->
                // Download complete. Depending on your app, you could enable the ML
                // feature, or switch from the local model to the remote model, etc.

                // The CustomModel object contains the local path of the model file,
                // which you can use to instantiate a TensorFlow Lite interpreter.
                val modelFile = model?.file
                if (modelFile != null) {
                    showToast("Downloaded remote model: $model")
                    //var interpreter = Interpreter(modelFile)

                    downloadTrace.stop()
                } else {
                    showToast("Failed to get model file.")
                }
            }
            .addOnFailureListener {
                showToast("Exception occurred in downloading model.")
            }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            this,
            text,
            Toast.LENGTH_LONG
        ).show()
    }
}