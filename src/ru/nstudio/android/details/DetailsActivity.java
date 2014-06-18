package ru.nstudio.android.details;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ru.nstudio.android.DateParser;
import ru.nstudio.android.IDialogListener;
import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;
import ru.nstudio.android.dialogs.AddCategoryDialog;

public class DetailsActivity extends FragmentActivity
implements OnClickListener, IDialogListener
{
	private EditText 			_etExplain;
	private EditText 			_etPrice;

	private RadioButton 		_rbIncome;
	private RadioButton 		_rbExpend;

	private Spinner				_spinner;
	private Button				_btnAddCategory;
	
	private TextView 			_tvDateExplain;
	
	private Button 				_btnExplainOk;
	
	private GregorianCalendar 	_gcDate;
	
	private long 				_idFinance;
		
	private final int DIALOG_DATE_EXPLAIN = 1;
	private static final int INVALID_VALUE = -1;
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView( R.layout.add_finance_activity);

		_etExplain = (EditText) findViewById(R.id.etExplain);
		_etPrice = (EditText) findViewById(R.id.etPrice);

		_rbIncome = (RadioButton) findViewById(R.id.rbIncome);
		_rbExpend = (RadioButton) findViewById(R.id.rbExpend);
	
		_tvDateExplain = (TextView) findViewById(R.id.tvFinanceDate);
		_gcDate = new GregorianCalendar();
		
		_btnExplainOk = (Button) findViewById(R.id.btnExplainOK);
		_btnExplainOk.setOnClickListener(this);

		_spinner = (Spinner) findViewById(R.id.spinnerCategory);
		_btnAddCategory = (Button) findViewById( R.id.btnAddCategory );

		fillSpinnerWithCategories();

		Intent intent = getIntent();
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
                ||(currentDate.get(Calendar.MONTH) == month-1 && currentDate.get(Calendar.YEAR) == year) )
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
	}

	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	private void fillSpinnerWithCategories()
	{
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query( MoneyContract.Category.CONTENT_URI, null, null, null, MoneyContract.Category.DEFAULT_SORT_ORDER );

		android.widget.SimpleCursorAdapter categoryAdapter =
				new android.widget.SimpleCursorAdapter( this, android.R.layout.simple_spinner_item, c,
										 new String[]{ MoneyContract.Category.NAME },
										 new int[]{ android.R.id.text1 },
										 SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );

		categoryAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		_spinner.setAdapter( categoryAdapter );
	}
	
	private void getOperationValues(long idFinance)
	{
		ContentResolver cr = getContentResolver();
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
		Cursor c = getContentResolver().query( MoneyContract.Category.CONTENT_URI, null, null, null, MoneyContract.Category.DEFAULT_SORT_ORDER );
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

	//TODO - replace this shit to fragments
	@SuppressWarnings("deprecation")
	public void onDateExplainClick (View v)
	{
		showDialog(DIALOG_DATE_EXPLAIN);
	} // onDateExplainClick	

	//TODO - replace this shit to fragments
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id)
	{
		if (id == DIALOG_DATE_EXPLAIN)
		{
			DatePickerDialog dpdExplain = new DatePickerDialog(this, dpdExplainCallback, 
					_gcDate.get(GregorianCalendar.YEAR),
					_gcDate.get(GregorianCalendar.MONTH),
					_gcDate.get(GregorianCalendar.DAY_OF_MONTH));
			
			dpdExplain.setTitle(R.string.hintSelectDate);
			return dpdExplain;
		}
		return super.onCreateDialog(id);
	}
	//TODO - replace this shit to fragments
	OnDateSetListener dpdExplainCallback = new OnDateSetListener() 
	{
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) 
		{
			int resYear = year;
			int resMonthOfYear = monthOfYear;
			int resDay = dayOfMonth;
			_gcDate = new GregorianCalendar(resYear, resMonthOfYear, resDay);
			displayDate();
		} // onDateSet
	};

	public void onClick(View v) 
	{
		if ( v.getId() == R.id.btnExplainOK )
		{
			if ( TextUtils.isEmpty( _etExplain.getText().toString() ) ||
				 TextUtils.isEmpty( _etPrice.getText().toString() ))
			{
				Toast.makeText( this, R.string.errEmptyField, Toast.LENGTH_LONG ).show();
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
			cv.put( MoneyContract.Finance.DATE, DateParser.format(_gcDate, DateParser.SQLITE_FORMAT ) );
			cv.put( MoneyContract.Finance.CATEGORY, categoryID );

			ContentResolver cr = getContentResolver();
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

			finish();
		}
		else if ( v.getId() == R.id.btnAddCategory )
		{
			showDialogAddCategory();
		}
	}

	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	private void showDialogAddCategory()
	{
		AddCategoryDialog dialog = AddCategoryDialog.getInstance( null, -1L );
		dialog.show( getSupportFragmentManager(), AddCategoryDialog.class.toString() );
	}

	@Override
	public void onDialogPositiveClick( DialogFragment dialog )
	{
		AddCategoryDialog addCategoryDialog = (AddCategoryDialog)dialog;
		String category = addCategoryDialog.getCategory();

		ContentValues values = new ContentValues(  );
		values.put( MoneyContract.Category.NAME, category );

		ContentResolver cr = getContentResolver();
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
