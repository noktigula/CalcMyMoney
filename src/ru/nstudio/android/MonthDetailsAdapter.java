package ru.nstudio.android;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class MonthDetailsAdapter extends BaseAdapter //implements OnItemClickListener
{
	public static final int ITEM_TYPE = 1;
	public static final int TYPE_COUNT = 1;
	
	private Cursor 			c;
	private LayoutInflater 	li;
	private Context 		context;
	private ArrayList<View> alView;
	private String			moneyFormat;
	
	public MonthDetailsAdapter(Context context, LayoutInflater inflater, Cursor c)
	{
		this.context = context;
		this.c = c;
		this.li = inflater;
		this.alView = new ArrayList<View>();
		this.moneyFormat = this.context.getString(R.string.money_format);
		this.parseCursor();
	} // MonthDetailsAdapter
	
	public int getCount() 
	{
		return this.alView.size();
	} // getCount
	
	public int getViewTypeCount()
	{
		return this.TYPE_COUNT;
	} // getViewTypeCount
	
	public int getItemViewType(int position)
	{		
		return this.ITEM_TYPE;
	} // getItemViewType

	public Object getItem(int position) 
	{
		return null;
	} // getItem
	
	public long getItemId(int position) 
	{
		return this.alView.get(position).getId();
	} // getItemId
	
	public boolean isEnabled(int position)
	{
		return (this.getItemViewType(position) == this.ITEM_TYPE);
	} // isEnabled
	
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		convertView = this.alView.get(position);
		return convertView;
	} // getView

	private void parseCursor()
	{
		if (this.c == null)
		{
			String err = this.context.getString(R.string.errNoCursor);
			throw new IllegalArgumentException(err);
		} // if
		
		if (c.moveToFirst())
		{
			do
			{
				boolean isIncome = (c.getInt(c.getColumnIndex(DBHelper.Finance.TYPE)) == 1);
				String explain = new String(c.getString(c.getColumnIndex(DBHelper.Finance.REASON)));
				Double price = c.getDouble(c.getColumnIndex(DBHelper.Finance.PRICE));
				Integer quant = c.getInt(c.getColumnIndex(DBHelper.Finance.QUANTITY));
				int id = c.getInt(c.getColumnIndex(DBHelper.Finance.ID));
				String date = new String(c.getString(c.getColumnIndex(DBHelper.Finance.DATE)));
				String category = new String(c.getString(c.getColumnIndex(DBHelper.Category.NAME)));
				
				String dateDescript = 
					DateParser.format(this.context, date, DateParser.CALCMONEY_FORMAT);
								
				int color = (isIncome) ? Color.GREEN : Color.RED;
							
				View v = this.li.inflate(R.layout.list_item_month_details, null);
								
				TextView tvExplain = (TextView) v.findViewById(R.id.tvFinanceOperationDetails1);
				tvExplain.setText(explain);
				
				TextView tvPrice = (TextView) v.findViewById(R.id.tvShowPrice1);
				tvPrice.setText(String.format(this.moneyFormat, price) + " р");
				tvPrice.setTextColor(color);
						
				TextView tvQuant = (TextView) v.findViewById(R.id.tvShowQuant1);
				tvQuant.setText(quant.toString() + " шт");
				tvQuant.setTextColor(color);
				
				TextView tvBalance = (TextView) v.findViewById(R.id.tvShowTotalCost1);
				tvBalance.setText(String.format(this.moneyFormat, price * quant) + " р");
				tvBalance.setTextColor(color);
				
				TextView tvDate = (TextView) v.findViewById(R.id.tvShowDate1);
				tvDate.setText(dateDescript);

				TextView tvCategory = (TextView) v.findViewById(R.id.tvShowCategory);
				tvCategory.setText(category);
				
				v.setId(id);
				
				this.alView.add(v);
			} while(c.moveToNext());
			
			this.c.close();
		} // if cursor isn`t empty
	} // parseCursor
} // class MonthDetailsAdapter
