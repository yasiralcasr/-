package com.example.data.model

data class EnterpriseProgram(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val sectorAr: String,
    val sectorEn: String,
    val sectorType: SectorType,
    val descriptionAr: String,
    val descriptionEn: String,
    val automationLevel: String,
    val status: ProgramStatus,
    val targetAudienceAr: String,
    val targetAudienceEn: String,
    val generatedUsername: String,
    val generatedPassword: String,
    val systemKey: String,
    val integrationEndpoints: List<String>,
    val createdAt: String,
    val isApprovedForClients: Boolean = true
)

enum class SectorType(val labelAr: String, val labelEn: String, val icon: String) {
    COMMERCIAL("قطاع تجاري ومؤسسات", "Commercial & Enterprise", "🏢"),
    GOVERNMENT("قطاع حكومي وسيادي", "Government & Sovereign", "🏛️"),
    NON_PROFIT("قطاع غير ربحي وخيري", "Non-Profit & Humanitarian", "🤝"),
    INDUSTRIAL("قطاع صناعي وتحكم آلي", "Industrial & Automation", "⚙️")
}

enum class ProgramStatus(val labelAr: String, val labelEn: String, val colorHex: Long) {
    ACTIVE("نشط ومتصل بالخوادم", "Active & Connected", 0xFF10B981),
    DEVELOPING("قيد التطوير والربط", "In Development", 0xFFF59E0B),
    COMPLETED("تم الانتهاء والاعتماد", "Completed & Certified", 0xFF00B4D8),
    SECURED("مؤمّن بتشفير خاص", "Secured & Encrypted", 0xFFD4AF37)
}
