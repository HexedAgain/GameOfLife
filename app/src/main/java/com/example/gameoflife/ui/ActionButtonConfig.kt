package com.example.gameoflife.ui

import com.example.gameoflife.R

data class ActionButtonConfig(
    val contentDescriptionResId: Int,
    val textResId: Int,
    val iconResId: Int
) {
    enum class ActionType {
        PLAY,
        PAUSE,
        RANDOMISE,
        CLEAR,
        SETTINGS
    }

    companion object {
        fun actionFor (actionType: ActionType): ActionButtonConfig {
            return when (actionType) {
                ActionType.PLAY -> ActionButtonConfig(
                    iconResId = R.drawable.play_arrow,
                    textResId = R.string.play,
                    contentDescriptionResId = R.string.content_descr_play_game_of_life,
                )

                ActionType.PAUSE -> ActionButtonConfig(
                    iconResId = R.drawable.pause,
                    textResId = R.string.pause,
                    contentDescriptionResId = R.string.content_descr_pause_game_of_life
                )
                ActionType.RANDOMISE -> ActionButtonConfig(
                    iconResId = R.drawable.refresh,
                    textResId = R.string.randomise_cells,
                    contentDescriptionResId = R.string.content_descr_randomise_cell_liveness
                )
                ActionType.CLEAR -> ActionButtonConfig(
                    iconResId = R.drawable.clear,
                    textResId = R.string.clear_cells,
                    contentDescriptionResId = R.string.content_descr_clear_cells
                )
                ActionType.SETTINGS -> ActionButtonConfig(
                    iconResId = R.drawable.settings,
                    textResId = R.string.game_settings,
                    contentDescriptionResId = R.string.content_descr_game_settings
                )
            }
        }
    }
}