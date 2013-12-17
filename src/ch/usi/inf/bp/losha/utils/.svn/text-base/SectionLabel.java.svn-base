/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.usi.inf.bp.losha.R;

/**
 * The colored labels/sections.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class SectionLabel extends LinearLayout {
	private final Context context;
	private View view;
	private TextView textView;
	private TextView extraTextView;
	private ImageView refresh;

	public SectionLabel(final Context context) {
		super(context);
		this.context = context;
		this.inflate();
	}

	public SectionLabel(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.inflate();
		final TypedArray arr = context.obtainStyledAttributes(attrs,
				R.styleable.custom);
		final CharSequence text = arr.getString(R.styleable.custom_text);
		if (text != null) {
			setText(text.toString());
		}
		final boolean hasRefresh = arr.getBoolean(R.styleable.custom_has_refresh, false);
		if (hasRefresh) {
			setHasRefresh();
		}
		final CharSequence extraText = arr.getString(R.styleable.custom_extra_text);
		if (extraText != null) {
			setExtraText(extraText.toString());
		}
		arr.recycle();
	}

	public void setExtraText(final String extra) {
		extraTextView.setVisibility(View.VISIBLE);
		extraTextView.setText(extra);
	}

	public String getText() {
		return textView.getText().toString();
	}

	private void inflate() {
		final LayoutInflater layoutInflater = (LayoutInflater) context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.section_label, this);
		textView = (TextView) view.findViewById(R.id.label_text);
		extraTextView = (TextView) view.findViewById(R.id.label_extra_text);
		refresh = (ImageView) view.findViewById(R.id.refreshButton);
	}

	public void setText(final String text) {
		textView.setText(text);
	}

	public void setHasRefresh() {
		refresh.setVisibility(View.VISIBLE);
	}
}
