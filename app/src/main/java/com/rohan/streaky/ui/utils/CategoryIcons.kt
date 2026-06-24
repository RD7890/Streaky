package com.rohan.streaky.ui.utils

import com.rohan.streaky.R

object CategoryIcons {

    fun drawableRes(iconName: String): Int = when (iconName) {
        "flame_running"      -> R.drawable.flame_running
        "flame_flex"         -> R.drawable.flame_flex
        "flame_zen"          -> R.drawable.flame_zen
        "flame_graduate"     -> R.drawable.flame_graduate
        "flame_gym"          -> R.drawable.flame_gym
        "flame_joy"          -> R.drawable.flame_joy
        "flame_victory"      -> R.drawable.flame_victory
        "flame_mascot_cool"  -> R.drawable.flame_mascot_cool
        "flame_sleeping"     -> R.drawable.flame_sleeping
        else                 -> R.drawable.flame_running
    }

    fun forCategory(category: String): String = when (category) {
        "Health"   -> "flame_running"
        "Fitness"  -> "flame_flex"
        "Mind"     -> "flame_zen"
        "Study"    -> "flame_graduate"
        "Work"     -> "flame_gym"
        "Social"   -> "flame_joy"
        "Finance"  -> "flame_victory"
        "Creative" -> "flame_mascot_cool"
        else       -> "flame_sleeping"
    }
}
