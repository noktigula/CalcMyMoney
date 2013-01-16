package ru.nstudio.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity 
implements OnClickListener, android.content.DialogInterface.OnClickListener
{
	private EditText 			etExplain;
	private EditText 			etPrice;
	private EditText 			etQuantity;

	private RadioGroup 			rgIncomeExpend;
	private RadioButton 		rbIncome;
	private RadioButton 		rbExpend;
	
	private TextView			tvDateExplain;
	
	private Button 				btnExplainOk;
	
	private GregorianCalendar 	gcDate;
	
	private SQLiteDatabase		db;
	private DBHelper			dbHelper;
	
	private int					idFinance;
		
	private final int DIALOG_DATE_EXPLAIN = 1;
	private final int RESULT_FIRST_USER_DETAIL = 11;
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.element_add_finance);
		setContentView(R.layout.add_finance_activity);
		etExplain = (EditText) findViewById(R.id.etExplain);
		etPrice = (EditText) findViewById(R.id.etPrice);
		etQuantity = (EditText) findViewById(R.id.etQuantity);
		
		rgIncomeExpend = (RadioGroup) findViewById(R.id.rgIncomeExpend);
		rbIncome = (RadioButton) findViewById(R.id.rbIncome);
		rbExpend = (RadioButton) findViewById(R.id.rbExpend);
	
		tvDateExplain = (TextView) findViewById(R.id.tvFinanceDate);		
		this.gcDate = new GregorianCalendar();
		
		btnExplainOk = (Button) findViewById(R.id.btnExplainOK);
		btnExplainOk.setOnClickListener(this);
		
		Intent intent = this.getIntent();
		this.idFinance = intent.getIntExtra("ru.nstudio.android.idFinance", -1);
		if (this.idFinance != -1)
		{
			this.getOperationValues(idFinance);
		} // if isset idFinance
		else
		{
			String action = intent.getAction();
			if(action.equalsIgnoreCase("ru.nstudio.android.showDetails"))
			{
				int year = intent.getIntExtra("ru.nstudio.android.year", 2012);
				int month = intent.getIntExtra("ru.nstudio.android.month", 1);
				this.gcDate = new GregorianCalendar(year, month-1, 1);
			} // if
			else
			{
				this.gcDate = new GregorianCalendar();
			} // else
			
			this.rbIncome.setChecked(true);
			
			displayDate();
		} // else
	} // onCreate
	
	private void initDatabase()
	{
		if (this.dbHelper == null)
			this.dbHelper = new DBHelper(this, DBHelper.CURRENT_DATABASE_VERSION);
		
		if (this.db == null)
			this.db = this.dbHelper.getWritableDatabase();
	} // initDataBase
	
	private void getOperationValues(int idFinance)
	{
		initDatabase();
		
		String query = new String ("SELECT * FROM Finance WHERE idFinance = ?");
		Cursor c = db.rawQuery(query, new String[]{Integer.toString(idFinance)});
		
		if (c.moveToFirst())
		{
			//@preorder - только одна строка(выборка по первичному ключу)
			this.etExplain.setText(c.getString(c.getColumnIndex("reason")));
			
			Double price = c.getDouble(c.getColumnIndex("price"));
			this.etPrice.setText(String.format(this.getString(R.string.money_format), price));
			
			int quant = c.getInt(c.getColumnIndex("quantity"));
			this.etQuantity.setText(Integer.toString(quant));
			
			String strDate = c.getString(c.getColumnIndex("financeDate"));
			
			this.displayDate(strDate);		
			
			boolean type = (c.getInt(c.getColumnIndex("type")) == 1);
			this.rbIncome.setChecked(type);
			this.rbExpend.setChecked(!type);
		} // if
		
		c.close();
	} // getOperationValues
	
	private void displayDate(String strDate)
	{
		this.gcDate = DateParser.parseStringToDate(this, strDate);
		String dateDesc = DateParser.format(this, strDate, DateParser.CALCMONEY_FORMAT);
		this.tvDateExplain.setText(dateDesc);
	} // displayDate
	
	private void displayDate()
	{
		String dateDesc = DateParser.format(this, this.gcDate, DateParser.CALCMONEY_FORMAT);
		this.tvDateExplain.setText(dateDesc);
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
					this.gcDate.get(GregorianCalendar.YEAR), 
					this.gcDate.get(GregorianCalendar.MONTH), 
					this.gcDate.get(GregorianCalendar.DAY_OF_MONTH));
			
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
			gcDate = new GregorianCalendar(resYear, resMonthOfYear, resDay);
			displayDate();
		} // onDateSet
	}; // new OnDateSetListener*/

	public void onClick(View v) 
	{
		if (v.getId() == R.id.btnExplainOK)
		{
			if (etExplain.getText().toString().isEmpty() ||
				etQuantity.getText().toString().isEmpty() ||
				etPrice.getText().toString().isEmpty())
			{
				Toast.makeText(this, R.string.errEmptyField, 10000).show();
				return;
			} // if
			
			this.initDatabase();
			
			ContentValues cv = new ContentValues();
			
			cv.put("reason", etExplain.getText().toString());
			cv.put("quantity", Double.parseDouble(etQuantity.getText().toString()));
			cv.put("price", Double.parseDouble(etPrice.getText().toString()));
			cv.put("type", rbIncome.isChecked());
			cv.put("financeDate", DateParser.format(this, this.gcDate, DateParser.SQLITE_FORMAT));
								
			if (this.idFinance == -1)
			{
				this.db.insert("Finance", null, cv);
			} // if adding new
			else
			{
				this.db.update("Finance", 
							   cv, 
							   "idFinance = ?", 
							   new String[]{Integer.toString(this.idFinance)});
			} // else
			this.db.close();
			Intent intent = new Intent();
			intent.putExtra("ru.nstudio.android.success", true);
			setResult(RESULT_FIRST_USER_DETAIL, intent);
			finish();
		} // if
		
	} //onClick

	public void onClick(DialogInterface dialog, int which) 
	{
		if (which == DialogInterface.BUTTON_POSITIVE)
		{
			
		} // if
	} // onClick(Dialog)
} // AddNewDetailsActivity
