package com.hush.whitesound.data.repository

import com.hush.whitesound.data.model.SoundCategory
import com.hush.whitesound.data.model.SoundItem
import com.hush.whitesound.data.model.SoundType

object SoundCatalog {

    val allSounds: List<SoundItem> = listOf(
        // ================= 🌙 깊은 잠 =================
        SoundItem(
            id = "gentle_rain",
            nameKo = "잔잔한 빗소리",
            categories = setOf(SoundCategory.SLEEP, SoundCategory.STUDY),
            type = SoundType.NATURE,
            isProcedural = false,
            assetFileName = "gentle_rain.ogg",
            description = "포근하고 규칙적인 실제 빗방울 소리",
            gradientStart = 0xFF3B4A85,
            gradientEnd = 0xFF1E2550,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "night_waves",
            nameKo = "밤바다 파도",
            categories = setOf(SoundCategory.SLEEP),
            type = SoundType.NATURE,
            isProcedural = false,
            assetFileName = "night_waves.ogg",
            description = "해변가에 철썩이며 부서지는 실제 밤바다 파도",
            gradientStart = 0xFF2A5C7E,
            gradientEnd = 0xFF173049,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "thunder_storm",
            nameKo = "폭풍과 천둥",
            categories = setOf(SoundCategory.SLEEP, SoundCategory.FOCUS),
            type = SoundType.NATURE,
            isProcedural = false,
            assetFileName = "thunder_storm.ogg",
            description = "우르릉거리는 천둥과 시원하게 쏟아지는 폭풍우",
            gradientStart = 0xFF242F4A,
            gradientEnd = 0xFF111624,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "fireplace",
            nameKo = "벽난로",
            categories = setOf(SoundCategory.SLEEP),
            type = SoundType.FIRE,
            isProcedural = false,
            assetFileName = "fireplace.ogg",
            description = "타닥타닥 장작이 타는 아늑한 실제 불소리",
            gradientStart = 0xFFA4553A,
            gradientEnd = 0xFF4A2230,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "brown_noise",
            nameKo = "브라운 노이즈",
            categories = setOf(SoundCategory.SLEEP),
            type = SoundType.NOISE,
            isProcedural = true,
            description = "낮은 저음역대의 부드럽고 묵직한 백색소음",
            gradientStart = 0xFF6B5146,
            gradientEnd = 0xFF2E2430,
            licenseNotice = "Procedural Audio Synthesizer (0MB Native)"
        ),
        SoundItem(
            id = "tent_rain",
            nameKo = "텐트 위 빗소리",
            categories = setOf(SoundCategory.SLEEP),
            type = SoundType.NATURE,
            isProcedural = false,
            assetFileName = "tent_rain.ogg",
            description = "천막 위로 투닥거리며 떨어지는 빗소리",
            gradientStart = 0xFF384570,
            gradientEnd = 0xFF182040,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "night_crickets",
            nameKo = "여름밤 풀벌레",
            categories = setOf(SoundCategory.SLEEP),
            type = SoundType.NATURE,
            isProcedural = false,
            assetFileName = "night_crickets.ogg",
            description = "한적한 시골밤 귀뚜라미와 풀벌레 합창",
            gradientStart = 0xFF2F5A4B,
            gradientEnd = 0xFF162B2A,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "delta_binaural_2hz",
            nameKo = "델타파 2Hz",
            categories = setOf(SoundCategory.SLEEP),
            type = SoundType.FREQUENCY,
            isProcedural = true,
            requiresHeadphones = true,
            description = "서파 수면 유도를 위한 바이노럴 비트 (이어폰 권장)",
            gradientStart = 0xFF5B4FA8,
            gradientEnd = 0xFF25204F,
            licenseNotice = "Procedural Audio Synthesizer (0MB Native)"
        ),
        SoundItem(
            id = "drone_432hz",
            nameKo = "432Hz 드론",
            categories = setOf(SoundCategory.SLEEP),
            type = SoundType.FREQUENCY,
            isProcedural = true,
            description = "몸과 마음을 편안하게 이완시키는 치유의 기음",
            gradientStart = 0xFF4E4885,
            gradientEnd = 0xFF1F1A42,
            licenseNotice = "Procedural Audio Synthesizer (0MB Native)"
        ),

        // ================= 🎯 집중 =================
        SoundItem(
            id = "white_noise",
            nameKo = "화이트 노이즈",
            categories = setOf(SoundCategory.FOCUS),
            type = SoundType.NOISE,
            isProcedural = true,
            description = "주변 잡음을 가장 효과적으로 마스킹하는 균일 소음",
            gradientStart = 0xFF525D7E,
            gradientEnd = 0xFF22283E,
            licenseNotice = "Procedural Audio Synthesizer (0MB Native)"
        ),
        SoundItem(
            id = "pink_noise",
            nameKo = "핑크 노이즈",
            categories = setOf(SoundCategory.FOCUS),
            type = SoundType.NOISE,
            isProcedural = true,
            description = "1/f 주파수로 자연스럽고 편안한 화이트 노이즈",
            gradientStart = 0xFF9A5A86,
            gradientEnd = 0xFF3B2342,
            licenseNotice = "Procedural Audio Synthesizer (0MB Native)"
        ),
        SoundItem(
            id = "forest_creek",
            nameKo = "숲속 계곡",
            categories = setOf(SoundCategory.FOCUS),
            type = SoundType.NATURE,
            isProcedural = false,
            assetFileName = "forest_creek.ogg",
            description = "청량하게 흘러가는 계곡 물소리와 미세한 새소리",
            gradientStart = 0xFF2E7F86,
            gradientEnd = 0xFF14383F,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "waterfall",
            nameKo = "폭포",
            categories = setOf(SoundCategory.FOCUS),
            type = SoundType.NATURE,
            isProcedural = false,
            assetFileName = "waterfall.ogg",
            description = "시원하게 쏟아져 내리는 웅장한 폭포수",
            gradientStart = 0xFF2D6F8A,
            gradientEnd = 0xFF163548,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "campfire",
            nameKo = "모닥불",
            categories = setOf(SoundCategory.FOCUS),
            type = SoundType.FIRE,
            isProcedural = false,
            assetFileName = "campfire.ogg",
            description = "야외 캠핑장에서 밤하늘 아래 타오르는 모닥불",
            gradientStart = 0xFF8C4332,
            gradientEnd = 0xFF3C1A1C,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "wind_breeze",
            nameKo = "바람 소리",
            categories = setOf(SoundCategory.FOCUS),
            type = SoundType.NATURE,
            isProcedural = false,
            assetFileName = "wind_breeze.ogg",
            description = "고원 지대를 지나는 시원하고 깨끗한 바람",
            gradientStart = 0xFF4C6C8A,
            gradientEnd = 0xFF22324A,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "cafe_ambience",
            nameKo = "카페 앰비언스",
            categories = setOf(SoundCategory.FOCUS),
            type = SoundType.AMBIENCE,
            isProcedural = false,
            assetFileName = "cafe_ambience.ogg",
            description = "잔 부딪히는 소리와 기분 좋은 은은한 웅성거림",
            gradientStart = 0xFF785E48,
            gradientEnd = 0xFF36281C,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "beta_binaural_18hz",
            nameKo = "베타파 18Hz",
            categories = setOf(SoundCategory.FOCUS),
            type = SoundType.FREQUENCY,
            isProcedural = true,
            requiresHeadphones = true,
            description = "두뇌 각성과 고도 집중을 돕는 바이노럴 비트 (이어폰 권장)",
            gradientStart = 0xFF386685,
            gradientEnd = 0xFF182D3E,
            licenseNotice = "Procedural Audio Synthesizer (0MB Native)"
        ),

        // ================= 📖 공부 =================
        SoundItem(
            id = "library_ambience",
            nameKo = "도서관 앰비언스",
            categories = setOf(SoundCategory.STUDY),
            type = SoundType.AMBIENCE,
            isProcedural = false,
            assetFileName = "library_ambience.ogg",
            description = "조용한 열람실의 사각거리는 연필과 책장 소리",
            gradientStart = 0xFF4F5D73,
            gradientEnd = 0xFF232B38,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "window_rain",
            nameKo = "창가 빗소리",
            categories = setOf(SoundCategory.STUDY),
            type = SoundType.NATURE,
            isProcedural = false,
            assetFileName = "window_rain.ogg",
            description = "공부방 유리창을 토닥이는 맑은 빗방울",
            gradientStart = 0xFF3C587A,
            gradientEnd = 0xFF1B2A40,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "calm_stream",
            nameKo = "잔잔한 시냇물",
            categories = setOf(SoundCategory.STUDY),
            type = SoundType.NATURE,
            isProcedural = false,
            assetFileName = "calm_stream.ogg",
            description = "돌 틈을 부드럽게 굽이쳐 흐르는 맑은 개울",
            gradientStart = 0xFF326A7A,
            gradientEnd = 0xFF15323D,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "wood_stove",
            nameKo = "장작 난로",
            categories = setOf(SoundCategory.STUDY),
            type = SoundType.FIRE,
            isProcedural = false,
            assetFileName = "wood_stove.ogg",
            description = "겨울 서재의 무쇠 난로에서 타는 은은한 불꽃",
            gradientStart = 0xFF75453B,
            gradientEnd = 0xFF331E18,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "forest_wind",
            nameKo = "산들바람 숲",
            categories = setOf(SoundCategory.STUDY),
            type = SoundType.NATURE,
            isProcedural = false,
            assetFileName = "forest_wind.ogg",
            description = "푸른 숲속 나뭇잎을 스치는 청명한 바람결",
            gradientStart = 0xFF385A48,
            gradientEnd = 0xFF182D22,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "green_noise",
            nameKo = "그린 노이즈",
            categories = setOf(SoundCategory.STUDY),
            type = SoundType.NOISE,
            isProcedural = false,
            assetFileName = "green_noise.ogg",
            description = "자연계의 중간 음역대를 닮은 가장 차분한 소음",
            gradientStart = 0xFF436652,
            gradientEnd = 0xFF1D3325,
            licenseNotice = "Open Ambient Archive - CC0"
        ),
        SoundItem(
            id = "alpha_binaural_10hz",
            nameKo = "알파파 10Hz",
            categories = setOf(SoundCategory.STUDY),
            type = SoundType.FREQUENCY,
            isProcedural = true,
            requiresHeadphones = true,
            description = "차분한 몰입과 장기 기억력 유지를 돕는 주파수 (이어폰 권장)",
            gradientStart = 0xFF4A6D5D,
            gradientEnd = 0xFF1D382B,
            licenseNotice = "Procedural Audio Synthesizer (0MB Native)"
        ),
        SoundItem(
            id = "gamma_tone_40hz",
            nameKo = "40Hz 감마",
            categories = setOf(SoundCategory.STUDY),
            type = SoundType.FREQUENCY,
            isProcedural = true,
            description = "두뇌 정보처리와 인지 활성화를 돕는 등시성 톤",
            gradientStart = 0xFF526A4A,
            gradientEnd = 0xFF25361F,
            licenseNotice = "Procedural Audio Synthesizer (0MB Native)"
        )
    )

    fun getSoundsByCategory(category: SoundCategory): List<SoundItem> {
        return allSounds.filter { it.categories.contains(category) }
    }

    fun getSoundById(id: String): SoundItem? {
        return allSounds.find { it.id == id }
    }
}
