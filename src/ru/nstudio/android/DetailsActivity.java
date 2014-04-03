package ru.nstudio.android;

import java.util.GregorianCalendar;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.RadioGroup;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ru.nstudio.android.Storage.MoneyContract;
import ru.nstudio.android.dialogs.AddCategoryDialog;

public class DetailsActivity extends FragmentActivity
implements OnClickListener,  IDialogListener
{
	private EditText 			_etExplain;
	private EditText 			_etPrice;
	private EditText 			_etQuantity;

	private RadioGroup 			_rgIncomeExpend;
	private RadioButton 		_rbIncome;
	private RadioButton 		_rbExpend;

	private Spinner				_spinner;
	private Button				_btnAddCategory;
	
	private TextView 			_tvDateExplain;
	
	private Button 				_btnExplainOk;
	
	private GregorianCalendar 	_gcDate;
	
	private long 				_idFinance;
		
	private final int DIALOG_DATE_EXPLAIN = 1;
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_finance_activity);

		_etExplain = (EditText) findViewById(R.id.etExplain);
		_etPrice = (EditText) findViewById(R.id.etPrice);
		_etQuantity = (EditText) findViewById(R.id.etQuantity);
		
		_rgIncomeExpend = (RadioGroup) findViewById(R.id.rgIncomeExpend);
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
		_idFinance = intent.getLongExtra("ru.nstudio.android.idFinance", -1);
		if (_idFinance != -1)
		{
			getOperationValues(_idFinance);
		}
		else
		{
			String action = intent.getAction();
			if(action.equalsIgnoreCase("ru.nstudio.android.showDetails"))
			{
				int year = intent.getIntExtra("ru.nstudio.android._year", 2013 ); // TODO set current date
				int month = intent.getIntExtra("ru.nstudio.android._month", 1);
				_gcDate = new GregorianCalendar(year, month-1, 1);
			}
			else
			{
				_gcDate = new GregorianCalendar();
			}
			
			_rbIncome.setChecked(true);
			
			displayDate();
		}

		_btnAddCategory.setOnClickListener( this );
	}

	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	private void fillSpinnerWithCategories()
	{
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query( MoneyContract.Category.CONTENT_URI, null, null, null, null );

		SimpleCursorAdapter categoryAdapter =
				new SimpleCursorAdapter( this, android.R.layout.simple_spinner_item, c,
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
			_etPrice.setText(String.format(getString(R.string.money_format), price));
			
			int quant = c.getInt(c.getColumnIndex(MoneyContract.Finance.QUANTITY));
			_etQuantity.setText(Integer.toString(quant));
			
			String strDate = c.getString(c.getColumnIndex(MoneyContract.Finance.DATE));
			
			displayDate(strDate);		
			
			boolean type = (c.getInt(c.getColumnIndex(MoneyContract.Finance.TYPE)) == 1);
			_rbIncome.setChecked(type);
			_rbExpend.setChecked(!type);

			int selection = c.getInt( c.getColumnIndex( MoneyContract.Finance.CATEGORY ) )-1;
			_spinner.setSelection( selection );
		} // if
		
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
				 TextUtils.isEmpty( _etQuantity.getText().toString() ) ||
				 TextUtils.isEmpty( _etPrice.getText().toString() ))
			{
				Toast.makeText( this, R.string.errEmptyField, 10000 ).show();
				return;
			}
			
			ContentValues cv = new ContentValues();

            String quant = _etQuantity.getText().toString();
            String price = _etPrice.getText().toString();
			long categoryID = _spinner.getSelectedItemId();

            if(quant.contains(","))
            {
                quant = quant.replace(",", ".");
            }
            if(price.contains(","))
            {
                price = price.replace(",", ".");
            }

			cv.put( MoneyContract.Finance.REASON, _etExplain.getText().toString() );
			cv.put( MoneyContract.Finance.QUANTITY, Double.parseDouble(quant) );
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
		AddCategoryDialog dialog = new AddCategoryDialog();
		dialog.show( getSupportFragmentManager(), "add_category" );
	}

//	public void onClick(DialogInterface dialog, int which)
//	{
//		if (which == DialogInterface.BUTTON_POSITIVE)
//		{
//
//		}
//	}

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
	}

	@Override
	public void onDialogNegativeClick( DialogFragment dialog )
	{
		return;
	}
}
