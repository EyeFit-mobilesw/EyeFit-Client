package com.example.eyefit.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.eyefit.R

@Composable
fun CharacterWithBackground(
    day: Int,
    modifier: Modifier = Modifier
) {

    val characterRes = when (day) {
        1, 4 -> R.drawable.character_day1
        2, 5 -> R.drawable.character_day2
        3, 6 -> R.drawable.character_day3
        else -> R.drawable.character_day7
    }

    // 이제 Box 로 wrapping만!
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = characterRes),
            contentDescription = null,
            modifier = Modifier
                .size(90.dp) // 필요시 조절
        )
    }
}
