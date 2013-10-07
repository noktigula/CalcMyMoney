package ru.nstudio.android;

import java.util.GregorianCalendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity 
implements OnClickListener, android.content.DialogInterface.OnClickListener
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
	
	private SQLiteDatabase 		_db;
	private DBHelper 			_dbHelper;
	
	private int 				_idFinance;
		
	private final int DIALOG_DATE_EXPLAIN = 1;
	private final int RESULT_FIRST_USER_DETAIL = 11;
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.element_add_finance);
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
		
		Intent intent = getIntent();
		_idFinance = intent.getIntExtra("ru.nstudio.android.idFinance", -1);
		if (_idFinance != -1)
		{
			getOperationValues(_idFinance);
		} // if isset _idFinance
		else
		{
			String action = intent.getAction();
			if(action.equalsIgnoreCase("ru.nstudio.android.showDetails"))
			{
				int year = intent.getIntExtra("ru.nstudio.android.year", 2013); // set dynamic year
				int month = intent.getIntExtra("ru.nstudio.android.month", 1);
				_gcDate = new GregorianCalendar(year, month-1, 1);
			} // if
			else
			{
				_gcDate = new GregorianCalendar();
			} // else
			
			_rbIncome.setChecked(true);
			
			displayDate();
		} // else


		fillSpinnerWithCategories();
		_btnAddCategory.setOnClickListener( this );
	} // onCreate

	@TargetApi( Build.VERSION_CODES.HONEYCOMB )
	private void fillSpinnerWithCategories()
	{
		initDatabase();

		String query = "SELECT " + DBHelper.Category.ID + " AS _id, " + DBHelper.Category.NAME + " FROM " + DBHelper.Category.TABLE_NAME;

		Cursor c = _db.rawQuery(query, null);
		//_db.close();
		//CategoryAdapter categoryAdapter = new CategoryAdapter( this, getLayoutInflater(), c );
		SimpleCursorAdapter categoryAdapter =
				new SimpleCursorAdapter( this, R.layout.spinner_item_category, c,
										 new String[]{ DBHelper.Category.NAME },
										 new int[]{R.id.tvCategory},
										 SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );

		_spinner.setAdapter( categoryAdapter );
	}

	private void initDatabase()
	{
		if (_dbHelper == null)
			_dbHelper = new DBHelper(this, DBHelper.CURRENT_DATABASE_VERSION);
		
		if (_db == null)
			_db = _dbHelper.getWritableDatabase();
	} // initDataBase
	
	private void getOperationValues(int idFinance)
	{
		initDatabase();
		
		String query = new String ("SELECT * FROM " + DBHelper.Finance.TABLE_NAME +
								   " WHERE " + DBHelper.Finance.ID + " = ?");
		Cursor c = _db.rawQuery(query, new String[]{Integer.toString(idFinance)});
		
		if (c.moveToFirst())
		{
			_etExplain.setText(c.getString(c.getColumnIndex( DBHelper.Finance.REASON ) ));
			
			Double price = c.getDouble(c.getColumnIndex(DBHelper.Finance.PRICE));
			_etPrice.setText(String.format(getString(R.string.money_format), price));
			
			int quant = c.getInt(c.getColumnIndex(DBHelper.Finance.QUANTITY));
			_etQuantity.setText(Integer.toString(quant));
			
			String strDate = c.getString(c.getColumnIndex(DBHelper.Finance.DATE));
			
			displayDate(strDate);		
			
			boolean type = (c.getInt(c.getColumnIndex(DBHelper.Finance.TYPE)) == 1);
			_rbIncome.setChecked(type);
			_rbExpend.setChecked(!type);
		} // if

		fillSpinnerWithCategories();
		
		c.close();
	} // getOperationValues
	
	private void displayDate(String strDate)
	{
		_gcDate = DateParser.parseStringToDate(this, strDate);
		String dateDesc = DateParser.format(this, strDate, DateParser.CALCMONEY_FORMAT);
		_tvDateExplain.setText(dateDesc);
	} // displayDate
	
	private void displayDate()
	{
		String dateDesc = DateParser.format(this, _gcDate, DateParser.CALCMONEY_FORMAT);
		_tvDateExplain.setText(dateDesc);
	} // displayDate
	
	@SuppressWarnings("deprecation")
	public void onDateExplainClick (View v)
	{
		showDialog(DIALOG_DATE_EXPLAIN);
	} // onDateExplainClick	
	
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
		} // id == DIALOG_DATE_EXPLAIN
		return super.onCreateDialog(id);
	} // onCreateDialog
	
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
	}; // new OnDateSetListener*/

	public void onClick(View v) 
	{
		if (v.getId() == R.id.btnExplainOK)
		{
			if (_etExplain.getText().toString().isEmpty() ||
				_etQuantity.getText().toString().isEmpty() ||
				_etPrice.getText().toString().isEmpty())
			{
				Toast.makeText(this, R.string.errEmptyField, 10000).show();
				return;
			} // if
			
			initDatabase();
			
			ContentValues cv = new ContentValues();

            String quant = _etQuantity.getText().toString();
            String price = _etPrice.getText().toString();

            if(quant.contains(","))
            {
                quant = quant.replace(",", ".");
            }   // if quant contains ,

            if(price.contains(","))
            {
                price = price.replace(",", ".");
            }   // if quant contains ,
			
			cv.put("reason", _etExplain.getText().toString());
			cv.put("quantity", Double.parseDouble(quant));
			cv.put("price", Double.parseDouble(price));
			cv.put("type", _rbIncome.isChecked());
			cv.put("financeDate", DateParser.format(this, _gcDate, DateParser.SQLITE_FORMAT));
								
			if (_idFinance == -1)
			{
				_db.insert("Finance", null, cv);
			} // if adding new
			else
			{
				_db.update("Finance",
						cv,
						"_idFinance = ?",
						new String[]{Integer.toString(_idFinance)});
			} // else
			_db.close();
			Intent intent = new Intent();
			intent.putExtra("ru.nstudio.android.success", true);
			setResult(RESULT_FIRST_USER_DETAIL, intent);
			finish();
		} // if
		else if ( v.getId() == R.id.btnAddCategory )
		{
			showDialogAddCategory();
		}
	} //onClick

	private void showDialogAddCategory()
	{

	}

	public void onClick(DialogInterface dialog, int which) 
	{
		if (which == DialogInterface.BUTTON_POSITIVE)
		{
			
		} // if
	} // onClick(Dialog)
} // AddNewDetailsActivity
