package com.app.dsalingo.ui.screens.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dsalingo.ui.components.DuoButton
import com.app.dsalingo.ui.components.LottieAnimationRawRes
import com.app.dsalingo.ui.components.LottieAnimationView
import com.app.dsalingo.ui.theme.DuoGray
import com.app.dsalingo.ui.theme.DuoRed
import com.app.dsalingo.ui.theme.DuoRedDark
import com.app.dsalingo.R

@Composable
fun GameOverScreen(onQuit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(250.dp), contentAlignment = Alignment.Center) {
//            LottieAnimationView(url = "https://lottie.host/80447384-5a67-466d-966a-12798e3b3303/4O2oN5kF0T.json") // Sad Owl
            LottieAnimationRawRes(resId = R.raw.angry_bird)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("Out of hearts!", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = DuoRed)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Don't give up! Review your mistakes and try again.",
            fontSize = 18.sp,
            color = DuoGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        DuoButton(
            text = "QUIT SESSION",
            onClick = onQuit,
            color = DuoRed,
            shadowColor = DuoRedDark,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
