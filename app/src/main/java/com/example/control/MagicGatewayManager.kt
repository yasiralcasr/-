package com.example.control

import android.content.Context
import android.content.Intent
import android.net.Uri

object MagicGatewayManager {
    // خريطة الروابط الذكية للقفز المباشر وتخطي العوائق إلى منصات الخدمات
    val appGatewayMap = mapOf(
        "قوى" to "https://qiwa.sa",
        "qiwa" to "https://qiwa.sa",
        "أبشر" to "https://absher.sa",
        "absher" to "https://absher.sa",
        "مدد" to "https://mudad.com.sa",
        "mudad" to "https://mudad.com.sa",
        "التأمينات" to "https://www.gosi.gov.sa",
        "gosi" to "https://www.gosi.gov.sa",
        "الزكاة والضريبة" to "https://zatca.gov.sa",
        "zatca" to "https://zatca.gov.sa",
        "مقيم" to "https://muqeem.sa",
        "muqeem" to "https://muqeem.sa",
        "بلدي" to "https://balady.gov.sa",
        "balady" to "https://balady.gov.sa",
        "ناجز" to "https://najiz.sa",
        "najiz" to "https://najiz.sa",
        "منصة اعتماد" to "https://etimad.sa",
        "etimad" to "https://etimad.sa",
        "منصة صحة" to "https://seha.sa",
        "seha" to "https://seha.sa"
    )

    fun routeToApp(context: Context, appName: String): Intent {
        val targetUrl = appGatewayMap[appName.trim().lowercase()]
            ?: if (appName.startsWith("http://") || appName.startsWith("https://")) {
                appName
            } else {
                "https://google.com/search?q=${Uri.encode(appName)}"
            }

        // الانتقال الفوري وتخطي العوائق إلى الهدف الأساسي
        return Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun openGateway(context: Context, appName: String) {
        val intent = routeToApp(context, appName)
        context.startActivity(intent)
    }
}
