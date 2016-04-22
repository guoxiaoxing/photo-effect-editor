package com.guoxiaoxing.editor.process;

import android.graphics.Bitmap;

public interface ITransformation {
	Bitmap perform(Bitmap inp);
}
