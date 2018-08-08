package com.androappdroid.smashem;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

public class DialogManager {

    public static Dialog getGameOverDialog(Context context)
    {
        ///======================================= progress dialog init ========================

        Dialog dialog_progress = new Dialog(context);
        dialog_progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_progress.setContentView(R.layout.custom_dialog_game_over);
        dialog_progress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog_progress.setCancelable(false);
        //activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return dialog_progress;
    }
}
