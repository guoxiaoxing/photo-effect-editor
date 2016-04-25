package com.guoxiaoxing.editor.process.effect;

import android.graphics.Bitmap;

public interface ITransformation {
	Bitmap perform(Bitmap inp);
}
