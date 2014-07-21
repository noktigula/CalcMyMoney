package ru.nstudio.android.details;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import ru.nstudio.android.R;

public class DetailsActivity extends FragmentActivity

{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView( R.layout.add_finance_activity);
	}

	public DatePickerDialog.OnDateSetListener getDateSetListener()
	{
		DetailsFragment fragment = (DetailsFragment)getSupportFragmentManager().findFragmentById( R.id.fragmentDetails );
		if (fragment == null)
		{
			throw new RuntimeException( "Unexpected fragment" );
		}
		return fragment.getDateSetCallback();
	}
}
