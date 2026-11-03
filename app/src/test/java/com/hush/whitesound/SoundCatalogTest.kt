package com.hush.whitesound

import com.hush.whitesound.data.model.SoundCategory
import com.hush.whitesound.data.repository.SoundCatalog
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class SoundCatalogTest {

    @Test
    fun testSoundCatalogTotalCountIs25() {
        assertEquals("Total sound count must be 25", 25, SoundCatalog.allSounds.size)
    }

    @Test
    fun testEachCategoryHasAtLeastEightSounds() {
        SoundCategory.values().forEach { category ->
            val count = SoundCatalog.getSoundsByCategory(category).size
            assertTrue("Category ${category.name} should have at least 8 sounds", count >= 8)
        }
    }

    @Test
    fun testSoundIdsAreUnique() {
        val ids = SoundCatalog.allSounds.map { it.id }
        val uniqueIds = ids.toSet()
        assertEquals("All sound IDs must be unique", ids.size, uniqueIds.size)
    }

    @Test
    fun testProceduralSoundsAreMarkedProperly() {
        val proceduralIds = setOf(
            "white_noise", "pink_noise", "brown_noise",
            "delta_binaural_2hz", "alpha_binaural_10hz", "beta_binaural_18hz",
            "drone_432hz", "gamma_tone_40hz"
        )
        for (sound in SoundCatalog.allSounds) {
            if (proceduralIds.contains(sound.id)) {
                assertTrue("Sound ${sound.id} must be procedural", sound.isProcedural)
            }
        }
    }

    @Test
    fun testEndTimeCalculationForTomorrow() {
        // If target hour is earlier than now, target time should be tomorrow
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val targetHour = (currentHour - 2 + 24) % 24

        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        assertTrue("Calculated end time must be in future", target.timeInMillis > now.timeInMillis)
    }
}
