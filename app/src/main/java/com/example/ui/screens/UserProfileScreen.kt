package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.UserPreferences
import com.example.data.repository.AuthResult
import com.example.ui.components.ColorPaletteGrid
import com.example.ui.components.ColorPickerDialog
import com.example.ui.localization.LocalAppStrings
import com.example.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

val AvatarEmojis = listOf("👤", "💼", "🚀", "🌟", "🎯", "👑", "⚡", "🦁", "🌿", "💡")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: TaskViewModel,
    preferences: UserPreferences,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    isArabic: Boolean = true
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val primaryColor = MaterialTheme.colorScheme.primary

    var name by remember(preferences) { mutableStateOf(preferences.userName) }
    var phone by remember(preferences) { mutableStateOf(preferences.userPhone) }
    var address by remember(preferences) { mutableStateOf(preferences.userAddress) }
    var jobTitle by remember(preferences) { mutableStateOf(preferences.userJobTitle) }
    var selectedAvatarIndex by remember(preferences) { mutableStateOf(preferences.userAvatarIndex) }
    var avatarColorHex by remember(preferences) { mutableStateOf(preferences.userAvatarColor) }

    var showAvatarColorPicker by remember { mutableStateOf(false) }

    // Password change fields
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showOldPass by remember { mutableStateOf(false) }
    var showNewPass by remember { mutableStateOf(false) }
    var passError by remember { mutableStateOf("") }
    var isChangingPass by remember { mutableStateOf(false) }

    var isSavingProfile by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("user_profile_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("profile_back_button")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.userProfile,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Logout Action
                    TextButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = strings.logout,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Avatar Header Showcase
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Big Avatar Circle with dynamic color and emoji
                        val avatarColor = Color(avatarColorHex)
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(avatarColor.copy(alpha = 0.8f), avatarColor)
                                    )
                                )
                                .border(3.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val emoji = AvatarEmojis.getOrElse(selectedAvatarIndex) { "👤" }
                            Text(text = emoji, fontSize = 42.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (name.isNotBlank()) name else strings.guestUser,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (jobTitle.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = jobTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = primaryColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (preferences.userEmail.isNotBlank()) preferences.userEmail else "user@enjaz.app",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Avatar Picker Row
                        Text(
                            text = strings.avatarStyle,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AvatarEmojis.take(6).forEachIndexed { idx, emo ->
                                val isSelected = selectedAvatarIndex == idx
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) primaryColor.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) primaryColor else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedAvatarIndex = idx },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emo, fontSize = 20.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Avatar Color Picker trigger
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ColorPaletteGrid.take(6).forEach { colHex ->
                                val isColSelected = avatarColorHex == colHex
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(colHex))
                                        .border(
                                            width = if (isColSelected) 2.dp else 0.dp,
                                            color = if (isColSelected) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { avatarColorHex = colHex },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isColSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            TextButton(onClick = { showAvatarColorPicker = true }) {
                                Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(16.dp), tint = primaryColor)
                            }
                        }
                    }
                }
            }

            // Personal Information Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                            Text(
                                text = strings.editProfile,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Full Name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(strings.fullName) },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Phone Number
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(strings.phoneNumber) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Address / City
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text(strings.address) },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Job Title / Bio
                        OutlinedTextField(
                            value = jobTitle,
                            onValueChange = { jobTitle = it },
                            label = { Text(strings.jobTitle) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                isSavingProfile = true
                                scope.launch {
                                    viewModel.updateUserProfile(
                                        name = name,
                                        phone = phone,
                                        address = address,
                                        jobTitle = jobTitle,
                                        avatarIndex = selectedAvatarIndex,
                                        avatarColor = avatarColorHex
                                    )
                                    isSavingProfile = false
                                    Toast.makeText(context, strings.profileSaved, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isSavingProfile) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.save, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Security & Change Password Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                            Text(
                                text = strings.changePassword,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Current Password
                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = {
                                oldPassword = it
                                passError = ""
                            },
                            label = { Text(strings.currentPassword) },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { showOldPass = !showOldPass }) {
                                    Icon(
                                        imageVector = if (showOldPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (showOldPass) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // New Password
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                passError = ""
                            },
                            label = { Text(strings.newPassword) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { showNewPass = !showNewPass }) {
                                    Icon(
                                        imageVector = if (showNewPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (showNewPass) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Confirm Password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                passError = ""
                            },
                            label = { Text(strings.confirmPassword) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (passError.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = passError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (oldPassword.isBlank() || newPassword.isBlank()) {
                                    passError = if (isArabic) "يرجى ملء جميع حقول كلمة المرور" else "Please fill all password fields"
                                    return@Button
                                }
                                if (newPassword != confirmPassword) {
                                    passError = if (isArabic) "كلمة المرور غير متطابقة" else "Passwords do not match"
                                    return@Button
                                }
                                isChangingPass = true
                                scope.launch {
                                    val result = viewModel.changePassword(oldPassword, newPassword)
                                    isChangingPass = false
                                    if (result is AuthResult.Success) {
                                        oldPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                        Toast.makeText(context, strings.passwordChanged, Toast.LENGTH_SHORT).show()
                                    } else if (result is AuthResult.Error) {
                                        passError = if (isArabic) result.messageAr else result.messageEn
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            if (isChangingPass) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text(strings.changePassword, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAvatarColorPicker) {
        ColorPickerDialog(
            initialColorHex = avatarColorHex,
            onColorSelected = { chosenColor ->
                avatarColorHex = chosenColor
            },
            onDismiss = { showAvatarColorPicker = false },
            title = strings.avatarStyle
        )
    }
}
