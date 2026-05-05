package com.app.dsalingo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dsalingo.ui.theme.*

@Composable
fun DuoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = DuoGreen,
    shadowColor: Color = DuoGreenDark,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val currentShadowHeight = if (isPressed) 0.dp else 4.dp
    val currentTopPadding = if (isPressed) 4.dp else 0.dp

    Box(
        modifier = modifier
            .padding(top = currentTopPadding)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .height(56.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Shadow Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp)
                .background(if (enabled) shadowColor else DuoGrayLight, RoundedCornerShape(16.dp))
        )
        
        // Surface Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(if (enabled) color else DuoGray, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun DuoSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DuoButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        color = DuoBlue,
        shadowColor = DuoBlueDark
    )
}
