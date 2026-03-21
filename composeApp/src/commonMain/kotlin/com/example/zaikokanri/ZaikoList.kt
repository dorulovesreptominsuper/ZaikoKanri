package com.example.zaikokanri

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import zaikokanri.composeapp.generated.resources.Res
import zaikokanri.composeapp.generated.resources.compose_multiplatform
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import kotlinx.coroutines.launch
import kotlinx.datetime.Month

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InventoryListScreen(
    stocks: List<ItemStockWithCategory>,
    isAdmin: Boolean = false,
    onPasswordConfirmed: suspend (String) -> Unit = {},
    onSaveAsAdmin: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var showContent by remember { mutableStateOf(false) }
    val copyStocks = stocks.toMutableList()
    val categoryNames = mapOf(
        0 to "☆食材",
        1 to "☆備品在庫",
        2 to "☆その他"
    )

    val grouped = remember(stocks, copyStocks) {
        copyStocks.sortedBy { it.items.category }.groupBy { it.items.category }
    }

    if (showContent) {
        PasswordInputDialog(
            onDismiss = { showContent = false },
            onConfirm = { password ->
                showContent = false
                scope.launch {
                    onPasswordConfirmed(password)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp)
    ) {
        item {
            Text(
                text = "ふらっと食堂　食材・備品在庫",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        grouped.forEach { (category, itemsInGroup) ->
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                ) {
                    Text(
                        text = categoryNames[category] ?: "不明",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
            }

            items(itemsInGroup) { stock ->
                StockRow(stock = stock, isAdmin = isAdmin)
                HorizontalDivider()
            }

        }
        item {
            Column(
                modifier = Modifier
                    .safeContentPadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!isAdmin) {
                    Button(onClick = { showContent = !showContent }) {
                        Text("管理者としてログインする")
                    }
                } else {
                    Button(onClick = { onSaveAsAdmin() }) {
                        Text("変更を保存する")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StockRow(
    stock: ItemStockWithCategory,
    isAdmin: Boolean = false,
    onUpdated: (ItemStockWithCategory) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "・${stock.items.name}",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.weight(1f))
        if (isAdmin) {

            FilledIconButton(
                modifier = Modifier.size(16.dp),
                onClick = {
                },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFFB0E0E6)
                )
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "")
            }
        }
        Text(
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center,
            text = "${stock.quantity}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
        if (isAdmin) {

            FilledIconButton(
                modifier = Modifier.size(16.dp),
                onClick = { },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF90EE90)
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        }
    }
}

@Composable
fun PasswordInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "認証が必要です") },
        text = {
            Column {
                Text("パスワードを入力してください。")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    // パスワードを伏せ字にする設定
//                    visualTransformation = PasswordVisualTransformation(),
                    // キーボードをパスワード用（英数字）にする設定
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(password) },
                enabled = password.isNotEmpty()
            ) {
                Text("ログイン", color = Color(0xFF00ACC1)) // 水色系のアクセント
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}


@Preview(showBackground = true, name = "在庫リスト全体")
@Composable
fun PreviewInventoryListAdmin() {
    MaterialTheme {
        // 先ほど作成した InventoryListScreen にダミーデータを渡す
        InventoryListScreen(isAdmin = true, stocks = SampleData.items)
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