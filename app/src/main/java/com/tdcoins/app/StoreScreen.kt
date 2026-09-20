package com.tdcoins.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StoreScreen(
    coins: Int,
    purchasedIds: List<String>,
    onPurchase: (StoreItem) -> Unit,
) {
    val catalog = remember { storeItems() }
    var selectedTag by remember { mutableStateOf(StoreTag.ALL) }
    var pendingPurchase by remember { mutableStateOf<StoreItem?>(null) }
    val filtered = if (selectedTag == StoreTag.ALL) catalog else catalog.filter { it.tag == selectedTag }

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Tienda TD-Coins", fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text("Canjea tus monedas por mercancía real", color = MutedText, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(PrimaryPurple, Color(0xFFA855F7))))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text("🪙", fontSize = 37.sp)
                Column {
                    Text("TU SALDO", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(coins.toString(), color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                    Text("TD-Coins disponibles", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                }
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                items(StoreTag.entries) { tag ->
                    val active = selectedTag == tag
                    TextButton(
                        onClick = { selectedTag = tag },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = if (active) PrimaryPurple else Color.Transparent,
                            contentColor = if (active) Color.White else MutedText,
                        ),
                        modifier = Modifier
                            .then(
                                if (!active) Modifier.background(Color.Transparent, RoundedCornerShape(50))
                                else Modifier,
                            ),
                    ) {
                        Text(tag.label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        items(filtered.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowItems.forEach { item ->
                    ProductCard(
                        item = item,
                        owned = item.id in purchasedIds,
                        canAfford = coins >= item.price,
                        onSelect = { pendingPurchase = item },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
        item { Spacer(modifier = Modifier.height(4.dp)) }
    }

    pendingPurchase?.let { item ->
        PurchaseDialog(
            item = item,
            balance = coins,
            onDismiss = { pendingPurchase = null },
            onConfirm = {
                onPurchase(item)
                pendingPurchase = null
            },
        )
    }
}

@Composable
private fun ProductCard(
    item: StoreItem,
    owned: Boolean,
    canAfford: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenCard(modifier = modifier) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(Background, MutedLavender))),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(item.imageRes),
                    contentDescription = item.name,
                    modifier = Modifier.size(110.dp),
                    contentScale = ContentScale.Fit,
                )
            }
            Text(item.tag.label, color = MutedText, fontSize = 10.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp))
            Text(item.name, fontSize = 13.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold, minLines = 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 9.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CoinBadge(item.price, small = true)
                when {
                    owned -> Text("✓ Tuyo", color = Color(0xFF16A34A), fontSize = 10.sp, fontWeight = FontWeight.Black)
                    else -> Button(
                        onClick = onSelect,
                        enabled = canAfford,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 9.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPurple,
                            disabledContainerColor = BorderLavender,
                        ),
                        modifier = Modifier.height(32.dp),
                    ) {
                        Text("Canjear", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PurchaseDialog(
    item: StoreItem,
    balance: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Image(
                painter = painterResource(item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit,
            )
        },
        title = { Text(item.name, fontWeight = FontWeight.Black) },
        text = {
            Column {
                Text(item.description, color = MutedText, fontSize = 13.sp, lineHeight = 18.sp)
                BalanceRow("Costo", item.price, modifier = Modifier.padding(top = 14.dp))
                BalanceRow("Saldo después", balance - item.price, modifier = Modifier.padding(top = 8.dp))
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text("¡Canjear! 🎉") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}

@Composable
private fun BalanceRow(label: String, amount: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MutedLavender)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        CoinBadge(amount, small = true)
    }
}