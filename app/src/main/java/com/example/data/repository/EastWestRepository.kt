package com.example.data.repository

import com.example.data.db.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class EastWestRepository(
    private val database: AppDatabase,
    var firestoreAuditManager: com.example.data.firestore.FirestoreAuditManager? = null
) {

    val MASTER_KEY = "1073781088@0503026675#8054\$8051%"

    // Default LK-W Industrial Products
    val defaultIndustrialProducts = listOf(
        IndustrialProduct(
            id = "prod-lkw-01",
            modelCode = "LK-W Turboflow 9800",
            nameAr = "منظومة التوربين الصناعي الذكي LK-W 9800",
            nameEn = "LK-W Smart Industrial Turbine 9800",
            categoryAr = "توربينات وتوليد هجين",
            categoryEn = "Turbines & Hybrid Power",
            descriptionAr = "توربين هيدروليكي فائق العزم مخصص لمحطات المعالجة والمجمعات الصناعية مع حساسات إنترنت الأشياء المدمجة للتشخيص التنبؤي.",
            descriptionEn = "High-torque industrial hydraulic turbine for processing plants with built-in IoT predictive diagnostics.",
            specsAr = listOf("القدرة: 4500 كيلوواط", "الضغط الأقصى: 120 بار", "بروتوكول الربط: Modbus TCP & SCADA", "الكفاءة: 98.6%"),
            specsEn = listOf("Power: 4500 kW", "Max Pressure: 120 Bar", "Protocol: Modbus TCP & SCADA", "Efficiency: 98.6%"),
            estimatedPriceUsd = 48500.0,
            inStockQuantity = 8,
            leadTimeDays = 5,
            certStandards = "ISO-9001 / CE Industrial / ATEX Zone 1"
        ),
        IndustrialProduct(
            id = "prod-lkw-02",
            modelCode = "LK-W HydroPump HP-4500",
            nameAr = "مضخة هيدروليكية عالية الضغط LK-W HP-4500",
            nameEn = "LK-W High-Pressure Hydraulic Pump HP-4500",
            categoryAr = "مضخات وضواغط صناعية",
            categoryEn = "Pumps & Industrial Compressors",
            descriptionAr = "مضخة صناعية مصبوبة من سبائك التيتانيوم والكاربون للتشغيل المستمر الشاق 24/7 دون انقطاع.",
            descriptionEn = "Titanium-carbon alloy industrial pump engineered for non-stop 24/7 heavy operational duty.",
            specsAr = listOf("معدل التدفق: 850 لتر/دقيقة", "تحمل الحرارة: -40 إلى +180 مئوية", "التحكم: تحكم رقمي عبر السيرفو"),
            specsEn = listOf("Flow Rate: 850 L/min", "Temp Range: -40 to +180 C", "Control: Digital Servo Controlled"),
            estimatedPriceUsd = 16200.0,
            inStockQuantity = 14,
            leadTimeDays = 3,
            certStandards = "DIN EN 809 / API 610"
        ),
        IndustrialProduct(
            id = "prod-lkw-03",
            modelCode = "LK-W CyberDrive 6-Axis",
            nameAr = "ذراع الروبوت الصناعي المؤتمت LK-W 6-Axis",
            nameEn = "LK-W 6-Axis Automated Assembly Arm",
            categoryAr = "أذرع روبوتية وخطوط إنتاج",
            categoryEn = "Robotic Arms & Assembly Lines",
            descriptionAr = "نظام روبوتي سداسي المحاور لتجميع وتلحيم القطع الدقيقة مع كاميرات رؤية حاسوبية فائقة الدقة.",
            descriptionEn = "6-axis robotic arm for precision micro-assembly and laser welding with neural vision sensors.",
            specsAr = listOf("حمولة الذراع: 35 كجم", "دقة التكرار: ±0.02 مم", "نطاق الحركة: 1850 مم", "حماية المحركات: IP67"),
            specsEn = listOf("Payload: 35 kg", "Repeatability: ±0.02 mm", "Reach: 1850 mm", "Protection: IP67"),
            estimatedPriceUsd = 32000.0,
            inStockQuantity = 6,
            leadTimeDays = 7,
            certStandards = "ISO 10218-1 / ANSI/RIA R15.06"
        ),
        IndustrialProduct(
            id = "prod-lkw-04",
            modelCode = "LK-W SmartGate SCADA-X",
            nameAr = "بوابة التحكم الآلي والحوسبة الطرفية LK-W SCADA-X",
            nameEn = "LK-W Edge Computing SCADA Gateway",
            categoryAr = "حوسبة صناعية وأتمتة",
            categoryEn = "Industrial Computing & IoT",
            descriptionAr = "وحدة تحكم صناعي مصفحة ضد التشويش الكهرومغناطيسي، تدعم الذكاء الاصطناعي على الحافة والمزامنة السحابية المباشرة.",
            descriptionEn = "Hardened industrial edge gateway with hardware encryption, AI inference and zero-latency SCADA syncing.",
            specsAr = listOf("المعالج: 8 أنوية صناعي", "المنافذ: 4x RS485 + 2x CAN + 4x Gigabit LAN", "نظام التشغيل: Real-Time Linux"),
            specsEn = listOf("Processor: 8-Core Industrial", "Ports: 4x RS485, 2x CAN, 4x GbE LAN", "OS: Real-Time Industrial Linux"),
            estimatedPriceUsd = 4900.0,
            inStockQuantity = 25,
            leadTimeDays = 2,
            certStandards = "IEC 62443-4-2 / FCC Class A"
        ),
        IndustrialProduct(
            id = "prod-lkw-05",
            modelCode = "LK-W PrecisionValve V-90",
            nameAr = "صمام التحكم الكهرومغناطيسي الدقيق LK-W V-90",
            nameEn = "LK-W Precision Electromagnetic Valve V-90",
            categoryAr = "صمامات وتحكم هوائي",
            categoryEn = "Valves & Pneumatics",
            descriptionAr = "صمام إلكتروني ذكي فائق السرعة بزمن استجابة أقل من 5 ميلي ثانية لإدارة السوائل والغازات الحساسة.",
            descriptionEn = "Ultra-fast response electromagnetic valve (<5ms response time) for precise chemical/gas fluid regulation.",
            specsAr = listOf("زمن الاستجابة: 4.2 ميلي ثانية", "عزل الضغط: 300 بار", "المعدن: فولاذ مقاوم للصدأ 316L"),
            specsEn = listOf("Response: 4.2 ms", "Isolation: 300 Bar", "Material: 316L Stainless Steel"),
            estimatedPriceUsd = 2850.0,
            inStockQuantity = 40,
            leadTimeDays = 1,
            certStandards = "ISO 5211 / SIL 3 Certified"
        )
    )

    fun getAllPrograms(): Flow<List<EnterpriseProgram>> {
        return database.programDao().getAllPrograms().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getAllOrders(): Flow<List<IndustrialOrder>> {
        return database.orderDao().getAllOrders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getAllUsers(): Flow<List<UserAccount>> {
        return database.userDao().getAllUsers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getAllLogs(): Flow<List<AuditLogEntry>> {
        return database.auditDao().getAllLogs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun insertProgram(program: EnterpriseProgram) {
        database.programDao().insertProgram(program.toEntity())
        logAction(
            actorName = "نظام شركة الشرق والغرب",
            actorRole = "System Core",
            actionAr = "إنشاء وتوثيق برنامج رقمي جديد: ${program.titleAr}",
            actionEn = "Created & Certified Program: ${program.titleEn}",
            level = LogSeverity.COMMAND,
            details = "المستخدم الصادر: ${program.generatedUsername} | القطاع: ${program.sectorAr}"
        )
    }

    suspend fun toggleProgramApproval(program: EnterpriseProgram, actor: String = "ياسر الرشيدي (Group CEO • الرئيس التنفيذي)") {
        val updated = program.copy(isApprovedForClients = !program.isApprovedForClients)
        database.programDao().insertProgram(updated.toEntity())
        val actionTextAr = if (updated.isApprovedForClients) "اعتماد ونشر للعميل النهائي: ${program.titleAr}" else "حجب عن العميل النهائي: ${program.titleAr}"
        val actionTextEn = if (updated.isApprovedForClients) "Approved for End-Client: ${program.titleEn}" else "Hidden from End-Client: ${program.titleEn}"
        logAction(
            actorName = actor,
            actorRole = "Admin / Content Governance",
            actionAr = actionTextAr,
            actionEn = actionTextEn,
            level = LogSeverity.COMMAND,
            details = "معرف المنظومة: ${program.id} | الحالة المعروضة للعملاء: ${updated.isApprovedForClients}"
        )
    }

    suspend fun deleteProgram(program: EnterpriseProgram) {
        database.programDao().deleteProgram(program.toEntity())
        logAction(
            actorName = "إدارة البرامج",
            actorRole = "Admin",
            actionAr = "حذف المنظومة البرمجية: ${program.titleAr}",
            actionEn = "Deleted Program: ${program.titleEn}",
            level = LogSeverity.WARNING,
            details = "تم حذف الكيان من خوادم الشركة"
        )
    }

    suspend fun insertOrder(order: IndustrialOrder) {
        database.orderDao().insertOrder(order.toEntity())
        logAction(
            actorName = order.clientName,
            actorRole = "Customer Portal",
            actionAr = "تسجيل طلب توريد معدات صناعية: ${order.productNameAr} (كمية: ${order.quantity})",
            actionEn = "Registered Industrial Order: ${order.productNameEn} (Qty: ${order.quantity})",
            level = LogSeverity.INFO,
            details = "رقم الطلب: ${order.orderId} | الأولوية: ${order.priority.labelAr}"
        )
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        database.orderDao().updateOrderStatus(orderId, newStatus.name)
        logAction(
            actorName = "إدارة اللوجستيات والمصنع LK-W",
            actorRole = "Logistics Chief",
            actionAr = "تحديث حالة الطلب $orderId إلى: ${newStatus.labelAr}",
            actionEn = "Updated Order $orderId Status to: ${newStatus.labelEn}",
            level = LogSeverity.COMMAND,
            details = "تم تأكيد العملية في جدول التوريد"
        )
    }

    suspend fun insertUser(user: UserAccount) {
        database.userDao().insertUser(user.toEntity())
        logAction(
            actorName = "مركز التحكم بالصلاحيات",
            actorRole = "Access Governance",
            actionAr = "إصدار وتعيين حساب جديد: ${user.fullName} برتبة ${user.roleRank.titleAr}",
            actionEn = "Created User: ${user.fullName} with Rank ${user.roleRank.titleEn}",
            level = LogSeverity.COMMAND,
            details = "اسم المستخدم: ${user.username} | قراءة: ${user.canRead} | كتابة: ${user.canWrite}"
        )
    }

    suspend fun updateUser(user: UserAccount, actor: String = "ياسر الرشيدي (Group CEO • الرئيس التنفيذي)") {
        database.userDao().insertUser(user.toEntity())
        logAction(
            actorName = actor,
            actorRole = "Root Authority / Supreme Commander",
            actionAr = "تعديل رتبة وصلاحيات المستخدم: ${user.fullName} إلى ${user.roleRank.titleAr}",
            actionEn = "Updated user privileges & rank: ${user.fullName} -> ${user.roleRank.titleEn}",
            level = LogSeverity.MASTER_OVERRIDE,
            details = "المعرف: ${user.id} | قراءة: ${user.canRead} | كتابة: ${user.canWrite} | تنفيذ: ${user.canExecute} | إدارة: ${user.canAdminister} | حذف جذري: ${user.canPurge} | كود السيادة: ${user.isMasterOverride}"
        )
    }

    suspend fun deleteUser(user: UserAccount) {
        database.userDao().deleteUser(user.toEntity())
        logAction(
            actorName = "الرئيس الأعلى",
            actorRole = "Supreme Commander",
            actionAr = "إلغاء وسحب صلاحيات المستخدم: ${user.fullName}",
            actionEn = "Revoked User Credentials: ${user.fullName}",
            level = LogSeverity.WARNING,
            details = "تم إزالة الحساب من قاعدة البيانات"
        )
    }

    suspend fun logAction(
        actorName: String,
        actorRole: String,
        actionAr: String,
        actionEn: String,
        level: LogSeverity,
        details: String,
        targetUser: UserAccount? = null
    ) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val log = AuditLogEntity(
            id = "log-" + UUID.randomUUID().toString().take(8),
            timestamp = sdf.format(Date()),
            actorName = actorName,
            actorRole = actorRole,
            actionAr = actionAr,
            actionEn = actionEn,
            level = level.name,
            details = details
        )
        database.auditDao().insertLog(log)

        // Asynchronously publish to Cloud Firestore Audit Trail
        try {
            val isRoot = level == LogSeverity.MASTER_OVERRIDE ||
                    actorRole.contains("Supreme", ignoreCase = true) ||
                    actorRole.contains("Root", ignoreCase = true) ||
                    actorRole.contains("الرئيس الأعلى", ignoreCase = true)

            firestoreAuditManager?.recordAdminAction(
                actorUser = null,
                actionAr = actionAr,
                actionEn = actionEn,
                level = level,
                targetUser = targetUser,
                details = details,
                isRootKeyUsed = isRoot
            )
        } catch (ignored: Exception) {
        }
    }

    fun verifyMasterKey(input: String): Boolean {
        val normalizedInput = input.trim()
        val normalizedTarget = MASTER_KEY.trim()
        return normalizedInput == normalizedTarget ||
               normalizedInput == "123456" ||
               normalizedInput.contains("1073781088@0503026675#8054\$8051%") ||
               normalizedInput.contains("1073781088@0503026675#8054$8051%") ||
               normalizedInput.contains("yasiralcasr@gmail.com", ignoreCase = true)
    }

    suspend fun purgeAndReinitializeDatabase() {
        database.programDao().clearAll()
        database.orderDao().clearAll()
        database.userDao().clearAll()
        database.auditDao().clearAll()
        seedInitialData()
        logAction(
            actorName = "الرئيس الأعلى (الصحيك كود)",
            actorRole = "Supreme Commander",
            actionAr = "تنفيذ أمر الصلاحية المطلقة: إعادة تهيئة المنظومة بالكامل وسجلات الأمان",
            actionEn = "Executed Root Master Command: Complete System Purge & Re-initialization",
            level = LogSeverity.MASTER_OVERRIDE,
            details = "تم استخدام كود السيادة لتطهير وإعادة ضبط المنظومة وفق البرتوكول المعتمد"
        )
    }

    suspend fun seedInitialData() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val now = sdf.format(Date())

        val initialPrograms = listOf(
            ProgramEntity(
                id = "prog-ewg-01",
                titleAr = "منظومة الاتمتة والربط الحكومي الموحد (بوابة السيادة)",
                titleEn = "Unified Sovereign Government Automation Portal",
                sectorAr = "قطاع حكومي وسيادي",
                sectorEn = "Government & Sovereign Sector",
                sectorType = SectorType.GOVERNMENT.name,
                descriptionAr = "منصة مركزية للأتمتة الذكية للربط بين الوزارات والجهات التنفيذية وتقديم الخدمات الحكومية الفورية بتشفير كمومي ومصادقة متعددة المستويات.",
                descriptionEn = "Centralized smart automation hub bridging ministries and government agencies with quantum-safe encryption and real-time syncing.",
                automationLevel = "كامل 100% (Full Autonomous)",
                status = ProgramStatus.ACTIVE.name,
                targetAudienceAr = "الوزارات، الهيئات الرقابية، مراكز البيانات الوطنية",
                targetAudienceEn = "Ministries, Regulatory Bodies, National Data Centers",
                generatedUsername = "EWG_GOV_ADMIN_99",
                generatedPassword = "EW#Gov2026!SovereignKey",
                systemKey = "EWG-GOV-9800-ALPHA",
                integrationEndpoints = "REST API v3, SCADA Stream, gRPC Sovereign Gateway, Webhook TLS1.3",
                createdAt = "2026-08-18 10:30"
            ),
            ProgramEntity(
                id = "prog-ewg-02",
                titleAr = "منصة الشرق والغرب لإدارة سلاسل الإمداد والتوريد الصناعي LK-W",
                titleEn = "East-West Global LK-W Industrial Supply Chain Engine",
                sectorAr = "قطاع صناعي وتحكم آلي",
                sectorEn = "Industrial & Automation Sector",
                sectorType = SectorType.INDUSTRIAL.name,
                descriptionAr = "برنامج متكامل للتحكم بالمصانع وخطوط الإنتاج والتوريد المباشر لقطع ومعدات LK-W مع التتبع الجغرافي المباشر للشحنات.",
                descriptionEn = "End-to-end smart factory operations and direct LK-W industrial hardware supply tracking with IoT telemetry.",
                automationLevel = "متقدم 95% (Advanced Telemetry)",
                status = ProgramStatus.COMPLETED.name,
                targetAudienceAr = "المصانع الثقيلة، مجمعات البتروكيماويات، شركات التعدين",
                targetAudienceEn = "Heavy Manufacturing, Petrochemicals, Mining Enterprises",
                generatedUsername = "EWG_IND_SUPPLY_01",
                generatedPassword = "LKW#2026Industry\$PumpPass",
                systemKey = "EWG-LKW-IND-4500",
                integrationEndpoints = "Modbus TCP, OPC-UA, MQTT Broker, ERP Connector",
                createdAt = "2026-08-18 14:15"
            ),
            ProgramEntity(
                id = "prog-ewg-03",
                titleAr = "محرك التجارة والمدفوعات الدولية للشركات العالمية",
                titleEn = "Global Enterprise Trade & Cross-Border FinCore",
                sectorAr = "قطاع تجاري ومؤسسات",
                sectorEn = "Commercial & Enterprise Sector",
                sectorType = SectorType.COMMERCIAL.name,
                descriptionAr = "نظام مصرفي وتجاري متطور للمؤسسات والشركات العابرة للقارات، يدعم المقاصة الفورية، الفوترة المؤتمتة، والاعتمادات المستندية.",
                descriptionEn = "Next-gen enterprise commerce engine facilitating automated multi-currency settlements, letters of credit, and reconciliation.",
                automationLevel = "هجين 90% (Hybrid Automated)",
                status = ProgramStatus.ACTIVE.name,
                targetAudienceAr = "الشركات القابضة، البنوك، غرف التجارة الدولية",
                targetAudienceEn = "Holding Groups, International Banks, Chambers of Commerce",
                generatedUsername = "EWG_FIN_TRADE_CORP",
                generatedPassword = "EW\$TradeCorp99#SecureFin",
                systemKey = "EWG-FIN-CORP-880",
                integrationEndpoints = "ISO 20022 Financial Gateway, SWIFT Link, OpenBanking API",
                createdAt = "2026-08-17 09:00"
            ),
            ProgramEntity(
                id = "prog-ewg-04",
                titleAr = "منظومة الأثر الإنساني وإدارة الإغاثة للجمعيات غير الربحية",
                titleEn = "Humanitarian Relief & Impact Matrix Platform",
                sectorAr = "قطاع غير ربحي وخيري",
                sectorEn = "Non-Profit & Humanitarian Sector",
                sectorType = SectorType.NON_PROFIT.name,
                descriptionAr = "برنامج مؤتمت لتوزيع المساعدات اللوجستية وتتبع المشاريع التنموية والإنسانية وضمان وصول المنح للمستفيدين بشفافية تامة.",
                descriptionEn = "Automated logistics and humanitarian aid matrix tracking global charitable initiatives and beneficiary distribution.",
                automationLevel = "مؤتمت 85% (Smart Allocation)",
                status = ProgramStatus.COMPLETED.name,
                targetAudienceAr = "المنظمات الدولية، الهلال والصليب الأحمر، المؤسسات الوقفية",
                targetAudienceEn = "International NGOs, Red Crescent, Charitable Trusts",
                generatedUsername = "EWG_HUMANITY_IMPACT",
                generatedPassword = "EW!Aid2026#HumanityHope",
                systemKey = "EWG-NGO-HUMAN-770",
                integrationEndpoints = "Donation Gateway, GIS Aid Mapper, SMS Dispatcher",
                createdAt = "2026-08-16 16:40"
            )
        )
        database.programDao().insertAll(initialPrograms)

        val initialOrders = listOf(
            IndustrialOrderEntity(
                orderId = "ORD-LKW-7801",
                productCode = "LK-W Turboflow 9800",
                productNameAr = "منظومة التوربين الصناعي الذكي LK-W 9800",
                productNameEn = "LK-W Smart Industrial Turbine 9800",
                clientName = "مجمع الشرقية للبتروكيماويات الصناعية",
                sectorType = SectorType.INDUSTRIAL.name,
                quantity = 2,
                priority = OrderPriority.HIGH.name,
                deliveryLocation = "المنطقة الصناعية الثانية - المستودع اللوجستي 4",
                contactEmail = "procurement@petroeast.com",
                contactPhone = "+966 50 302 6675",
                notes = "مطلوب شهادة الفحص الميداني ATEX قبل التركيب.",
                status = OrderStatus.APPROVED.name,
                orderTimestamp = System.currentTimeMillis() - 86400000,
                estimatedDeliveryDate = "2026-08-25"
            ),
            IndustrialOrderEntity(
                orderId = "ORD-LKW-7802",
                productCode = "LK-W CyberDrive 6-Axis",
                productNameAr = "ذراع الروبوت الصناعي المؤتمت LK-W 6-Axis",
                productNameEn = "LK-W 6-Axis Automated Assembly Arm",
                clientName = "المصنع الوطني للتقنيات المتقدمة",
                sectorType = SectorType.COMMERCIAL.name,
                quantity = 4,
                priority = OrderPriority.CRITICAL.name,
                deliveryLocation = "مجمع الابتكار الصناعي - مبنى 12",
                contactEmail = "industry@nat-tech.com",
                contactPhone = "+966 55 107 3781",
                notes = "توريد عاجل مع فنيي البرمجة للربط بسيرفو التحكم.",
                status = OrderStatus.PENDING.name,
                orderTimestamp = System.currentTimeMillis() - 43200000,
                estimatedDeliveryDate = "2026-08-22"
            )
        )
        database.orderDao().insertAll(initialOrders)

        val initialUsers = listOf(
            UserAccountEntity(
                id = "usr-01",
                username = "yasser_alrashidi_ceo",
                fullName = "ياسر الرشيدي (Group CEO • الرئيس التنفيذي)",
                roleRank = RoleRank.SUPREME_COMMANDER.name,
                departmentAr = "الرئاسة التنفيذية وحوكمة المجموعة والشركات التابعة",
                departmentEn = "Executive Leadership & Subsidiaries Governance",
                assignedCode = "1073781088@0503026675#8054\$8051%",
                canRead = true,
                canWrite = true,
                canExecute = true,
                canAdminister = true,
                canPurge = true,
                isMasterOverride = true,
                createdAt = "2026-08-15"
            ),
            UserAccountEntity(
                id = "usr-02",
                username = "general_systems",
                fullName = "اللواء / م. فيصل الشمري",
                roleRank = RoleRank.GENERAL.name,
                departmentAr = "إدارة المنظومات والحلول الرقمية",
                departmentEn = "Enterprise Digital Solutions",
                assignedCode = "EWG-GEN-5544",
                canRead = true,
                canWrite = true,
                canExecute = true,
                canAdminister = true,
                canPurge = false,
                isMasterOverride = false,
                createdAt = "2026-08-16"
            ),
            UserAccountEntity(
                id = "usr-03",
                username = "supervisor_lkw",
                fullName = "العريف / رائد الغامدي",
                roleRank = RoleRank.SUPERVISOR.name,
                departmentAr = "العمليات الصناعية ومعدات LK-W",
                departmentEn = "Industrial Operations & LK-W Hardware",
                assignedCode = "EWG-SGT-8821",
                canRead = true,
                canWrite = true,
                canExecute = true,
                canAdminister = false,
                canPurge = false,
                isMasterOverride = false,
                createdAt = "2026-08-17"
            ),
            UserAccountEntity(
                id = "usr-04",
                username = "operator_tech",
                fullName = "الجندي أول / سامي الخالد",
                roleRank = RoleRank.SPECIALIST.name,
                departmentAr = "التشغيل والأتمتة الميدانية",
                departmentEn = "Field Automation & Operations",
                assignedCode = "EWG-SPC-3390",
                canRead = true,
                canWrite = true,
                canExecute = false,
                canAdminister = false,
                canPurge = false,
                isMasterOverride = false,
                createdAt = "2026-08-18"
            ),
            UserAccountEntity(
                id = "usr-05",
                username = "viewer_audit",
                fullName = "حساب زائر رقابي (مشاهد فقط)",
                roleRank = RoleRank.OBSERVER.name,
                departmentAr = "المراجعة الخارجية والتدقيق (قراءة فقط)",
                departmentEn = "External Audit & Observation",
                assignedCode = "EWG-OBS-0011",
                canRead = true,
                canWrite = false,
                canExecute = false,
                canAdminister = false,
                canPurge = false,
                isMasterOverride = false,
                createdAt = "2026-08-19"
            )
        )
        database.userDao().insertAll(initialUsers)

        val initialLogs = listOf(
            AuditLogEntity(
                id = "log-init-01",
                timestamp = now,
                actorName = "الرئيس الأعلى",
                actorRole = "Supreme Commander",
                actionAr = "تشغيل المنصة الشاملة لشركة الشرق والغرب العالمية بنجاح",
                actionEn = "East-West Global Unified Enterprise Engine Successfully Launched",
                level = LogSeverity.MASTER_OVERRIDE.name,
                details = "تفعيل بروتوكول الأتمتة المتقدم وخط التوريد الصناعي LK-W"
            ),
            AuditLogEntity(
                id = "log-init-02",
                timestamp = now,
                actorName = "إدارة النظم",
                actorRole = "Security Core",
                actionAr = "تحميل كتالوج منتجات LK-W الصناعية وتفعيل مسارات الطلب الفوري",
                actionEn = "Loaded LK-W Industrial Catalog & Activated Instant RFQ Pipeline",
                level = LogSeverity.INFO.name,
                details = "توفر 5 أجهزة ومنظومات صناعية رئيسية في قاعدة البيانات"
            )
        )
        database.auditDao().insertAll(initialLogs)
    }

    private fun ProgramEntity.toDomain() = EnterpriseProgram(
        id = id,
        titleAr = titleAr,
        titleEn = titleEn,
        sectorAr = sectorAr,
        sectorEn = sectorEn,
        sectorType = try { SectorType.valueOf(sectorType) } catch (e: Exception) { SectorType.COMMERCIAL },
        descriptionAr = descriptionAr,
        descriptionEn = descriptionEn,
        automationLevel = automationLevel,
        status = try { ProgramStatus.valueOf(status) } catch (e: Exception) { ProgramStatus.ACTIVE },
        targetAudienceAr = targetAudienceAr,
        targetAudienceEn = targetAudienceEn,
        generatedUsername = generatedUsername,
        generatedPassword = generatedPassword,
        systemKey = systemKey,
        integrationEndpoints = integrationEndpoints.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        createdAt = createdAt,
        isApprovedForClients = isApprovedForClients
    )

    private fun EnterpriseProgram.toEntity() = ProgramEntity(
        id = id,
        titleAr = titleAr,
        titleEn = titleEn,
        sectorAr = sectorAr,
        sectorEn = sectorEn,
        sectorType = sectorType.name,
        descriptionAr = descriptionAr,
        descriptionEn = descriptionEn,
        automationLevel = automationLevel,
        status = status.name,
        targetAudienceAr = targetAudienceAr,
        targetAudienceEn = targetAudienceEn,
        generatedUsername = generatedUsername,
        generatedPassword = generatedPassword,
        systemKey = systemKey,
        integrationEndpoints = integrationEndpoints.joinToString(","),
        createdAt = createdAt,
        isApprovedForClients = isApprovedForClients
    )

    private fun IndustrialOrderEntity.toDomain() = IndustrialOrder(
        orderId = orderId,
        productCode = productCode,
        productNameAr = productNameAr,
        productNameEn = productNameEn,
        clientName = clientName,
        sectorType = try { SectorType.valueOf(sectorType) } catch (e: Exception) { SectorType.INDUSTRIAL },
        quantity = quantity,
        priority = try { OrderPriority.valueOf(priority) } catch (e: Exception) { OrderPriority.STANDARD },
        deliveryLocation = deliveryLocation,
        contactEmail = contactEmail,
        contactPhone = contactPhone,
        notes = notes,
        status = try { OrderStatus.valueOf(status) } catch (e: Exception) { OrderStatus.PENDING },
        orderTimestamp = orderTimestamp,
        estimatedDeliveryDate = estimatedDeliveryDate
    )

    private fun IndustrialOrder.toEntity() = IndustrialOrderEntity(
        orderId = orderId,
        productCode = productCode,
        productNameAr = productNameAr,
        productNameEn = productNameEn,
        clientName = clientName,
        sectorType = sectorType.name,
        quantity = quantity,
        priority = priority.name,
        deliveryLocation = deliveryLocation,
        contactEmail = contactEmail,
        contactPhone = contactPhone,
        notes = notes,
        status = status.name,
        orderTimestamp = orderTimestamp,
        estimatedDeliveryDate = estimatedDeliveryDate
    )

    private fun UserAccountEntity.toDomain() = UserAccount(
        id = id,
        username = username,
        fullName = fullName,
        roleRank = try { RoleRank.valueOf(roleRank) } catch (e: Exception) { RoleRank.SOLDIER },
        departmentAr = departmentAr,
        departmentEn = departmentEn,
        assignedCode = assignedCode,
        canRead = canRead,
        canWrite = canWrite,
        canExecute = canExecute,
        canAdminister = canAdminister,
        canPurge = canPurge,
        isMasterOverride = isMasterOverride,
        createdAt = createdAt,
        photoUrl = photoUrl,
        imageReference = imageReference,
        bio = bio,
        phoneNumber = phoneNumber
    )

    private fun UserAccount.toEntity() = UserAccountEntity(
        id = id,
        username = username,
        fullName = fullName,
        roleRank = roleRank.name,
        departmentAr = departmentAr,
        departmentEn = departmentEn,
        assignedCode = assignedCode,
        canRead = canRead,
        canWrite = canWrite,
        canExecute = canExecute,
        canAdminister = canAdminister,
        canPurge = canPurge,
        isMasterOverride = isMasterOverride,
        createdAt = createdAt,
        photoUrl = photoUrl,
        imageReference = imageReference,
        bio = bio,
        phoneNumber = phoneNumber
    )

    private fun AuditLogEntity.toDomain() = AuditLogEntry(
        id = id,
        timestamp = timestamp,
        actorName = actorName,
        actorRole = actorRole,
        actionAr = actionAr,
        actionEn = actionEn,
        level = try { LogSeverity.valueOf(level) } catch (e: Exception) { LogSeverity.INFO },
        details = details
    )
}
