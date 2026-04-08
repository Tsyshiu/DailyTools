package com.github.tsyshiu.dailytools.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.github.tsyshiu.dailytools.ui.MPadding
import com.github.tsyshiu.dailytools.ui.MSpace

@Composable
fun HuntForDeals() {
    val clipboardManager = LocalClipboardManager.current

    // 使用 TextFieldState 以支持 scrollState 同步
    val inputState = rememberTextFieldState()
    val outputState = rememberTextFieldState()

    // 创建两个 ScrollState 用于同步
    val scrollState = rememberScrollState()

    // 转换逻辑
    fun performConversion() {
        val currentInput = inputState.text.toString()
        val result = convertMartianToNormal(currentInput)
        if (outputState.text.toString() != result) {
            outputState.setTextAndPlaceCursorAtEnd(result)
            if (result.isNotBlank()) {
                clipboardManager.setText(AnnotatedString(result))
            }
        }
    }

    // 监听输入变化
    LaunchedEffect(inputState.text) {
        performConversion()
    }

    // 自动识别剪贴板文本 (进入页面时)
    LaunchedEffect(Unit) {
        clipboardManager.getText()?.let {
            if (it.text.isNotBlank()) {
                inputState.setTextAndPlaceCursorAtEnd(it.text)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(MPadding.screenPadding)) {
        Text("羊毛工具", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(MSpace.underH1Title))

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                state = inputState,
                label = { Text("待转换火星文") },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                scrollState = scrollState
            )

            Spacer(Modifier.width(8.dp))

            Button(modifier = Modifier.fillMaxHeight(0.4f), onClick = { performConversion() }) {
                Text("转换".toCharArray().joinToString("\n"))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Text("转换文本：", style = MaterialTheme.typography.titleMedium)
        // Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            state = outputState,
            readOnly = true,
            modifier = Modifier.fillMaxWidth().weight(1f),
            label = { Text("转换结果 (已自动复制)") },
            scrollState = scrollState
        )
    }
}

// 提取规则为常量，避免重复创建
private val MARTIAN_RULES = listOf(
    "①" to "1", "②" to "2", "③" to "3", "④" to "4", "⑤" to "5",
    "⑥" to "6", "⑦" to "7", "⑧" to "8", "⑨" to "9", "⑩" to "10",
    "𝙕𝙝𝙞𝙁𝙪𝘽𝙖𝙤" to "支付宝", "𝓏𝒽𝒾𝒻𝓊𝒷𝒶ℴ" to "支付宝", "吱Fu堡" to "支付宝",
    "αρρ" to "app", "αpp" to "app", "芸" to "云", "亓" to "元",
    "立剪J" to "立减金", "立剪" to "立减", "J" to "金", "贴Jin" to "贴金",
    "奍" to "券", "Fu" to "付", "fu" to "付", "吱" to "支", "嗖" to "搜",
    "v" to "vx", "V" to "vx", "呺" to "号", "丑tuan" to "美团",
    "hang" to "行", "珩" to "行", "佧" to "卡", "垠" to "银",
    "中hang" to "中行", "农hang" to "农行", "工hang" to "工行",
    "交hang" to "行", "兌" to "兑", "幤" to "币", "奬" to "奖",
    "xyk" to "信用卡", "cxk" to "储蓄卡", "荭" to "红", "忦" to "价",
    "開" to "开", "啭杖" to "转账", "満" to "满", "剪" to "减",
    "名鹅" to "名额", "报茗" to "报名", "紬" to "抽", "牰" to "抽", "荟" to "会",
    "單" to "单", "\uD83C\uDC04" to "中", "唰" to "刷",
    "消沸" to "消费", "biao" to "标", "蕙" to "惠", "搶" to "抢"
).sortedByDescending { it.first.length } // 关键：按长度倒序排列，确保优先匹配长词

private val MARTIAN_RULES_MAP = MARTIAN_RULES.toMap()

// 预编译组合正则表达式
private val COMBINED_MARTIAN_REGEX =
    Regex(MARTIAN_RULES.joinToString("|") { Regex.escape(it.first) })

// 预编译数字 o 替换正则
private val DIGIT_O_REGEX = Regex("\\do+")

private val DOT_REGEX = Regex("(?<!\\d)\\.(?!\\d)")

/**
 * 优化后的转换函数
 *
 * 核心优化点：
 * 1. 使用预编译的正则表达式一次扫描替换所有规则，避免循环 replace (O(N) vs O(N*M))。
 * 2. 使用 Sequence 处理行，减少中间集合创建。
 * 3. 提取常量，避免重复计算。
 */
fun convertMartianToNormal(input: String): String {
    if (input.isBlank()) return ""

    @Suppress("SimplifiableCallChain")
    return input.lineSequence().map { line ->
        val trimmed = line.trim()
        val isUrlLine = trimmed.startsWith("http://") ||
                trimmed.startsWith("https://") ||
                trimmed.startsWith("mp://")

        if (isUrlLine) {
            line
        } else {
            // 1. 删除点号
            var processed = line.replace(DOT_REGEX, "")

            // 2. 一次性替换火星文规则 (高性能核心)
            processed = COMBINED_MARTIAN_REGEX.replace(processed) { matchResult ->
                MARTIAN_RULES_MAP[matchResult.value] ?: matchResult.value
            }

            // 3. 处理数字中的 'o' -> '0'
            processed = DIGIT_O_REGEX.replace(processed) { matchResult ->
                matchResult.value.replace('o', '0')
            }

            processed
        }
    }.joinToString("\n")
}
