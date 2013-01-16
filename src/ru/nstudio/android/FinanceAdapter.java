package ru.nstudio.android;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

public class FinanceAdapter extends BaseAdapter //implements OnItemClickListener 
{
	public static final int MONTH_VIEW_TYPE = 0;
	public static final int YEAR_VIEW_TYPE 	= 1;
	public static final int TYPE_COUNT 		= 2;
	public static final int YEAR_DELIMITER_ID = -1;
	
	private Cursor 						c;
 
	private ArrayList<View>				alView;
	private Context 					context;
	private LayoutInflater 				inflater;

	private String						moneyFormat;
	
	public FinanceAdapter(Context context, LayoutInflater li, Cursor curs)
	{
		this.context = context;
		this.inflater = li;
		this.c = curs;
		this.alView = new ArrayList<View>();
		this.moneyFormat = this.context.getString(R.string.money_format);
		this.parseCursorToView();
	} // ctor1
		
	public int getCount() 
	{
		return this.alView.size();
	} // getCount
	
	public int getViewTypeCount()
	{
		return this.TYPE_COUNT;
	} // getViewTypeCount

	public boolean isEnabled(int position)
	{
		return (this.getItemViewType(position) == this.MONTH_VIEW_TYPE);
	} // isEnabled
	
	public Object getItem(int position) 
	{
		// TODO Auto-generated method stub
		return null;
	} // getItem

	public long getItemId(int position) 
	{
		View v = this.alView.get(position);
		int id = v.getId();
		return id;
	} // getItemId

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if (convertView == null)
		{
			convertView = this.alView.get(position);
		} // if
		
		return convertView;
	} // getView
	
	public int getItemViewType(int position)
	{		
		View v = this.alView.get(position);
		if (v.getId() == this.YEAR_DELIMITER_ID)
			return this.YEAR_VIEW_TYPE;
		else
			return this.MONTH_VIEW_TYPE;
	} // getItemViewType
	
	private void parseCursorToView()
	{
		if(this.c == null)
		{
			String err = this.context.getString(R.string.errNoCursor);
			throw new IllegalArgumentException(err);
		} // if
		
		if(c.moveToFirst())
		{
			int checkYear = -1;
			do
			{
				String monthTitle = new String(c.getString(c.getColumnIndex("title")));
				Double income = c.getDouble(c.getColumnIndex("plus"));
				Double expend = c.getDouble(c.getColumnIndex("minus"));
				Double diff = c.getDouble(c.getColumnIndex("diff"));
					
				Integer curYear = c.getInt(c.getColumnIndex("fyear"));
				Integer curMonth = Integer.parseInt(c.getString(c.getColumnIndex("fmonth")));
				int idItem = (curYear*100) + curMonth;
				
				if (curYear != checkYear)
				{
					checkYear = curYear;
					View vYear = this.inflater.inflate(R.layout.year_delimiter, null);
					TextView tvDelim = (TextView) vYear.findViewById(R.id.tvYearDelimiter); 
					tvDelim.setText(curYear.toString() + " " + this.context.getString(R.string.year_tag));
					vYear.setId(this.YEAR_DELIMITER_ID);
					this.alView.add(vYear);
				} // if
						
				View monthDetails = this.inflater.inflate(R.layout.main_menu_list_item, null);
				
				TextView tvMonthName = (TextView) monthDetails.findViewById(R.id.tvMainMonthTitle);
				tvMonthName.setText(monthTitle);
				
				TextView tvIncome = (TextView) monthDetails.findViewById(R.id.tvMainMonthIncome);
				tvIncome.setText(String.format(this.moneyFormat, income) + " ð");
				
				TextView tvExpend = (TextView) monthDetails.findViewById(R.id.tvMainMonthExpend);
				tvExpend.setText(String.format(this.moneyFormat, expend) + " ð");
				
				TextView tvBalance = (TextView) monthDetails.findViewById(R.id.tvMainMonthBalance);
				tvBalance.setText(String.format(this.moneyFormat, diff) + " ð");
				
				monthDetails.setId(idItem);
				this.alView.add(monthDetails);
			} while(c.moveToNext());
			
			this.c.close();
		} // if cursor not empty
	} // parseCursorToView
}// class FinanceAdapter
