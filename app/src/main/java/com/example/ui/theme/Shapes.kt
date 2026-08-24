package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import com.example.data.preferences.CardCornerStyle

fun getAppShapes(cardStyle: CardCornerStyle): Shapes {
    val cardRadius = cardStyle.cornerRadiusDp.dp
    return Shapes(
        extraSmall = RoundedCornerShape(cardRadius / 3),
        small = RoundedCornerShape(cardRadius / 2),
        medium = RoundedCornerShape(cardRadius),
        large = RoundedCornerShape(cardRadius + 4.dp),
        extraLarge = RoundedCornerShape(cardRadius + 8.dp)
    )
}
