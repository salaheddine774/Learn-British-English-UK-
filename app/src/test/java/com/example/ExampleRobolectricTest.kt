package com.example

import android.content.Context
import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.BritishLearningViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Learn British English", appName)
  }

  @Test
  fun `viewmodel initialization succeeds`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = BritishLearningViewModel(app)
    assertNotNull(viewModel)
  }
}
