package com.androiddevelopers.freelanceapp.util

import android.app.AlertDialog
import com.androiddevelopers.freelanceapp.R

fun setupErrorDialog(errorDialog: AlertDialog) {
    with(errorDialog) {
        setTitle(context.getString(R.string.login_dialog_error))
        setCancelable(false)
        setButton(
            AlertDialog.BUTTON_POSITIVE,
            context.getString(R.string.ok)
        ) { dialog, _ ->
            dialog.cancel()
        }
    }
}