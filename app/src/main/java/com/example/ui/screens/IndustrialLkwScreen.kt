package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun IndustrialLkwScreen(
    products: List<IndustrialProduct>,
    orders: List<IndustrialOrder>,
    language: AppLanguage,
    activeUser: UserAccount,
    isMasterUnlocked: Boolean,
    onOpenOrderDialog: (IndustrialProduct) -> Unit,
    onUpdateOrderStatus: (orderId: String, newStatus: OrderStatus) -> Unit,
    isSyncing: Boolean = false,
    onSyncRetrofit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val isClientRole = activeUser.roleRank == RoleRank.OBSERVER || activeUser.roleRank == RoleRank.SOLDIER
    val visibleProducts = products.filter { product ->
        if (isClientRole && !isMasterUnlocked) product.isApprovedForClients else true
    }
    var currentSubTab by remember { mutableStateOf(0) } // 0: Catalog, 1: Orders Track

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Banner Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Navy800,
                border = androidx.compose.foundation.BorderStroke(1.dp, Cyan400.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_industrial_lkw_hero_1787828609760),
                            contentDescription = "LK-W Industrial Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        listOf(Color.Transparent, Navy900.copy(alpha = 0.9f))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Gold500
                            ) {
                                Text(
                                    text = "LK-W INDUSTRIAL SERIES",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = if (isAr) "منتجات LK-W الصناعية وتسهيل طلبها" else "LK-W Industrial Supply & Equipment",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isAr)
                                "خط الإنتاج والتوريد المباشر للمعدات الثقيلة، التوربينات الهيدروليكية، الأذرع الروبوتية، وأنظمة SCADA الصناعية المتوافقة مع معايير CE و ISO العالمية."
                            else
                                "Direct industrial supply pipeline for heavy machinery, automated robotics, turbines, and certified SCADA hardware.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate300,
                                lineHeight = 18.sp
                            )
                        )

                        // Retrofit Sync Bar
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Navy900,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Cyan500.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(
                                        imageVector = Icons.Default.CloudSync,
                                        contentDescription = "Sync",
                                        tint = Cyan400,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isAr) "واجهة Retrofit السحابية الموثقة" else "Authenticated Retrofit API Client",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Cyan400)
                                        )
                                        Text(
                                            text = if (isAr) "مفتاح الأمان: mLj1Ri... | Bearer Token" else "Key: mLj1Ri... | Bearer Token",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Slate400)
                                        )
                                    }
                                }

                                FilledTonalButton(
                                    onClick = onSyncRetrofit,
                                    enabled = !isSyncing,
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Cyan500.copy(alpha = 0.2f),
                                        contentColor = Cyan400
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("sync_retrofit_products_button")
                                ) {
                                    if (isSyncing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = Cyan400,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isAr) "مزامنة الكتالوج" else "Sync Catalog",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sub-Tabs (Catalog vs Orders Pipeline)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TabButton(
                    title = if (isAr) "كتالوج المعدات LK-W (${products.size})" else "LK-W Catalog (${products.size})",
                    icon = Icons.Default.PrecisionManufacturing,
                    isSelected = currentSubTab == 0,
                    onClick = { currentSubTab = 0 },
                    modifier = Modifier.weight(1f)
                )

                TabButton(
                    title = if (isAr) "متابعة أوامر التوريد (${orders.size})" else "Track Orders (${orders.size})",
                    icon = Icons.Default.LocalShipping,
                    isSelected = currentSubTab == 1,
                    onClick = { currentSubTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Content
        if (currentSubTab == 0) {
            items(visibleProducts, key = { it.id }) { product ->
                IndustrialProductCard(
                    product = product,
                    language = language,
                    onRequestOrder = { onOpenOrderDialog(product) }
                )
            }
        } else {
            if (orders.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Navy800,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inbox,
                                contentDescription = "Empty",
                                tint = Slate300,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isAr) "لا توجد طلبات توريد حالياً" else "No industrial orders currently",
                                color = Slate200,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(orders, key = { it.orderId }) { order ->
                    IndustrialOrderCard(
                        order = order,
                        language = language,
                        canManageStatus = activeUser.canExecute || isMasterUnlocked,
                        onUpdateStatus = { newStatus -> onUpdateOrderStatus(order.orderId, newStatus) }
                    )
                }
            }
        }
    }
}

@Composable
fun TabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Cyan500 else Navy800,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Cyan400 else Slate700),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Navy900 else Cyan400,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Navy900 else Slate200,
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun IndustrialProductCard(
    product: IndustrialProduct,
    language: AppLanguage,
    onRequestOrder: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Navy800,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Category & Availability
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Navy900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Gold500.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = product.modelCode,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Gold400,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GreenSuccess.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isAr) "متوفر: ${product.inStockQuantity} وحدات" else "In Stock: ${product.inStockQuantity}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GreenSuccess,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isAr) product.nameAr else product.nameEn,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = if (isAr) product.descriptionAr else product.descriptionEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Slate300,
                    lineHeight = 18.sp
                ),
                maxLines = if (expanded) 10 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            // Specs grid
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Navy900,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    val specs = if (isAr) product.specsAr else product.specsEn
                    specs.take(if (expanded) specs.size else 2).forEach { spec ->
                        Text(
                            text = "• $spec",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate200,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action & Price row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isAr) "السعر التقديري للتوريد:" else "Estimated Unit Quote:",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate300, fontSize = 10.sp)
                    )
                    Text(
                        text = "$%,.0f USD".format(product.estimatedPriceUsd),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Gold400
                        )
                    )
                }

                Button(
                    onClick = onRequestOrder,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Cyan500,
                        contentColor = Navy900
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("request_order_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Order",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "طلب توريد مباشر" else "Request Quote",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun IndustrialOrderCard(
    order: IndustrialOrder,
    language: AppLanguage,
    canManageStatus: Boolean,
    onUpdateStatus: (OrderStatus) -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    var showStatusMenu by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Navy800,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderId,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold400
                    )
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(order.status.colorHex).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(order.status.colorHex).copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.clickable(enabled = canManageStatus) {
                        showStatusMenu = true
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) order.status.labelAr else order.status.labelEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(order.status.colorHex),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                        if (canManageStatus) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Change Status",
                                tint = Color(order.status.colorHex),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false },
                        modifier = Modifier.background(Navy800)
                    ) {
                        OrderStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isAr) status.labelAr else status.labelEn,
                                        color = Color(status.colorHex)
                                    )
                                },
                                onClick = {
                                    onUpdateStatus(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isAr) order.productNameAr else order.productNameEn,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = (if (isAr) "الجهة الطالبة: " else "Client: ") + order.clientName + " | " + (if (isAr) "الكمية: " else "Qty: ") + "${order.quantity}",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate200),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = (if (isAr) "موقع التسليم: " else "Location: ") + order.deliveryLocation,
                style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
            )

            if (order.notes.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Navy900,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "📝 " + order.notes,
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
