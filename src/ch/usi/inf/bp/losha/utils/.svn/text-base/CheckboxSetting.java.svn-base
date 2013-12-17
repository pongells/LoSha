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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.usi.inf.bp.losha.R;

/**
 * Custom checkbox view used in the settings.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class CheckboxSetting extends LinearLayout {
	private final Context context;
	private View view;
	private TextView pushTitle;
	private TextView pushSummary;
	private CheckBox pushCheckbox;

	private String checkedText;
	private String uncheckedText;

	public CheckboxSetting(final Context context) {
		super(context);
		this.context = context;
		this.inflate();
	}

	public CheckboxSetting(final Context context, final String checkedText, final String uncheckedText) {
		super(context);
		this.context = context;
		this.inflate();
		this.checkedText = checkedText;
		this.uncheckedText = uncheckedText;
	}


	public CheckboxSetting(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.inflate();
		final TypedArray arr = context.obtainStyledAttributes(attrs,
				R.styleable.custom);
		final CharSequence text = arr.getString(R.styleable.custom_text);
		if (text != null) {
			setTitle(text.toString());
		}
		final CharSequence checkedText = arr.getString(R.styleable.custom_checked_text);
		if (checkedText != null) {
			setCheckedText(checkedText.toString());
		}
		final CharSequence uncheckedText = arr.getString(R.styleable.custom_unchecked_text);
		if (uncheckedText != null) {
			setUncheckedText(uncheckedText.toString());
		}
		arr.recycle();
	}

	public void setTitle(final String text) {
		pushTitle.setText(text);
	}
	public String getTitle() {
		return pushTitle.getText().toString();
	}

	public void setSummary(final String text) {
		pushSummary.setText(text);
	}
	public String getSummary() {
		return pushSummary.getText().toString();
	}

	public CheckBox getCheckbox() {
		return pushCheckbox;
	}
	public void setChecked(final boolean checked) {
		pushCheckbox.setChecked(checked);
	}
	public boolean isChecked() {
		return pushCheckbox.isChecked();
	}

	public void setCheckedText(final String checkedText) {
		this.checkedText = checkedText;
	}
	public void setUncheckedText(final String uncheckedText) {
		setSummary(uncheckedText);
		this.uncheckedText = uncheckedText;
	}

	private void inflate() {
		final LayoutInflater layoutInflater = (LayoutInflater) context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.settings_check, this);

		pushTitle = (TextView) view.findViewById(R.id.title);
		pushSummary = (TextView) view.findViewById(R.id.summary);
		pushCheckbox = (CheckBox) view.findViewById(R.id.checkbox);

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				pushCheckbox.toggle();
			}
		});

		pushCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				if (isChecked) {
					setSummary(checkedText);
				} else {
					setSummary(uncheckedText);
				}
			}
		});
	}

}
