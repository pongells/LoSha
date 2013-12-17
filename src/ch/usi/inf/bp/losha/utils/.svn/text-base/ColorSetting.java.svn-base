/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.utils;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.usi.inf.bp.losha.R;

/**
 * Custom view used in the settings (color picker).
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class ColorSetting extends LinearLayout {
	private final Context context;
	private View view;
	private TextView titleDisplay;
	private LinearLayout selectedColorDisplay;
	private int selectedColor;
	private View target;
	private ViewGroup targetGroup;
	private String targetTag;

	public ColorSetting(final Context context, final int defaultColor) {
		super(context);
		this.context = context;
		this.inflate();
		this.selectedColor = defaultColor;
	}

	public ColorSetting(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.inflate();
		final TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.custom);

		final int color = arr.getColor(R.styleable.custom_color, context.getResources().getColor(R.color.title_background));
		setColor(color);

		final CharSequence text = arr.getString(R.styleable.custom_text);
		if (text != null) {
			setText(text.toString());
		}

		arr.recycle();
	}

	public void setTarget(final View target) {
		this.target = target;
	}
	public void setTarget(final ViewGroup view, final String tag) {
		this.targetGroup = view;
		this.targetTag = tag;
	}

	private void colorAllTargets(final ViewGroup view, final String tag) {
		final int childs = view.getChildCount();
		for (int i=0;i<childs;i++) {
			final View v = view.getChildAt(i);
			if (v instanceof ViewGroup) {
				colorAllTargets((ViewGroup) v, tag);
			}
			if (v.getTag() != null && v.getTag().equals(tag)) {
				v.setBackgroundColor(selectedColor);
			}
		}
	}

	public void setColor(final int color) {
		this.selectedColor = color;
		selectedColorDisplay.setBackgroundColor(color);
	}
	public int getColor() {
		return selectedColor;
	}

	public void setText(final String text) {
		titleDisplay.setText(text);
	}

	private void inflate() {
		final LayoutInflater layoutInflater = (LayoutInflater) context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.settings_color, this);

		selectedColorDisplay = (LinearLayout) view.findViewById(R.id.selectedColor);
		titleDisplay = (TextView) view.findViewById(R.id.text);

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				new AmbilWarnaDialog(context, selectedColor, new OnAmbilWarnaListener() {
					@Override
					public void onOk(final AmbilWarnaDialog dialog, final int color) {
						setColor(color);
						if (target != null) {
							target.setBackgroundColor(color);
						}
						if (targetGroup != null && targetTag != null) {
							colorAllTargets(targetGroup, targetTag);
						}
					}

					@Override
					public void onCancel(final AmbilWarnaDialog dialog) {}
				}).show();
			}
		});
	}

}
