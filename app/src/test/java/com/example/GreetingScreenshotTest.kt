package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.*
import com.example.ui.components.TopBrandBar
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleUser = UserAccount(
        id = "usr-01",
        username = "supreme_admin",
        fullName = "الرئيس التنفيذي والقائد الأعلى",
        roleRank = RoleRank.SUPREME_COMMANDER,
        departmentAr = "القيادة العليا",
        departmentEn = "Supreme Command",
        assignedCode = "1073781088@0503026675#8054\$8051%",
        canRead = true,
        canWrite = true,
        canExecute = true,
        canAdminister = true,
        canPurge = true,
        isMasterOverride = true
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        TopBrandBar(
          language = AppLanguage.ARABIC,
          activeUser = sampleUser,
          isMasterUnlocked = true,
          onToggleLanguage = {},
          onOpenMasterDialog = {},
          onRoleBadgeClick = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
