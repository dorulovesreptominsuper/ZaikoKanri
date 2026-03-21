package com.example.zaikokanri

import kotlinx.serialization.Serializable

@Serializable
data class ItemStockWithCategory(
    val id: String,
    val quantity: Int,
    // リレーション先のアイテム情報を保持する
    val items: ItemCategoryOnly
)

@Serializable
data class ItemCategoryOnly(
    val category: Int,
    val name: String
)