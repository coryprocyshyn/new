package com.example.trailerquest

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class QuestTask(
    val id: Int,
    val zone: String,
    val title: String,
    val xp: Int,
    var isCompleted: MutableState<Boolean> = mutableStateOf(false)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TrailerQuestApp()
            }
        }
    }
}

@Composable
fun TrailerQuestApp() {
    val toneGen = remember {
        try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception) {
            null
        }
    }

    fun playSound(toneType: Int, durationMs: Int = 150) {
        toneGen?.startTone(toneType, durationMs)
    }

    fun playVictoryFanfare() {
        Thread {
            try {
                toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 200)
                Thread.sleep(220)
                toneGen?.startTone(ToneGenerator.TONE_PROP_ACK, 200)
                Thread.sleep(220)
                toneGen?.startTone(ToneGenerator.TONE_PROP_BEEP2, 450)
            } catch (_: Exception) {}
        }.start()
    }

    val tasks = remember {
        mutableStateListOf(
            QuestTask(1, "🍳 Zone 1: Kitchen & Cooking Gear", "Pack all silver & utensils", 50),
            QuestTask(2, "🍳 Zone 1: Kitchen & Cooking Gear", "Box up pots, pans, and lids", 50),
            QuestTask(3, "🍳 Zone 1: Kitchen & Cooking Gear", "Clear out plates, bowls & cups", 50),
            QuestTask(4, "🍳 Zone 1: Kitchen & Cooking Gear", "⭐ Bonus: Wipe down empty cupboards", 75),
            QuestTask(5, "🥫 Zone 2: Pantry Raid & Fridge", "Clear out canned goods & snacks", 50),
            QuestTask(6, "🥫 Zone 2: Pantry Raid & Fridge", "Empty all spices and oils", 50),
            QuestTask(7, "🥫 Zone 2: Pantry Raid & Fridge", "Clean & prop fridge door open", 75),
            QuestTask(8, "🎲 Zone 3: Games & Lounge Vault", "Pack all board games & card decks", 50),
            QuestTask(9, "🎲 Zone 3: Games & Lounge Vault", "Collect outdoor games & gear", 50),
            QuestTask(10, "🎲 Zone 3: Games & Lounge Vault", "⭐ Hidden Item: Find a rogue loose game piece", 75),
            QuestTask(11, "🧹 Zone 4: The Final Sweep", "Gather blankets & linens", 50),
            QuestTask(12, "🧹 Zone 4: The Final Sweep", "👑 Final Master Sweep of all drawers", 100)
        )
    }

    var timerSeconds by remember { mutableStateOf(600) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning, timerSeconds) {
        if (isTimerRunning && timerSeconds > 0) {
            delay(1000L)
            timerSeconds--
            if (timerSeconds == 0) {
                isTimerRunning = false
                playSound(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 600)
            }
        }
    }

    val completedCount = tasks.count { it.isCompleted.value }
    val totalXp = tasks.filter { it.isCompleted.value }.sumOf { it.xp }
    val level = (totalXp / 150) + 1
    val progress = if (tasks.isNotEmpty()) completedCount.toFloat() / tasks.size else 0f
    val isVictory = completedCount == tasks.size

    var hasPlayedVictorySound by remember { mutableStateOf(false) }
    LaunchedEffect(isVictory) {
        if (isVictory && !hasPlayedVictorySound) {
            hasPlayedVictorySound = true
            playVictoryFanfare()
        } else if (!isVictory) {
            hasPlayedVictorySound = false
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF3F4F6))
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFFA855F7))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏕️ Katie's Winter Quest", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Trailer Breakdown Champion", fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text("⭐ XP: $totalXp", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("🏆 Level: $level", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val minutes = timerSeconds / 60
            val seconds = timerSeconds % 60
            val timeFormatted = String.format("%02d:%02d", minutes, seconds)

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("⏱️ SPEED RUN TIMER", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(
                            text = timeFormatted,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (timerSeconds < 60 && timerSeconds > 0) Color.Red else Color(0xFF1F2937)
                        )
                    }
                    Row {
                        Button(
                            onClick = {
                                isTimerRunning = !isTimerRunning
                                playSound(ToneGenerator.TONE_PROP_BEEP, 100)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) Color(0xFFEF4444) else Color(0xFF10B981)
                            )
                        ) {
                            Text(if (isTimerRunning) "Pause" else "Start")
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            onClick = {
                                isTimerRunning = false
                                timerSeconds = 600
                                playSound(ToneGenerator.TONE_PROP_BEEP2, 100)
                            }
                        ) {
                            Text("Reset")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = Color(0xFF10B981),
                trackColor = Color(0xFFE5E7EB)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val groupedTasks = tasks.groupBy { it.zone }

            LazyColumn(modifier = Modifier.weight(1f)) {
                groupedTasks.forEach { (zone, zoneTasks) ->
                    item {
                        Text(
                            text = zone,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                    items(zoneTasks) { task ->
                        TaskRow(
                            task = task,
                            onToggle = { isChecked ->
                                task.isCompleted.value = isChecked
                                if (isChecked) {
                                    playSound(ToneGenerator.TONE_PROP_BEEP, 120)
                                } else {
                                    playSound(ToneGenerator.TONE_PROP_BEEP2, 80)
                                }
                            }
                        )
                    }
                }

                if (isVictory) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🎉 QUEST COMPLETE! 🎉", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Katie has officially beaten the Trailer Breakdown Challenge!", color = Color(0xFF065F46))
                                Text("Total Score: $totalXp XP", fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                Text("🏆 Title Earned: Master Winterizer of the Realm", fontWeight = FontWeight.Medium, color = Color(0xFF047857))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskRow(task: QuestTask, onToggle: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { onToggle(!task.isCompleted.value) },
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted.value) Color(0xFFF0FDF4) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted.value,
                onCheckedChange = { onToggle(it) },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                textDecoration = if (task.isCompleted.value) TextDecoration.LineThrough else TextDecoration.None,
                color = if (task.isCompleted.value) Color.Gray else Color(0xFF1F2937),
                fontSize = 14.sp
            )
            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "+${task.xp} XP",
                    color = Color(0xFFD97706),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
