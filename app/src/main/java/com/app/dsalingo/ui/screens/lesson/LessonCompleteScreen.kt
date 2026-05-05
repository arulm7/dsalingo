package com.app.dsalingo.ui.screens.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dsalingo.ui.components.DuoButton
import com.app.dsalingo.ui.components.LottieAnimationRawRes
import com.app.dsalingo.ui.theme.DuoGray
import com.app.dsalingo.ui.theme.DuoGreen
import com.app.dsalingo.R

@Composable
fun LessonCompleteScreen(xpReward: Int, onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(250.dp), contentAlignment = Alignment.Center) {
            LottieAnimationRawRes(resId = R.raw.angry_bird)        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("Lesson Complete!", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = DuoGreen)
        Spacer(modifier = Modifier.height(16.dp))
        Text("You earned $xpReward XP today!", fontSize = 18.sp, color = DuoGray)
        Spacer(modifier = Modifier.height(48.dp))
        DuoButton(text = "CONTINUE", onClick = onContinue, modifier = Modifier.fillMaxWidth())
    }
}
