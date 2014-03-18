package ru.nstudio.android;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.nstudio.android.Storage.MoneyContract;

/**
 * Created with IntelliJ IDEA.
 * User: noktigula
 * Date: 12.07.13
 * Time: 2:50
 * To change this template use File | Settings | File Templates.
 */
public class CategoryAdapter extends BaseAdapter
{
	private final int ITEM_TYPE = 1;
	private final int TYPE_COUNT = 1;

	private Context 		_context;
	private LayoutInflater 	_inflater;
	private Cursor 			_cursor;
	private ArrayList<View> _alView;

	public CategoryAdapter(Context context, LayoutInflater inflater, Cursor cursor)
	{
		_context = context;
		_inflater = inflater;
		_cursor = cursor;
		_alView = new ArrayList<View>();

		parseCursor();
	}

	@Override
	public int getCount()
	{
		return _alView.size();  //To change body of implemented methods use File | Settings | File Templates.
	}

	public int getTypeCount()
	{
		return TYPE_COUNT;
	}

	public int getItemType()
	{
		return ITEM_TYPE;
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return _alView.get(position).getId();  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		convertView = _alView.get(position);
		return convertView;
	}

	private void parseCursor()
	{
		if (_context == null)
		{
			String err = _context.getString(R.string.errNoCursor);
			throw new IllegalArgumentException(err);
		} // if

		if(_cursor.moveToFirst())
		{
			do
			{
				String category = _cursor.getString( _cursor.getColumnIndex( MoneyContract.Category.NAME ) );

				_alView.add( getViewWithText( category ) );
			} while(_cursor.moveToNext());
		}

		//_alView.add( getViewWithText( _context.getString( R.string.new_category ) ) );
	}

	private View getViewWithText(String text)
	{
		View v = _inflater.inflate( R.layout.spinner_item_category, null );
		TextView tv = (TextView) v.findViewById( R.id.tvCategory );
		tv.setText( text );

		return v;
	}
}
