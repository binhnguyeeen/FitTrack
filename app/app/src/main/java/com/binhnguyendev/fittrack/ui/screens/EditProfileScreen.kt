package com.binhnguyendev.fittrack.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.binhnguyendev.fittrack.ui.components.AvatarPicker
import com.binhnguyendev.fittrack.ui.components.FtIcon
import com.binhnguyendev.fittrack.ui.components.FtSectionLabel
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.components.FtUnderlineInput
import com.binhnguyendev.fittrack.ui.components.ftClick
import com.binhnguyendev.fittrack.ui.components.pressScale
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontHeadline
import com.binhnguyendev.fittrack.ui.util.ImageStorage
import com.binhnguyendev.fittrack.ui.vm.EditProfileViewModel
import com.binhnguyendev.fittrack.ui.vm.ftViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    val vm = ftViewModel { repos -> EditProfileViewModel(repos) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            scope.launch { vm.photo = ImageStorage.persist(context, uri) }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(FT.bg)
            .padding(top = statusTop),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(40.dp).ftClick(onBack),
                contentAlignment = Alignment.Center,
            ) { FtIcon("arrowL", size = 20.dp, color = FT.text) }
            FtText(
                "EDIT PROFILE",
                modifier = Modifier.weight(1f),
                color = FT.text2,
                size = 14,
                letterSpacingEm = 0.04f,
                align = TextAlign.Center,
            )
            Box(
                Modifier
                    .pressScale(0.97f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FT.orange)
                    .ftClick { vm.save(onBack) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                FtText("Save", color = FT.text, size = 13, weight = FontWeight.Medium)
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                AvatarPicker(vm.photo) {
                    picker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                }
                if (vm.photo != null) {
                    FtText(
                        "Remove photo",
                        modifier = Modifier.ftClick { vm.photo = null },
                        color = FT.text3,
                        size = 12,
                    )
                }
            }

            FtSectionLabel("Display name")
            FtUnderlineInput(
                value = vm.name,
                onValueChange = { vm.name = it },
                placeholder = "Your name",
                size = 24,
                family = FontHeadline,
            )
        }
    }
}
