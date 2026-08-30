package com.sprinthon.focusclock

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.sprinthon.focusclock.data.FocusPreferencesRepository
import com.sprinthon.focusclock.domain.model.BackgroundType
import com.sprinthon.focusclock.domain.model.CuratedColors
import com.sprinthon.focusclock.domain.model.SlideshowInterval
import com.sprinthon.focusclock.domain.model.SlideshowTransition
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FocusBackgroundTest {

    private lateinit var context: Context
    private lateinit var repository: FocusPreferencesRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        repository = FocusPreferencesRepository(context)
    }

    @Test
    fun testCuratedColorsPaletteContainsExpectedGroups() {
        assertEquals(5, CuratedColors.Neutrals.size)
        assertEquals(5, CuratedColors.Calm.size)
        assertEquals(3, CuratedColors.Accents.size)
        assertEquals(13, CuratedColors.All.size)

        // AMOLED Black should be 0xFF000000
        val amoled = CuratedColors.findByHex(0xFF000000)
        assertNotNull(amoled)
        assertEquals("AMOLED Black", amoled?.name)
    }

    @Test
    fun testHexColorParsing() {
        val valid6Hex = CuratedColors.parseHexColor("#1A1A1E")
        assertEquals(0xFF1A1A1EL, valid6Hex)

        val valid8Hex = CuratedColors.parseHexColor("#FF1A1A1E")
        assertEquals(0xFF1A1A1EL, valid8Hex)

        val invalidHex = CuratedColors.parseHexColor("invalid-color")
        assertEquals(null, invalidHex)
    }

    @Test
    fun testSlideshowIntervalSeconds() {
        assertEquals(0L, SlideshowInterval.NEVER.seconds)
        assertEquals(5L, SlideshowInterval.FIVE_SEC.seconds)
        assertEquals(15L, SlideshowInterval.FIFTEEN_SEC.seconds)
        assertEquals(30L, SlideshowInterval.THIRTY_SEC.seconds)
        assertEquals(60L, SlideshowInterval.ONE_MIN.seconds)
        assertEquals(300L, SlideshowInterval.FIVE_MIN.seconds)
        assertEquals(600L, SlideshowInterval.TEN_MIN.seconds)
        assertEquals(900L, SlideshowInterval.FIFTEEN_MIN.seconds)
    }

    @Test
    fun testBackgroundTypeUpdatePersistence() = runTest {
        repository.updateBackgroundType(BackgroundType.SINGLE_IMAGE)
        val prefs1 = repository.preferencesFlow.first()
        assertEquals(BackgroundType.SINGLE_IMAGE, prefs1.backgroundType)

        repository.updateBackgroundType(BackgroundType.SLIDESHOW)
        val prefs2 = repository.preferencesFlow.first()
        assertEquals(BackgroundType.SLIDESHOW, prefs2.backgroundType)
    }

    @Test
    fun testSlideshowImagesCollectionPersistence() = runTest {
        val images = listOf("content://media/1", "content://media/2", "content://media/3")
        repository.updateSlideshowImageUris(images)
        repository.updateSlideshowInterval(SlideshowInterval.TEN_MIN)
        repository.updateSlideshowShuffle(true)

        val prefs = repository.preferencesFlow.first()
        assertEquals(3, prefs.slideshowImageUris.size)
        assertEquals("content://media/1", prefs.slideshowImageUris[0])
        assertEquals(SlideshowInterval.TEN_MIN, prefs.slideshowInterval)
        assertTrue(prefs.slideshowShuffle)
    }

    @Test
    fun testOverlayStrengthPersistence() = runTest {
        repository.updateOverlayStrength(0.45f)
        val prefs = repository.preferencesFlow.first()
        assertEquals(0.45f, prefs.backgroundOverlayStrength, 0.01f)
    }
}
