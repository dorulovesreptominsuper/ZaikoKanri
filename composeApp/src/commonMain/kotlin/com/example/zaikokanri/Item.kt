package com.example.zaikokanri

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Item(
    // DEFAULT gen_random_uuid() なので、新規作成時は null を許容
    val id: String? = null,

    @SerialName("organization_id")
    val organizationId: String? = null,

    val name: String,

    val category: Int = 0,

    val sku: String? = null,

    val description: String? = null,

    @SerialName("min_threshold")
    val minThreshold: Int = 0,

    // jsonb 型は JsonElement で受けるのが柔軟です
    val metadata: JsonElement? = null,

    @SerialName("created_by")
    val createdBy: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null, // ISO 8601 形式の文字列

    @SerialName("updated_at")
    val updatedAt: String? = null
)