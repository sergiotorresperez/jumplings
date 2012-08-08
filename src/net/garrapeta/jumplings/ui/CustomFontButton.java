package net.garrapeta.jumplings.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import net.garrapeta.jumplings.R;

public class CustomFontButton extends Button {

    public CustomFontButton(Context context, AttributeSet set) {
        super(context, set);

        String fontPath = null;
        
        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.CustomFontView);
        
        // se intenta pillar propiedad del estilo y del theme
        if (ta != null) {
        	int defStyleAttr = ta.getResourceId(R.styleable.CustomFontView_customFontViewAppearance, 0);
     		
    		// NOTE: http://developer.android.com/reference/android/content/res/Resources.Theme.html#obtainStyledAttributes%28android.util.AttributeSet,%20int[],%20int,%20int%29
    		
    		// con esto lo intenta pillar del estilo, y si no lo encuentra, lo intenta pillar del estilo 
    		// apuntado por defStyleAttr
    		TypedArray ta2 = getContext().obtainStyledAttributes(set, R.styleable.CustomFontView, 0, defStyleAttr);
    		fontPath = ta2.getString(R.styleable.CustomFontView_fontPath);
    		
    		ta2.recycle();
        	ta.recycle();
        }
        
        
        // se intenta pillar propiedad del XML
        String fromXML = set.getAttributeValue(null, "fontPath");
        if (fromXML != null) {
        	fontPath = fromXML;
        }
        
        // si se ha encontrado definici�n de fuente, se aplica
        if (fontPath != null) {
            Typeface font = Typeface.createFromAsset(context.getAssets(), fontPath);
            super.setTypeface(font);
        }
        
        
        
    }

}