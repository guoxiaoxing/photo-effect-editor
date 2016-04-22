package com.guoxiaoxing.editor.widget;

import android.content.Context;

import com.guoxiaoxing.editor.R;
import com.guoxiaoxing.editor.process.AverageSmoothTransformation;
import com.guoxiaoxing.editor.process.BrightnessTransformation;
import com.guoxiaoxing.editor.process.ColorBostUpTransformation;
import com.guoxiaoxing.editor.process.ContrastTransformation;
import com.guoxiaoxing.editor.process.EffectCreator;
import com.guoxiaoxing.editor.process.EffectTransformation;
import com.guoxiaoxing.editor.process.EmbossTransformation;
import com.guoxiaoxing.editor.process.EngravingTransformation;
import com.guoxiaoxing.editor.process.FlippingTransformation;
import com.guoxiaoxing.editor.process.GammaCorrectionTransformation;
import com.guoxiaoxing.editor.process.GaussianBlurTransformation;
import com.guoxiaoxing.editor.process.GrayscaleTransformation;
import com.guoxiaoxing.editor.process.HDRTransformation;
import com.guoxiaoxing.editor.process.HisEqualTransformation;
import com.guoxiaoxing.editor.process.InvertTransformation;
import com.guoxiaoxing.editor.process.LeftRightMotionBlurTransformation;
import com.guoxiaoxing.editor.process.LomoEffectTransformation;
import com.guoxiaoxing.editor.process.NeonEffectTransformation;
import com.guoxiaoxing.editor.process.RomanticEmbossTransformation;
import com.guoxiaoxing.editor.process.SaturationTransformation;
import com.guoxiaoxing.editor.process.SharpenTransformation;
import com.guoxiaoxing.editor.process.SketchEffectTransformation;
import com.guoxiaoxing.editor.process.SmoothTransformation;
import com.guoxiaoxing.editor.process.SoftGlowEffectTransformation;
import com.guoxiaoxing.editor.process.TVTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamxuanlu@gmail.com on 3/17/2015.
 */
public class ToolStructureBuilder {

    private Context context;

    public ToolStructureBuilder(Context context) {
        this.context = context;
    }

    private BaseToolObject enhance() {
        BaseToolObject enhances = new BaseToolObject();
        enhances.name = this.context.getResources().getString(R.string.enhance_tool_name);
        enhances.iconResourceId = R.drawable.ic_enhance;

        BaseToolObject hisEqual = new EffectToolObject();
        hisEqual.name = "Histogram";
        hisEqual.transform = new HisEqualTransformation();
        enhances.addChild(hisEqual);

        //HDR
        BaseToolObject hdr = new EffectToolObject();
        hdr.name = "PseudoHDR";
        hdr.transform = new HDRTransformation();
        enhances.addChild(hdr);

        return enhances;
    }

    private BaseToolObject effects() {
        BaseToolObject effects = new BaseToolObject();
        effects.name = context.getString(R.string.editor_tool_name_effects);
        effects.iconResourceId = R.drawable.ic_effects;

        //Sepia
        BaseToolObject sepia = new EffectToolObject();
        sepia.name = "Sepia";
        float[] sp = new float[]{.393f, .769f, .189f, .349f, .686f, .168f, .272f, .534f, .131f};
        sepia.transform = new EffectTransformation(
                EffectCreator.extractEffect(sp, EffectCreator.ColorToneIntensity.BLUE, (short) 30));
        effects.addChild(sepia);

        //TV Effect
        BaseToolObject tveffect = new EffectToolObject();
        tveffect.name = "Television";
        tveffect.transform = new TVTransformation();
        effects.addChild(tveffect);

        //Oil Effect
//        BaseToolObject oilEffect = new EffectToolObject();
//        oilEffect.name = "Oil";
//        oilEffect.transform = new OilEffectTransformation();
//        effects.addChild(oilEffect);

        //Sketch - Threshold
        BaseToolObject sketch = new EffectToolObject();
        sketch.name = "Sketch";
        sketch.transform = new SketchEffectTransformation();
        effects.addChild(sketch);

        //Neon
        BaseToolObject neon = new EffectToolObject();
        neon.name = "Neon";
        neon.transform = new NeonEffectTransformation();
        effects.addChild(neon);

        //Lomo
        BaseToolObject lomo = new EffectToolObject();
        lomo.name = "Lomo";
        lomo.transform = new LomoEffectTransformation();
        effects.addChild(lomo);

        //Metropolis
        BaseToolObject metropolis = new EffectToolObject();
        metropolis.name = "Grayscale";
        metropolis.transform = new GrayscaleTransformation();
        effects.addChild(metropolis);

        //Invert
        BaseToolObject invert = new EffectToolObject();
        invert.name = "Negative";
        invert.transform = new InvertTransformation();
        effects.addChild(invert);

        //Highlight
//        BaseToolObject highlight = new EffectToolObject();
//        highlight.name = "Highlight";
//        highlight.transform = new HighlightTransformation();
//        effects.addChild(highlight);

        return effects;
    }

    private BaseToolObject filters() {
        //Filters Parent Object
        BaseToolObject filters = new BaseToolObject();
        filters.name = "Filters";
        filters.iconResourceId = R.drawable.ic_filters2;

        //Gaussian Blur
        BaseToolObject gausianBlur = new EffectToolObject();
        gausianBlur.name = "Gaussian Blur";
        gausianBlur.transform = new GaussianBlurTransformation();
        filters.addChild(gausianBlur);

        //Emboss
        BaseToolObject emboss = new EffectToolObject();
        emboss.name = "Emboss";
        emboss.transform = new EmbossTransformation();
        filters.addChild(emboss);

        //RomanticEmboss
        BaseToolObject romanticEmboss = new EffectToolObject();
        romanticEmboss.name = "Roman Emboss";
        romanticEmboss.transform = new RomanticEmbossTransformation();
        filters.addChild(romanticEmboss);

        //Engraving
        BaseToolObject engraving = new EffectToolObject();
        engraving.name = "Engraving";
        engraving.transform = new EngravingTransformation();
        filters.addChild(engraving);

        //Sharpen
        BaseToolObject sharpen = new EffectToolObject();
        sharpen.name = "Sharpen";
        sharpen.transform = new SharpenTransformation();
        filters.addChild(sharpen);

        //Left to Right Motion Blur
        BaseToolObject lrMotion = new EffectToolObject();
        lrMotion.name = "LR Motion";
        lrMotion.transform = new LeftRightMotionBlurTransformation();
        filters.addChild(lrMotion);

        //Average Smooth
        BaseToolObject avgSmooth = new EffectToolObject();
        avgSmooth.name = "Average";
        avgSmooth.transform = new AverageSmoothTransformation();
        filters.addChild(avgSmooth);

        //Smooth
        BaseToolObject smooth = new EffectToolObject();
        smooth.name = "Smooth";
        smooth.transform = new SmoothTransformation();
        filters.addChild(smooth);

        //SoftGlow
        BaseToolObject softGlow = new EffectToolObject();
        softGlow.name = "Glow";
        softGlow.transform = new SoftGlowEffectTransformation();
        filters.addChild(softGlow);

        return filters;
    }


    private BaseToolObject brightness() {

        //Brightness
        BaseToolObject brightness = new OptimizeToolObject();
        brightness.name = "Brightness";
        brightness.transform = new BrightnessTransformation();
        brightness.iconResourceId = R.drawable.ic_brightness;

        return brightness;
    }

    private BaseToolObject contrast() {
        BaseToolObject contrast = new OptimizeToolObject();
        contrast.name = "Contrast";
        contrast.iconResourceId = R.drawable.ic_contrast;
        contrast.transform = new ContrastTransformation();
        return contrast;
    }

    private BaseToolObject saturation() {
        BaseToolObject saturation = new OptimizeToolObject();
        saturation.name = "Saturation";
        saturation.transform = new SaturationTransformation();
        saturation.iconResourceId = R.drawable.ic_saturation;
        return saturation;
    }

    private BaseToolObject gamma() {
        //Gamma Correction
        BaseToolObject gamma = new OptimizeToolObject();
        gamma.name = "Gamma";
        gamma.transform = new GammaCorrectionTransformation();
        gamma.iconResourceId = R.drawable.ic_gamma;

        return gamma;
    }

    private BaseToolObject colorBostUp() {
        //Color Bost Up Group
        BaseToolObject colorBostGroup = new BaseToolObject();
        colorBostGroup.name = "Color Lighten";
        colorBostGroup.iconResourceId = R.drawable.ic_color;

        //RED
        BaseToolObject clbuRed = new OptimizeToolObject();
        clbuRed.name = "Red";
        clbuRed.transform = new ColorBostUpTransformation(ColorBostUpTransformation.RED);
        clbuRed.iconResourceId = R.drawable.ic_red_color;
        colorBostGroup.addChild(clbuRed);

        //GREEN
        BaseToolObject clbuGreen = new OptimizeToolObject();
        clbuGreen.name = "Green";
        clbuGreen.transform = new ColorBostUpTransformation(ColorBostUpTransformation.GREEN);
        clbuGreen.iconResourceId = R.drawable.ic_green_color;
        colorBostGroup.addChild(clbuGreen);

        //BLUE
        BaseToolObject clbuBlue = new OptimizeToolObject();
        clbuBlue.name = "Blue";
        clbuBlue.transform = new ColorBostUpTransformation(ColorBostUpTransformation.BLUE);
        clbuBlue.iconResourceId = R.drawable.ic_blue_color;
        colorBostGroup.addChild(clbuBlue);

        return colorBostGroup;
    }

    private BaseToolObject flipping() {
        BaseToolObject flipping = new BaseToolObject();
        flipping.name = "Flipping";
        flipping.iconResourceId = R.drawable.ic_flip;

        //HORIZONTAL
        BaseToolObject horizontal = new EffectToolObject();
        horizontal.name = "Horizontal";
        horizontal.transform = new FlippingTransformation(FlippingTransformation.FLIP_HORIZONTAL);
        flipping.addChild(horizontal);

        //VERTICAL
        BaseToolObject vertical = new EffectToolObject();
        vertical.name = "Vertical";
        vertical.transform = new FlippingTransformation(FlippingTransformation.FLIP_VERTICAL);
        flipping.addChild(vertical);

        return flipping;
    }

    public List<BaseToolObject> build() {
        List<BaseToolObject> lst = new ArrayList<BaseToolObject>();
        lst.add(enhance());
        lst.add(effects());
        lst.add(filters());
        lst.add(flipping());
        lst.add(brightness());
        lst.add(saturation());
        lst.add(gamma());
        lst.add(contrast());
        lst.add(colorBostUp());

        return lst;
    }
}