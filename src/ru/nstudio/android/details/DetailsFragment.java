package ru.nstudio.android.details;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.nstudio.android.DateParser;
import ru.nstudio.android.IDialogListener;
import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;
import ru.nstudio.android.dialogs.AddCategoryDialog;

/**
 * Created by noktigula on 05.07.14.
 */
public class DetailsFragment extends Fragment implements View.OnClickListener, IDialogListener
{
	private EditText _etExplain;
	private EditText _etPrice;

	private RadioButton _rbIncome;
	private RadioButton _rbExpend;

	private Spinner _spinner;
	private Button _btnAddCategory;

	private TextView _tvDateExplain;

	private Button _btnExplainOk;

	private GregorianCalendar _gcDate;

	private long _idFinance;

	private final int DIALOG_DATE_EXPLAIN = 1;
	private static final int INVALID_VALUE = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate( R.layout.fragment_new_operation, container, false );
		_etExplain = (EditText) view.findViewById( R.id.etExplain );
		_etPrice = (EditText) view.findViewById( R.id.etPrice );

		_rbIncome = (RadioButton) view.findViewById( R.id.rbIncome );
		_rbExpend = (RadioButton) view.findViewById( R.id.rbExpend );

		_tvDateExplain = (TextView) view.findViewById( R.id.tvFinanceDate );
		_gcDate = new GregorianCalendar();

		_btnExplainOk = (Button) view.findViewById( R.id.btnExplainOK );
		_btnExplainOk.setOnClickListener(this);

		_spinner = (Spinner) view.findViewById( R.id.spinnerCategory );
		_btnAddCategory = (Button) view.findViewById( R.id.btnAddCategory );

		fillSpinnerWithCategories();

		Intent intent = getActivity().getIntent();
		_idFinance = intent.getLongExtra(getString( R.string.key_finance_id ), INVALID_VALUE );
		if (_idFinance != INVALID_VALUE)
		{
			getOperationValues(_idFinance);
		}
		else
		{
			GregorianCalendar currentDate = new GregorianCalendar();
			int year = intent.getIntExtra( getString( R.string.key_year ), INVALID_VALUE );
			int month = intent.getIntExtra( getString( R.string.key_month ), INVALID_VALUE );
			if( ( year == INVALID_VALUE || month == INVALID_VALUE )
					||(currentDate.get( Calendar.MONTH) == month-1 && currentDate.get(Calendar.YEAR) == year) )
			{
				_gcDate = currentDate;
			}
			else
			{
				_gcDate = new GregorianCalendar( year, month-1, 1 );
			}

			_rbExpend.setChecked( true );
			displayDate();
		}


		_btnAddCategory.setOnClickListener( this );
		_tvDateExplain.setOnClickListener( this );

		return view;
	}

	@Override
	public void onClick( View v )
	{
		if ( v.getId() == R.id.btnExplainOK )
		{
			if ( TextUtils.isEmpty( _etExplain.getText().toString() ) ||
					TextUtils.isEmpty( _etPrice.getText().toString() ))
			{
				Toast.makeText( getActivity(), R.string.errEmptyField, Toast.LENGTH_LONG ).show();
				return;
			}

			ContentValues cv = new ContentValues();

			String price = _etPrice.getText().toString();
			long categoryID = _spinner.getSelectedItemId();

			if(price.contains(","))
			{
				price = price.replace(",", ".");
			}

			cv.put( MoneyContract.Finance.REASON, _etExplain.getText().toString() );
			cv.put( MoneyContract.Finance.QUANTITY, 1 );
			cv.put( MoneyContract.Finance.PRICE, Double.parseDouble(price) );
			cv.put( MoneyContract.Finance.TYPE, _rbIncome.isChecked() );
			cv.put( MoneyContract.Finance.DATE, DateParser.format( _gcDate, DateParser.SQLITE_FORMAT ) );
			cv.put( MoneyContract.Finance.CATEGORY, categoryID );

			ContentResolver cr = getActivity().getContentResolver();
			if (_idFinance == -1)
			{
				cr.insert( MoneyContract.Finance.CONTENT_URI, cv );
			}
			else
			{
				Uri updateUri = Uri.withAppendedPath( MoneyContract.Finance.CONTENT_URI, Long.toString( _idFinance) );
				cr.update( updateUri,
						cv, null, null );
			}

			getActivity().finish();
		}
		else if ( v.getId() == R.id.btnAddCategory )
		{
			showDialogAddCategory();
		}
		else if ( v.getId() == _tvDateExplain.getId() )
		{
			onDateExplainClick( v );
		}
	}

	private void fillSpinnerWithCategories()
	{
		ContentResolver cr = getActivity().getContentResolver();
		Cursor c = cr.query( MoneyContract.Category.CONTENT_URI, null, null, null, MoneyContract.Category.DEFAULT_SORT_ORDER );

		SimpleCursorAdapter categoryAdapter =
				new SimpleCursorAdapter( getActivity(), android.R.layout.simple_spinner_item, c,
						new String[]{ MoneyContract.Category.NAME },
						new int[]{ android.R.id.text1 },
						SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );

		categoryAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		_spinner.setAdapter( categoryAdapter );
	}

	private void getOperationValues(long idFinance)
	{
		ContentResolver cr = getActivity().getContentResolver();
		Uri itemUri = Uri.withAppendedPath( MoneyContract.Finance.CONTENT_URI, Long.toString( idFinance ));
		Cursor c = cr.query( itemUri, null, null, null, null );

		if (c.moveToFirst())
		{
			_etExplain.setText(c.getString(c.getColumnIndex( MoneyContract.Finance.REASON ) ));

			Double price = c.getDouble(c.getColumnIndex(MoneyContract.Finance.PRICE));
			int quant = c.getInt(c.getColumnIndex(MoneyContract.Finance.QUANTITY));
			String cost = String.format(getString(R.string.money_format), price*quant );
			_etPrice.setText(cost.trim());

			String strDate = c.getString(c.getColumnIndex(MoneyContract.Finance.DATE));

			displayDate(strDate);

			boolean type = (c.getInt(c.getColumnIndex(MoneyContract.Finance.TYPE)) == 1);
			_rbIncome.setChecked(type);
			_rbExpend.setChecked(!type);

			int category = c.getInt( c.getColumnIndex( MoneyContract.Finance.CATEGORY ) );
			selectItemInSpinner(category);
		}

		c.close();
	}

	private void selectItemInSpinner(int category)
	{
		Cursor c = getActivity().getContentResolver().query( MoneyContract.Category.CONTENT_URI, null, null, null, MoneyContract.Category.DEFAULT_SORT_ORDER );
		int position = 0;
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			if( category == c.getInt( c.getColumnIndex( MoneyContract.Category._ID ) ))
			{
				break;
			}
			++position;
		}

		_spinner.setSelection( position );
		c.close();
	}

	private void displayDate(String strDate)
	{
		_gcDate = DateParser.parseStringToDate( strDate );
		String dateDesc = DateParser.format( strDate, DateParser.CALCMONEY_FORMAT );
		_tvDateExplain.setText( dateDesc );
	}

	private void displayDate()
	{
		String dateDesc = DateParser.format( _gcDate, DateParser.CALCMONEY_FORMAT );
		_tvDateExplain.setText(dateDesc);
	}

	public void onDateExplainClick (View v)
	{
		int year = _gcDate.get(Calendar.YEAR);
		int month = _gcDate.get(Calendar.MONTH);
		int day = _gcDate.get(Calendar.DAY_OF_MONTH);
		DialogFragment fragment = DatePickerFragment.getInstance(year, month, day);
		fragment.show( getActivity().getSupportFragmentManager(), "date_picker_dialog" );
	}

	DatePickerDialog.OnDateSetListener dpdExplainCallback = new DatePickerDialog.OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int year, int monthOfYear,
							  int dayOfMonth)
		{
			int resYear = year;
			int resMonthOfYear = monthOfYear;
			int resDay = dayOfMonth;
			_gcDate = new GregorianCalendar(resYear, resMonthOfYear, resDay);
			displayDate();
		}
	};

	public DatePickerDialog.OnDateSetListener getDateSetCallback()
	{
		return dpdExplainCallback;
	}

	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	private void showDialogAddCategory()
	{
		AddCategoryDialog dialog = AddCategoryDialog.getInstance( null, -1L );
		dialog.show( getActivity().getSupportFragmentManager(), AddCategoryDialog.class.toString() );
	}

	@Override
	public void onDialogPositiveClick( DialogFragment dialog )
	{
		AddCategoryDialog addCategoryDialog = (AddCategoryDialog)dialog;
		String category = addCategoryDialog.getCategory();

		ContentValues values = new ContentValues(  );
		values.put( MoneyContract.Category.NAME, category );

		ContentResolver cr = getActivity().getContentResolver();
		cr.insert( MoneyContract.Category.CONTENT_URI, values );

		fillSpinnerWithCategories();
		_spinner.setSelection( _spinner.getAdapter().getCount()-1 );
	}

	@Override
	public void onDialogNegativeClick( DialogFragment dialog )
	{
		return;
	}
}
