package com.github.tsyshiu.dailytools.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.tsyshiu.dailytools.ui.MPadding
import com.github.tsyshiu.dailytools.ui.MSpace
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
@Preview
fun FinancialTools() {
    Column(modifier = Modifier.fillMaxSize().padding(MPadding.screenPadding)) {
        Text("理财计算器", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(MSpace.underH1Title))
        Asset()

        Spacer(Modifier.height(32.dp))

        InterestRate()
    }
}

@Composable
fun Asset(modifier: Modifier = Modifier) {
    // 获取当前日期信息
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val today = now.day
    val daysInMonth = now.month.number.let { month ->
        val year = now.year
        getDaysInMonth(year, month)
    }

    val daysLeft = daysInMonth - today + 1 // 包含今天在内的剩余天数

    // 状态变量
    var currentMonthlyAvg by remember { mutableStateOf("") } // 当前月均资产
    var currentAssets by remember { mutableStateOf("") }     // 当前资产
    var targetMonthlyAvg by remember { mutableStateOf("") }  // 期望月均资产

    // --- 第一个 Row: 月均资产计算 ---
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("月均资产预测", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = currentMonthlyAvg,
                    onValueChange = { currentMonthlyAvg = it },
                    label = { Text("当前月均") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = currentAssets,
                    onValueChange = { currentAssets = it },
                    label = { Text("当前总资产") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).wrapContentSize(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = targetMonthlyAvg,
                    onValueChange = { targetMonthlyAvg = it },
                    label = { Text("期望月均") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            Spacer(Modifier.height(16.dp))

            // 计算逻辑
            val avg = currentMonthlyAvg.toDoubleOrNull() ?: 0.0
            val assets = currentAssets.toDoubleOrNull() ?: 0.0
            val target = targetMonthlyAvg.toDoubleOrNull() ?: 0.0


            val predictedAvg = calcPredictedAvg(avg, today, assets, daysLeft, daysInMonth)

            val requiredTopUp = calcRequiredTopUp(daysLeft, target, daysInMonth, avg, today, assets)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "本月进度：第 $today 天 / 共 $daysInMonth 天",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "预测本月最终月均: ${"%.2f".format(predictedAvg)}",
                    color = MaterialTheme.colorScheme.primary
                )
                if (target > 0) {
                    Text(
                        "达到目标还需立即转入: ${
                            if (requiredTopUp > 0) "%.2f".format(
                                requiredTopUp
                            ) else "已达标"
                        }",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (requiredTopUp > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}


/**
 * 计算为了达到目标月均资产还需转入的金额
 * 还需转入金额 = (期望月均 * 总天数 - 当前月均 * 已过天数) / 剩余天数 - 当前资产
 *
 * The formula used is:
 * `Required Top-up = (Target Average * Total Days - Current Average * Days Passed) / Remaining Days - Current Assets`
 *
 * @param daysLeft The number of days remaining in the month, including today.
 * @param targetAssets The desired average assets for the entire month.
 * @param daysInMonth The total number of days in the current month.
 * @param currentAvg The current average assets calculated from the start of the month up to yesterday.
 * @param today The current day of the month (1-based).
 * @param currentAssets The total assets currently held.
 * @return The amount required to be topped up to reach the target. Returns 0.0 if the month has ended.
 */
private fun calcRequiredTopUp(
    daysLeft: Int,
    targetAssets: Double,
    daysInMonth: Int,
    currentAvg: Double,
    today: Int,
    currentAssets: Double
): Double = if (daysLeft > 0) {
    (targetAssets * daysInMonth - currentAvg * (today - 1)) / daysLeft - currentAssets
} else 0.0


/**
 * 预测本月月均 = (当前月均 * 已过天数 + 当前资产 * 剩余天数) / 本月总天数
 * 注意：已过天数通常指到昨天为止，这里简化理解为：(avg * (today-1) + assets * (daysInMonth - today + 1)) / daysInMonth
 *
 *
 * @param currentAverage The average assets from the start of the month up to yesterday.
 * @param today The current day of the month (1-based).
 * @param currentAssets The total assets currently held.
 * @param daysLeft The number of days remaining in the month, including today.
 * @param daysInMonth The total number of days in the current month.
 * @return The predicted average assets for the entire month.
 */
private fun calcPredictedAvg(
    currentAverage: Double,
    today: Int,
    currentAssets: Double,
    daysLeft: Int,
    daysInMonth: Int
): Double = (currentAverage * (today - 1) + currentAssets * daysLeft) / daysInMonth

private fun getDaysInMonth(year: Int, month: Int): Int {
    return if (month == 2) {
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
    } else if (month in listOf(4, 6, 9, 11)) 30 else 31
}


@Composable
fun InterestRate(modifier: Modifier = Modifier) {
    // --- 第二个 Row: 年化利率计算 (留空) ---
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("年化利率计算 (待开发)", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("功能建设中...", color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
