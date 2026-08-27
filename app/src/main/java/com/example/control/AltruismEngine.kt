package com.example.control

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AltruismDistributionEvent(
    val id: String,
    val totalRevenue: Double,
    val charityShare: Double,
    val retainedShare: Double,
    val currency: String,
    val sourceDescriptionAr: String,
    val sourceDescriptionEn: String,
    val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
)

object AltruismEngine {

    var charityPoolBalance: Double = 185000.0 // Balance initialized from East-West Global revenue shares
    var totalRevenueProcessed: Double = 560000.0
    var totalDesalinatedDistributed: Double = 185000.0

    private val distributionEvents = mutableListOf<AltruismDistributionEvent>()

    init {
        // Initial seeded altruism distribution events
        distributionEvents.add(
            AltruismDistributionEvent(
                id = "altru-01",
                totalRevenue = 150000.0,
                charityShare = 49500.0,
                retainedShare = 100500.0,
                currency = "USD",
                sourceDescriptionAr = "عوائد توريد توربينات LK-W الهيدروليكية 9800 لقطاع الطاقة",
                sourceDescriptionEn = "LK-W 9800 Industrial Turbines supply contract proceeds"
            )
        )
        distributionEvents.add(
            AltruismDistributionEvent(
                id = "altru-02",
                totalRevenue = 85000.0,
                charityShare = 28050.0,
                retainedShare = 56950.0,
                currency = "SAR",
                sourceDescriptionAr = "رسوم اشتراكات الأتمتة الموحدة للقطاع التجاري والمصرفي",
                sourceDescriptionEn = "Enterprise commercial automation subscription pool"
            )
        )
    }

    /**
     * اقتطاع ثلث الأرباح (33%) تلقائياً لأعمال العطاء والتحلية وتسييلها لإخواننا المحتاجين
     */
    fun injectRevenueAndDistribute(
        amount: Double,
        currency: String = "SAR",
        sourceDescAr: String = "عائد مشروع أتمتة وتوريد LK-W",
        sourceDescEn: String = "LK-W Automation & Supply Revenue"
    ): AltruismDistributionEvent {
        val charityShare = amount * 0.33
        val retainedShare = amount - charityShare

        charityPoolBalance += charityShare
        totalRevenueProcessed += amount
        totalDesalinatedDistributed += charityShare

        val event = AltruismDistributionEvent(
            id = "altru-${System.currentTimeMillis() % 10000}",
            totalRevenue = amount,
            charityShare = charityShare,
            retainedShare = retainedShare,
            currency = currency,
            sourceDescriptionAr = sourceDescAr,
            sourceDescriptionEn = sourceDescEn
        )

        distributionEvents.add(0, event)
        return event
    }

    fun getDistributionHistory(): List<AltruismDistributionEvent> {
        return distributionEvents.toList()
    }
}
