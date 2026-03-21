package com.example.zaikokanri

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InventoryListScreen(stocks: List<ItemStockWithCategory>) {
    // 1. カテゴリIDを読みやすい文字列に変換するマップ
    val categoryNames = mapOf(
        0 to "☆食材",
        1 to "☆備品在庫",
        2 to "☆その他"
    )

    // 2. データをカテゴリごとにグループ化
    val grouped = remember(stocks) {
        stocks.sortedBy { it.items.category }.groupBy { it.items.category }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        // 画像のタイトル部分
        item {
            Text(
                text = "2025文化祭 食材・備品在庫",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        grouped.forEach { (category, itemsInGroup) ->
            // カテゴリヘッダー（スクロール時に上部に張り付く）
            stickyHeader {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = categoryNames[category] ?: "不明",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // 各在庫アイテムの行
            items(itemsInGroup) { stock ->
                StockRow(stock)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun StockRow(stock: ItemStockWithCategory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "・${stock.items.name}",
            style = MaterialTheme.typography.bodySmall
        )

        // 数量の表示
        Text(
            text = "${stock.quantity}", // 単位はmetadataから取得する実装もアリ
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}



@Preview(showBackground = true, name = "在庫リスト全体")
@Composable
fun PreviewInventoryList() {
    MaterialTheme {
        // 先ほど作成した InventoryListScreen にダミーデータを渡す
        InventoryListScreen(stocks = SampleData.items)
    }
}

@Preview(showBackground = true, name = "在庫行単体")
@Composable
fun PreviewStockRow() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            StockRow(stock = SampleData.items[0])
            Divider()
            StockRow(stock = SampleData.items[2])
        }
    }
}
object SampleData {
    val items = listOf(
        ItemStockWithCategory(
            id = "1",
            quantity = 14,
            items = ItemCategoryOnly(name = "米", category = 0)
        ),
        ItemStockWithCategory(
            id = "2",
            quantity = 30,
            items = ItemCategoryOnly(name = "容器", category = 0)
        ),
        ItemStockWithCategory(
            id = "3",
            quantity = 9,
            items = ItemCategoryOnly(name = "タオル（布きん）", category = 1)
        ),
        ItemStockWithCategory(
            id = "4",
            quantity = 100,
            items = ItemCategoryOnly(name = "トレー（エールピアより）", category = 2)
        )
    )
}