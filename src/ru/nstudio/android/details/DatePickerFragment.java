package ru.nstudio.android.details;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import ru.nstudio.android.R;

/**
 * Created by noktigula on 07.07.14.
 */
public class DatePickerFragment extends DialogFragment
{
	private static final String KEY_YEAR = "year";
	private static final String KEY_MONTH = "month";
	private static final String KEY_DAY = "day";

	public static DatePickerFragment getInstance(int year, int month, int day)
	{
		Bundle args = new Bundle();
		args.putInt( KEY_YEAR, year );
		args.putInt( KEY_MONTH, month );
		args.putInt( KEY_DAY, day );
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setArguments( args );
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Bundle args = getArguments();
		int year = args.getInt(KEY_YEAR);
		int month = args.getInt( KEY_MONTH );
		int day = args.getInt( KEY_DAY );
		DatePickerDialog.OnDateSetListener listener = ((DetailsActivity)getActivity()).getDateSetListener();
		DatePickerDialog dialog = new DatePickerDialog( getActivity(), listener, year, month, day);
		dialog.setTitle( R.string.hintSelectDate);
		return dialog;
	}
}
