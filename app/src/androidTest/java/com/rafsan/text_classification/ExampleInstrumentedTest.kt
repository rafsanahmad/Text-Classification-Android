/*
 * *
 *  * Created by Rafsan Ahmad on 10/17/21, 1:36 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

package com.rafsan.text_classification

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.rafsan.text_classification", appContext.packageName)
    }
}