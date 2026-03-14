package com.example.taskmanger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanger.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────
// DATA MODELS
// ─────────────────────────────────────────────────────────────────
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)

sealed class Screen {
    object Login    : Screen()
    object TaskList : Screen()
    object AddTask  : Screen()
}

// ─────────────────────────────────────────────────────────────────
// MAIN ACTIVITY
// ─────────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TaskMangerTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background),
                ) {
                    TaskManagerApp()
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// APP NAVIGATION & STATE
// ─────────────────────────────────────────────────────────────────
@Composable
fun TaskManagerApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    var tasks         by remember { mutableStateOf(listOf<Task>()) }
    var userEmail     by remember { mutableStateOf("") }

    when (currentScreen) {
        is Screen.Login -> LoginScreen(onLogin = { email ->
            userEmail = email
            currentScreen = Screen.TaskList
        })

        is Screen.TaskList -> TaskListScreen(
            tasks          = tasks,
            userEmail      = userEmail,
            onToggleTask   = { id -> tasks = tasks.map { if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it } },
            onDeleteTask   = { id -> tasks = tasks.filter { it.id != id } },
            onAddTaskClick = { currentScreen = Screen.AddTask },
            onLogout       = { userEmail = ""; currentScreen = Screen.Login },
        )

        is Screen.AddTask -> AddTaskScreen(
            onBack = { currentScreen = Screen.TaskList },
            onSave = { title, desc ->
                tasks = listOf(Task(title = title, description = desc)) + tasks
                currentScreen = Screen.TaskList
            },
        )
    }
}

// ═════════════════════════════════════════════════════════════════
// LOGIN SCREEN  (SOP: C10 — Form/Permission Screen)
// ═════════════════════════════════════════════════════════════════
@Composable
fun LoginScreen(onLogin: (String) -> Unit) {
    var email        by remember { mutableStateOf("") }
    var password     by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMsg     by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
        ) {
            // App icon circle (SOP §9)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(SurfaceColor)
                    .border(1.dp, BorderColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(28.dp),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Screen headline — always neon (SOP §2)
            Text(
                text = "Welcome Back",
                fontSize = 34.sp,
                fontWeight = FontWeight.SemiBold,
                color = Accent,
                letterSpacing = 0.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sign in to manage your tasks",
                fontSize = 14.sp,
                color = TextSecondary,
                letterSpacing = 0.sp,
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email field
            FieldLabel("EMAIL ADDRESS")
            NeonTextField(
                value = email,
                onValueChange = { email = it; errorMsg = "" },
                placeholder = "you@example.com",
                keyboardType = KeyboardType.Email,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password field
            FieldLabel("PASSWORD")
            NeonTextField(
                value = password,
                onValueChange = { password = it; errorMsg = "" },
                placeholder = "Enter password",
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
            )

            AnimatedVisibility(visible = errorMsg.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = errorMsg,
                    fontSize = 12.sp,
                    color = ColorNegative,
                    letterSpacing = 0.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Primary Filled CTA (SOP §5)
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) onLogin(email)
                    else errorMsg = "Please fill in all fields"
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor   = TextOnAccent,
                    disabledContainerColor = Accent.copy(alpha = 0.35f),
                    disabledContentColor   = TextOnAccent.copy(alpha = 0.35f),
                ),
            ) {
                Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ═════════════════════════════════════════════════════════════════
// TASK LIST SCREEN  (SOP: C1 Hero + C3 Stats + C2 Banner + C6 List)
// ═════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<Task>,
    userEmail: String,
    onToggleTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onAddTaskClick: () -> Unit,
    onLogout: () -> Unit,
) {
    val pending   = tasks.filter { !it.isCompleted }
    val completed = tasks.filter { it.isCompleted }
    val initial   = userEmail.firstOrNull()?.uppercaseChar()?.toString() ?: "U"

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    // Avatar circle with user initial (SOP §6)
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceColor)
                            .border(1.dp, BorderColor, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(initial, color = Accent, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable { onLogout() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                        Text("Sign Out", color = TextSecondary, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    scrolledContainerColor = Background,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                shape = CircleShape,
                containerColor = Accent,
                contentColor = TextOnAccent,
                modifier = Modifier.size(56.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task", modifier = Modifier.size(24.dp))
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            // C1: Hero Section
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Text(
                        text = "My Tasks",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Accent,
                        letterSpacing = 0.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (tasks.isEmpty()) "Tap + to add your first task"
                               else "${pending.size} pending · ${completed.size} done",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        letterSpacing = 0.sp,
                    )
                }
            }

            // C3: Stat Summary Row
            item {
                StatSummaryRow(total = tasks.size, pending = pending.size, done = completed.size)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // C2: Feature Banner (only when tasks exist)
            if (tasks.isNotEmpty()) {
                item {
                    FeatureBannerCard(pendingCount = pending.size)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Empty state
            if (tasks.isEmpty()) {
                item { EmptyStateView() }
            }

            // Pending tasks
            if (pending.isNotEmpty()) {
                item { SectionHeader("Pending", pending.size) }
                items(pending, key = { it.id }) { task ->
                    TaskCard(task, onToggle = { onToggleTask(task.id) }, onDelete = { onDeleteTask(task.id) })
                }
            }

            // Completed tasks
            if (completed.isNotEmpty()) {
                item { Spacer(modifier = Modifier.height(8.dp)); SectionHeader("Completed", completed.size) }
                items(completed, key = { it.id }) { task ->
                    TaskCard(task, onToggle = { onToggleTask(task.id) }, onDelete = { onDeleteTask(task.id) })
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════
// C3 — STAT SUMMARY ROW
// ═════════════════════════════════════════════════════════════════
@Composable
fun StatSummaryRow(total: Int, pending: Int, done: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceColor)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatItem(total.toString(),   "TOTAL")
        Box(modifier = Modifier.width(1.dp).height(32.dp).background(BorderColor))
        StatItem(pending.toString(), "PENDING")
        Box(modifier = Modifier.width(1.dp).height(32.dp).background(BorderColor))
        StatItem(done.toString(),    "DONE")
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, letterSpacing = 0.sp)
        Text(label, fontSize = 11.sp, color = TextSecondary, letterSpacing = 1.sp)
    }
}

// ═════════════════════════════════════════════════════════════════
// C2 — FEATURE BANNER CARD  (vibrant gradient — the ONE exception)
// ═════════════════════════════════════════════════════════════════
@Composable
fun FeatureBannerCard(pendingCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        androidx.compose.ui.graphics.Color(0xFF2D5A1B),
                        androidx.compose.ui.graphics.Color(0xFF1A3A0F),
                    ),
                ),
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (pendingCount == 0) "All done! 🎉" else "Keep going! 💪",
                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Accent, letterSpacing = 0.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (pendingCount == 0) "You've completed all your tasks"
                           else "$pendingCount task${if (pendingCount != 1) "s" else ""} still in progress",
                    fontSize = 13.sp,
                    color = TextPrimary.copy(alpha = 0.8f),
                    letterSpacing = 0.sp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Accent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Bolt, contentDescription = null, tint = TextOnAccent, modifier = Modifier.size(22.dp))
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════
// SECTION HEADER
// ═════════════════════════════════════════════════════════════════
@Composable
fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, letterSpacing = 0.sp)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(SurfaceColor)
                .border(1.dp, BorderColor, RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 3.dp),
        ) {
            Text(count.toString(), fontSize = 11.sp, color = TextSecondary)
        }
    }
}

// ═════════════════════════════════════════════════════════════════
// EMPTY STATE  (SOP §9)
// ═════════════════════════════════════════════════════════════════
@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 60.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(SurfaceColor)
                .border(1.dp, BorderColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Outlined.Assignment, contentDescription = null, tint = Accent, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("No tasks yet", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, letterSpacing = 0.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Tap + to add your first task", fontSize = 14.sp, color = TextSecondary, letterSpacing = 0.sp)
    }
}

// ═════════════════════════════════════════════════════════════════
// C6 — TASK CARD  (Item List Row)
// ═════════════════════════════════════════════════════════════════
@Composable
fun TaskCard(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }
    val dateText   = remember(task.createdAt) { dateFormat.format(Date(task.createdAt)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceColor)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Status icon circle (left)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (task.isCompleted) Accent.copy(alpha = 0.12f) else Background)
                .border(1.dp, if (task.isCompleted) Accent else BorderColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (task.isCompleted) Icons.Filled.Check else Icons.Filled.RadioButtonUnchecked,
                contentDescription = "Toggle",
                tint = if (task.isCompleted) Accent else ColorNeutral,
                modifier = Modifier.size(18.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content (center)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (task.isCompleted) TextSecondary else TextPrimary,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = 0.sp,
            )
            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = task.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.sp,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(dateText, fontSize = 11.sp, color = ColorNeutral.copy(alpha = 0.7f), letterSpacing = 0.sp)
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Delete icon (right)
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = TextSecondary.copy(alpha = 0.55f),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

// ═════════════════════════════════════════════════════════════════
// ADD TASK SCREEN  (SOP: C10 — Form Screen)
// ═════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(onBack: () -> Unit, onSave: (String, String) -> Unit) {
    var title       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val canSave = title.isNotBlank()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    // Outlined secondary CTA (SOP §5)
                    OutlinedButton(
                        onClick = { if (canSave) onSave(title.trim(), description.trim()) },
                        enabled = canSave,
                        modifier = Modifier.padding(end = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Accent,
                            disabledContentColor = Accent.copy(alpha = 0.35f),
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, if (canSave) Accent else Accent.copy(alpha = 0.35f),
                        ),
                    ) {
                        Text("Save", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    scrolledContainerColor = Background,
                ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Screen headline — neon (SOP §2)
            Text(
                text = "New Task",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Accent,
                letterSpacing = 0.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("What do you need to get done?", fontSize = 14.sp, color = TextSecondary, letterSpacing = 0.sp)

            Spacer(modifier = Modifier.height(32.dp))

            FieldLabel("TASK TITLE")
            NeonTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "e.g. Finish the report",
            )

            Spacer(modifier = Modifier.height(20.dp))

            FieldLabel("DESCRIPTION")
            NeonTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Optional notes or details",
                singleLine = false,
                minLines = 4,
                maxLines = 6,
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Primary Filled CTA (SOP §5)
            Button(
                onClick = { if (canSave) onSave(title.trim(), description.trim()) },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor   = TextOnAccent,
                    disabledContainerColor = Accent.copy(alpha = 0.35f),
                    disabledContentColor   = TextOnAccent.copy(alpha = 0.35f),
                ),
            ) {
                Text("Save Task", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// SHARED COMPOSABLES
// ─────────────────────────────────────────────────────────────────

/** ALL-CAPS field label with 1sp letter-spacing (SOP §2) */
@Composable
fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        color = TextSecondary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

/**
 * Neon-styled OutlinedTextField (SOP §4 C10)
 *  - Background: #232323 (SurfaceInput)
 *  - Border: #3A3A3A default → #C8FF00 focused
 *  - Radius: 8dp  |  Text 14sp #FFF  |  Placeholder #888
 */
@Composable
fun NeonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextSecondary, fontSize = 14.sp) },
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        textStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, color = TextPrimary, letterSpacing = 0.sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = Accent,
            unfocusedBorderColor    = BorderColor,
            focusedContainerColor   = SurfaceInput,
            unfocusedContainerColor = SurfaceInput,
            focusedTextColor        = TextPrimary,
            unfocusedTextColor      = TextPrimary,
            cursorColor             = Accent,
            focusedTrailingIconColor   = TextSecondary,
            unfocusedTrailingIconColor = TextSecondary,
        ),
    )
}

