package top.ourfor.app.iplay.util;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;

import androidx.core.content.res.ResourcesCompat;

import top.ourfor.app.iplay.R;

public class FontUtil {

    public static SpannableString applyEmojiFont(Context context, String text, int fontResId) {
        Typeface emojiTypeface = ResourcesCompat.getFont(context, fontResId);
        SpannableString spannableString = new SpannableString(text);
        for (int i = 0; i < text.length(); i++) {
            if (Character.isSurrogate(text.charAt(i))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    assert emojiTypeface != null;
                    spannableString.setSpan(new TypefaceSpan(emojiTypeface), i, i + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                i++;
            }
        }
        return spannableString;
    }
}
