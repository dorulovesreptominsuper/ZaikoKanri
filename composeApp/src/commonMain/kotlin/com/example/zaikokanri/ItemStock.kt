package com.example.zaikokanri

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemStock(
    // DEFAULT gen_random_uuid() のため、新規作成（Insert）時は null で OK
    val id: String? = null,

    @SerialName("item_id")
    val itemId: String,

    val category: Int = 0,

    val quantity: Int = 0,

    @SerialName("organization_id")
    val organizationId: String? = null,

    @SerialName("updated_by")
    val updatedBy: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null // ISO 8601 形式の文字列
)