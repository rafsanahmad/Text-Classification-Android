/*
 * *
 *  * Created by Rafsan Ahmad on 10/17/21, 1:36 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

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
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private val TAG = "TextClassificationDemo"

    private lateinit var binding: ActivityMainBinding
    private var executorService: ExecutorService? = null
    private lateinit var downloadTrace: Trace
    private val firebasePerformance = FirebasePerformance.getInstance()
    private var textClassifier: NLClassifier? = null

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

            //Run sentiment analysis on the input text
            textClassifier?.let { classifier ->
                val results: List<Category> = classifier.classify(text)
                //Convert the result to a human-readable text
                var textToShow = "Input: $text\nOutput:\n"
                for (i in results.indices) {
                    val result: Category = results.get(i)
                    var expression = "Positive"
                    if (result.label == "0") {
                        expression = "Negative"
                    }
                    textToShow += String.format(
                        "    %s: %s\n", expression,
                        result.score
                    )
                }
                textToShow += "---------\n"

                // Show classification result on screen
                showResult(textToShow)
            }
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
                    textClassifier = NLClassifier.createFromFile(modelFile);
                    binding.predictButton.isEnabled = true
                    downloadTrace.stop()
                } else {
                    showToast("Failed to get model file.")
                }
            }
            .addOnFailureListener { it ->
                showToast("Exception occurred in downloading model.")
                Log.e(TAG, it.localizedMessage)
                binding.predictButton.isEnabled = false
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