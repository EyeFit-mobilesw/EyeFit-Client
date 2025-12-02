package com.example.eyefit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eyefit.R

@Composable
fun EyefitButton(
    text: String,
    onClick: () -> Unit,
    fontSize: Int = 17,
    fontWeight: FontWeight = FontWeight.SemiBold,
    eyeSize: Int = 30
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1A1A1A),
        modifier = Modifier.wrapContentWidth()
    ) {

        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 👀 눈 이미지 + 중앙정렬 박스
            Box(
                modifier = Modifier.height(eyeSize.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_eyes),
                    contentDescription = null,
                    modifier = Modifier.size(eyeSize.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier.height(eyeSize.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = fontSize.sp,
                    fontWeight = fontWeight
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}
