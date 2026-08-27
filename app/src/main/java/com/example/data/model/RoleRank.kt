package com.example.data.model

enum class RoleRank(
    val level: Int,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val badgeIcon: String
) {
    SUPREME_COMMANDER(
        level = 6,
        titleAr = "الرئيس الأعلى (الصلاحية المطلقة)",
        titleEn = "Supreme Commander (Root Admin)",
        descriptionAr = "أعلى سلطة في النظام، قدرة على التعديل، الحذف الجذري، تدمير السجلات، وإنشاء الحسابات وتحديد رؤيتها.",
        descriptionEn = "Absolute root authority: full creation, system purge, unrestricted overrides, and access governance.",
        badgeIcon = "👑"
    ),
    GENERAL(
        level = 5,
        titleAr = "فريق (مدير عام المنظومات)",
        titleEn = "General (Executive Director)",
        descriptionAr = "إدارة البرامج والحلول، الموافقة على عقود التوريد الصناعي، وإدارة المستخدمين والعمليات.",
        descriptionEn = "System management, approving industrial supply contracts, user governance.",
        badgeIcon = "⭐"
    ),
    SUPERVISOR(
        level = 4,
        titleAr = "عريف / رقيب (مشرف عمليات)",
        titleEn = "Sergeant (Operations Supervisor)",
        descriptionAr = "إدارة طلبات التوريد، تحديث مسارات الأتمتة ومراقبة تكامل الربط البرمجي.",
        descriptionEn = "Manage supply orders, update automated workflows, and monitor integration.",
        badgeIcon = "🛡️"
    ),
    SPECIALIST(
        level = 3,
        titleAr = "جندي أول (أخصائي تشغيل)",
        titleEn = "Specialist (Operator)",
        descriptionAr = "تشغيل المنظومات، إنشاء طلبات المعدات الصناعية وتشغيل أدوات الاختبار.",
        descriptionEn = "Run systems, create industrial equipment orders, and execute testing pipelines.",
        badgeIcon = "⚡"
    ),
    SOLDIER(
        level = 2,
        titleAr = "جندي (مستخدم معتمد)",
        titleEn = "Soldier (Standard Member)",
        descriptionAr = "مستخدم أساسي، تصفح الخدمات وتقديم طلبات الاستشارة والبرامج.",
        descriptionEn = "Standard member, browse enterprise programs and request consultations.",
        badgeIcon = "🔹"
    ),
    OBSERVER(
        level = 1,
        titleAr = "مشاهد (قراءة فقط)",
        titleEn = "Observer (Read-Only)",
        descriptionAr = "حساب يدخل يقرأ فقط دون أي صلاحية تعديل أو إنشاء أو تنفيذ أوامر.",
        descriptionEn = "Read-only account that can view public information without execution or editing rights.",
        badgeIcon = "👁️"
    )
}
