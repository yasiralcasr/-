package com.example.data.model

data class IndustrialProduct(
    val id: String,
    val modelCode: String,
    val nameAr: String,
    val nameEn: String,
    val categoryAr: String,
    val categoryEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val specsAr: List<String>,
    val specsEn: List<String>,
    val estimatedPriceUsd: Double,
    val inStockQuantity: Int,
    val leadTimeDays: Int,
    val certStandards: String,
    val isApprovedForClients: Boolean = true
)

data class IndustrialOrder(
    val orderId: String,
    val productCode: String,
    val productNameAr: String,
    val productNameEn: String,
    val clientName: String,
    val sectorType: SectorType,
    val quantity: Int,
    val priority: OrderPriority,
    val deliveryLocation: String,
    val contactEmail: String,
    val contactPhone: String,
    val notes: String,
    val status: OrderStatus,
    val orderTimestamp: Long,
    val estimatedDeliveryDate: String
)

enum class OrderPriority(val labelAr: String, val labelEn: String) {
    CRITICAL("أولوية قصوى (توريد فوري)", "Critical (Immediate)"),
    HIGH("أولوية عالية (خلال 48 ساعة)", "High (48 Hours)"),
    STANDARD("أولوية قياسية (جدول اعتيادي)", "Standard Schedule")
}

enum class OrderStatus(val labelAr: String, val labelEn: String, val colorHex: Long) {
    PENDING("قيد المراجعة الفنية", "Technical Review", 0xFFF59E0B),
    APPROVED("معتمد وقيد التجهيز بالمصنع", "Approved & Manufacturing", 0xFF00B4D8),
    DISPATCHED("تم الشحن واللوجستيات", "Dispatched & In Transit", 0xFF10B981),
    DELIVERED("تم التسليم والتركيب", "Delivered & Commissioned", 0xFF3B82F6),
    CANCELLED("ملغي", "Cancelled", 0xFFEF4444)
}
