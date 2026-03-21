package com.example.zaikokanri

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import zaikokanri.composeapp.generated.resources.Res
import zaikokanri.composeapp.generated.resources.noto_sans_jp_regular

@Composable
@Preview
fun App() {
    val fontFamily = FontFamily(Font(Res.font.noto_sans_jp_regular))
    val scope = rememberCoroutineScope()
    val supabase = createSupabaseClient(
        supabaseUrl = "https://lkkgahmqzbcfirgdsvgb.supabase.co",
        supabaseKey = "sb_publishable_uVo1cq6UE4emiGDFckxA-A_Z9AEn7Km"
    ) {
        install(Auth)
        install(Postgrest)
    }

    var stockList by remember { mutableStateOf<List<ItemStockWithCategory>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAdmin by remember { mutableStateOf(false) }



    LaunchedEffect(Unit) {
        supabase.auth.signOut()
        signIn(
            supabase = supabase,
            emailInput = "flatsyokudou@test.com",
            passwordInput = "zaikokakunin",
            onSuccess = {
                scope.launch { stockList = fetchStocksWithCategory(supabase) }
            }
        )
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(fontFamily = fontFamily)
    ) {
        AppTheme {
            when {
                errorMessage != null -> Text("エラー: $errorMessage")
                stockList == null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                else -> InventoryListScreen(
                    stocks = stockList!!,
                    isAdmin = isAdmin,
                    onPasswordConfirmed = {
                        supabase.auth.signOut()
                        signIn(
                            supabase, "flatsyokudou@dev.com", it,
                            onSuccess = {
                                isAdmin = true
                                scope.launch { stockList = fetchStocksWithCategory(supabase) }
                            },
                            onError = {
                                scope.launch {
                                    signIn(
                                        supabase = supabase,
                                        emailInput = "flatsyokudou@test.com",
                                        passwordInput = "zaikokakunin",
                                        onSuccess = {
                                            scope.launch {
                                                stockList = fetchStocksWithCategory(supabase)
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    },
                    onSaveAsAdmin = {

                    }
                )
            }
        }
    }
}

suspend fun signIn(
    supabase: SupabaseClient, emailInput: String, passwordInput: String,
    onSuccess: () -> Unit = {},
    onError: () -> Unit = {},
) {
    try {
        // authモジュールを使用してサインイン
        supabase.auth.signInWith(Email) {
            email = emailInput
            password = passwordInput
        }
        println("ログイン成功")
        onSuccess()

    } catch (e: Exception) {
        // 認証エラー（パスワード間違い、ユーザー未登録など）のハンドリング
        println("ログイン失敗: ${e.message}")
        onError()
    }
}

suspend fun updateStock(
    supabase: SupabaseClient,
    stockId: String,
    newQuantity: Int,
    updatedStockList: List<ItemStockWithCategory>,
) {
    val currentUserId = supabase.auth.currentUserOrNull()?.id ?: return

    updatedStockList.forEach {
        supabase.from("item_stocks").upsert(
            values = updatedStockList
        )
    }
    supabase.from("item_stocks").update(
        {
            set("quantity", newQuantity)
            set("updated_by", currentUserId)
        }
    ) {
        filter {
            eq("id", stockId)
        }
    }
}


suspend fun fetchStocksWithCategory(
    supabase: SupabaseClient,
): List<ItemStockWithCategory> {
    val select = supabase.from("item_stocks")
        .select(
            columns = Columns.raw("id, quantity, items(name, category)")
        )
    println(select.data)
    return select.decodeList<ItemStockWithCategory>()
}
