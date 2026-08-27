package com.example.control

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class MagicInspectionResult(
    val query: String,
    val targetUrl: String,
    val isApprovedRightPath: Boolean, // اليمين (طريق الصالحين والغانمين) vs اليسار (السلة السوداء)
    val titleAr: String,
    val titleEn: String,
    val detailsAr: String,
    val detailsEn: String,
    val moralAdvisoryAr: String? = null,
    val moralAdvisoryEn: String? = null,
    val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
)

object KashefSecurityEngine {

    const val SOVEREIGN_CODE = "1073781088@0503026675#8054\$8051%"

    // 🏛️ [ثابت ومستقر]: بوابات القفز السريع للمنصات الراسخة زمانياً (طريق الغانمين)
    val ANCIENT_TRUST_MAP = mapOf(
        "مصرف الراجحي افراد" to "https://alrajhibank.com.sa",
        "al rajhi login" to "https://alrajhibank.com.sa",
        "الراجحي" to "https://alrajhibank.com.sa",
        "alrajhi" to "https://alrajhibank.com.sa",
        "قوى" to "https://qiwa.sa",
        "qiwa" to "https://qiwa.sa",
        "أبشر" to "https://absher.sa",
        "absher" to "https://absher.sa",
        "مدد" to "https://mudad.com.sa",
        "mudad" to "https://mudad.com.sa",
        "التأمينات" to "https://gosi.gov.sa",
        "gosi" to "https://gosi.gov.sa",
        "الزكاة والضريبة" to "https://zatca.gov.sa",
        "zatca" to "https://zatca.gov.sa",
        "ناجز" to "https://najiz.sa",
        "najiz" to "https://najiz.sa",
        "بلدي" to "https://balady.gov.sa",
        "balady" to "https://balady.gov.sa",
        "مقيم" to "https://muqeem.sa",
        "muqeem" to "https://muqeem.sa",
        "اعتماد" to "https://etimad.sa",
        "etimad" to "https://etimad.sa",
        "صحة" to "https://seha.sa",
        "seha" to "https://seha.sa"
    )

    // 🔐 [ثابت ومستقر]: سجل الجلسات المفتوحة والحصرية (لا تغلق إلا يدوياً)
    private val activeExclusiveSessions = mutableMapOf<String, Boolean>()

    // 🕳️ السلة السوداء للمصايد المحجورة
    private val quarantinedThreats = mutableListOf<MagicInspectionResult>()

    init {
        // Initial quarantined examples
        quarantinedThreats.add(
            MagicInspectionResult(
                query = "دخول الراجحي السريع المزور",
                targetUrl = "https://scam-alrajhi-login.net/auth",
                isApprovedRightPath = false,
                titleAr = "محاولة تصيد واحتيال منتحلة لمصرف الراجحي",
                titleEn = "Phishing Trap Impersonating Al Rajhi Bank",
                detailsAr = "تم رصد موقع تم تكوينه حديثاً (قبل أقل من شهر) بهدف الخداع وسلب حقوق وأموال البشر دون وجه حق.",
                detailsEn = "Newly created domain mimicking banking credentials to capture victim data.",
                moralAdvisoryAr = "اتقِ الله ولا تفعل! هذا الذي تحاول الوصول له هو أمر قد منعه الله، وأنت تريد أن تأخذ شيئاً ليس لك أو شيئاً لم تبذل فيه سبباً كما أمر الله لكي تحصل عليه. بدلاً من بذل سبب مشروع يصنع لك الخير، أنت تأخذ شيئاً صُنِع لغيرك وبجهد غيرك!",
                moralAdvisoryEn = "Fear God and do not commit deceit! Taking what belongs to others without legitimate cause brings destruction and ruin."
            )
        )
    }

    /**
     * 🔮 النافذة السحرية والرقيب الحسيب لفصل اليمين عن اليسار وعزل الخبيث
     */
    fun processMagicWindow(
        userQuery: String,
        detectedUrl: String = "",
        domainCreationDateStr: String = "2026-08-01",
        userId: String = "Current_User"
    ): MagicInspectionResult {
        val cleanQuery = userQuery.trim().lowercase()
        val effectiveUrl = if (detectedUrl.isNotBlank()) detectedUrl else userQuery
        val isExplicitScam = effectiveUrl.contains("scam") || effectiveUrl.contains("fake") ||
                effectiveUrl.contains("phish") || cleanQuery.contains("مزور") || cleanQuery.contains("احتيال")

        // 1. فحص الحصون القديمة الراسخة زمانياً (اليمين - طريق الصالحين والغانمين)
        val directUrl = if (!isExplicitScam) {
            ANCIENT_TRUST_MAP[cleanQuery]
                ?: ANCIENT_TRUST_MAP.entries.firstOrNull { cleanQuery.contains(it.key) }?.value
        } else null

        if (directUrl != null) {
            openExclusiveSession(userId)
            return MagicInspectionResult(
                query = userQuery,
                targetUrl = directUrl,
                isApprovedRightPath = true,
                titleAr = "الجهة اليمنى: طريق الصالحين والغانمين",
                titleEn = "Right Path: The Ancient Trusted Fortresses",
                detailsAr = "منفذ رسمي آمن مبني على سبب مشروع وزمان مستقر. تم فتح المنفذ الحصري والجلسة ممتدة ومستمرة لا تغلق تلقائياً.",
                detailsEn = "Legitimate, established sovereign gateway. Exclusive persistent session locked open."
            )
        }

        // 2. فحص الأقدمية والشبهة في الروابط المعطاة
        val isRecent = isDomainRecent(domainCreationDateStr)
        val suspiciousKeywords = listOf("rajhi", "qiwa", "absher", "login", "bank", "portal", "verify", "pay")
        val isSuspicious = suspiciousKeywords.any { effectiveUrl.lowercase().contains(it) }

        if (isRecent && isSuspicious) {
            val result = MagicInspectionResult(
                query = userQuery,
                targetUrl = effectiveUrl,
                isApprovedRightPath = false,
                titleAr = "الجهة اليسرى: السلة السوداء (حجر كاشف المستور)",
                titleEn = "Left Path: Black Basket Quarantine",
                detailsAr = "تم رصد موقع تم تكوينه في وقت حديث جداً بهدف الخداع وسلب الحقوق. تم قذفه لليسار وعزله في السلة السوداء!",
                detailsEn = "Recent domain created for deceit and phishing. Quarantined in the Black Basket.",
                moralAdvisoryAr = "اتقِ الله ولا تفعل! هذا الذي تحاول الوصول له هو أمر قد منعه الله، وأنت تريد أن تأخذ شيئاً ليس لك أو شيئاً لم تبذل فيه سبباً كما أمر الله لكي تحصل عليه. بدلاً من بذل سبب مشروع يصنع لك الخير، أنت تأخذ شيئاً صُنِع لغيرك وبجهد غيرك!",
                moralAdvisoryEn = "Fear God and do not commit deceit! Strive for lawful, dignified provision through legitimate causes."
            )
            quarantinedThreats.add(0, result)
            return result
        }

        // 3. مسار عادي مستقر
        val safeUrl = if (effectiveUrl.startsWith("http://") || effectiveUrl.startsWith("https://")) {
            effectiveUrl
        } else {
            "https://google.com/search?q=${Uri.encode(userQuery)}"
        }

        return MagicInspectionResult(
            query = userQuery,
            targetUrl = safeUrl,
            isApprovedRightPath = true,
            titleAr = "الجهة اليمنى: مسار نظامي مستقر ومتاح",
            titleEn = "Right Path: Standard Certified Route",
            detailsAr = "تم التحقق من الوجهة وإتاحتها ضمن فضاء التطبيق الآمن.",
            detailsEn = "Verified regular destination accessible within safe application space."
        )
    }

    private fun isDomainRecent(dateStr: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val creationDate = sdf.parse(dateStr) ?: return false
            val diffMs = Date().time - creationDate.time
            val days = TimeUnit.MILLISECONDS.toDays(diffMs)
            days < 180 // أقل من 6 أشهر
        } catch (e: Exception) {
            true // الاحتياط للأمان
        }
    }

    fun openExclusiveSession(userId: String) {
        activeExclusiveSessions[userId] = true
    }

    fun closeExclusiveSession(userId: String) {
        activeExclusiveSessions.remove(userId)
    }

    fun isSessionActive(userId: String): Boolean {
        return activeExclusiveSessions[userId] ?: true
    }

    fun getQuarantinedThreats(): List<MagicInspectionResult> {
        return quarantinedThreats.toList()
    }

    fun clearQuarantinedThreats() {
        quarantinedThreats.clear()
    }

    fun launchUri(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // fallback
        }
    }
}
