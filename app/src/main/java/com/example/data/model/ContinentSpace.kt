package com.example.data.model

enum class ContinentKey(
    val id: String,
    val icon: String,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val colorHex: Long
) {
    GOVERNMENT_GATE(
        id = "GOVERNMENT_GATE",
        icon = "🏛️",
        titleAr = "قارة السيادة والمنصات الحكومية",
        titleEn = "Government Sovereignty Continent",
        descriptionAr = "قارة السيادة والمنصات الحكومية الرسمية لخدمة البشر (أبشر، قوى، مدد، ناجز، بلدي، التأمينات، الزكاة، مقيم، صحة).",
        descriptionEn = "Official sovereign platforms and government gateways for civic service.",
        colorHex = 0xFFD4AF37 // Gold
    ),
    CHILDREN_HARBOR(
        id = "CHILDREN_HARBOR",
        icon = "👶",
        titleAr = "قارة الأجيال والطفولة الآمنة",
        titleEn = "Children & Generational Safe Harbor",
        descriptionAr = "فضاء نقي مخصص لتعليم وبث القيم النبيلة، حفظ الفطرة، البرامج التعليمية الهادفة، وحماية الأجيال من الشوائب.",
        descriptionEn = "Safe generational space for noble values, pure education, and digital innocence preservation.",
        colorHex = 0xFF38BDF8 // Light Sky Cyan
    ),
    FINANCIAL_MATRIX(
        id = "FINANCIAL_MATRIX",
        icon = "💼",
        titleAr = "المجرة المالية والمصرفية",
        titleEn = "Financial & Banking Matrix",
        descriptionAr = "المجرة المالية المصرفية وحسابات الأفراد وعمليات الأموال الحصرية (مصرف الراجحي أفراد، اعتمادات التجارة، المقاصة الدولية).",
        descriptionEn = "Exclusive banking gateways, Al Rajhi individual portal, and enterprise settlement engines.",
        colorHex = 0xFF10B981 // Emerald
    ),
    KNOWLEDGE_OASIS(
        id = "KNOWLEDGE_OASIS",
        icon = "🎬",
        titleAr = "واحة المعرفة والوثائقيات الهادفة",
        titleEn = "Knowledge Oasis & Meaningful Media",
        descriptionAr = "واحة المعرفة والترفيه التوعوي، الوثائقيات التاريخية والعلمية، والمسلسلات الهادفة التي تبني العقل والروح.",
        descriptionEn = "Purposeful media, historical documentaries, and enriching intellectual content.",
        colorHex = 0xFFA855F7 // Purple
    ),
    BLACK_BASKET(
        id = "BLACK_BASKET",
        icon = "🕳️",
        titleAr = "السلة السوداء (حجر كاشف المستور)",
        titleEn = "The Black Basket Quarantine",
        descriptionAr = "السلة السوداء لعزل الخبيث وحجر الإعلانات والمصايد الحديثة والمواقع المزورة، مع الخطاب الوعظي لردع الباطل.",
        descriptionEn = "Strict quarantine isolating deceitful domains, phishing traps, and fraudulent actors.",
        colorHex = 0xFFEF4444 // Red Danger
    ),
    ALTRUISM_OASIS(
        id = "ALTRUISM_OASIS",
        icon = "🌊",
        titleAr = "صندوق العطاء والتحلية التلقائي",
        titleEn = "The Altruism & Water Desalination Pool",
        descriptionAr = "اقتطاع ثلث العوائد والأرباح (33%) آلياً لتوزيعها وتسييلها لإخواننا المحتاجين ليفيض بالخير كبحر ماء محلى.",
        descriptionEn = "Autonomous 33% revenue altruism engine channeling sustainable relief and clean water.",
        colorHex = 0xFF06B6D4 // Deep Cyan
    )
}
