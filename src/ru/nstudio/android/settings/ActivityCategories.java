package ru.nstudio.android.settings;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;

import ru.nstudio.android.ContextMenuInitializer;
import ru.nstudio.android.IDialogListener;
import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;
import ru.nstudio.android.dialogs.AddCategoryDialog;


public class ActivityCategories extends ActionBarActivity implements LoaderManager.LoaderCallbacks, IDialogListener
{
	private ListView _lv;
	private SimpleCursorAdapter _adapter;
	private int LOADER_ID = 3;
	private ActionMode _actionMode;
	private ActionMode.Callback _actionModeCallback;
	private int _selectedPos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

		_lv = (ListView)findViewById( R.id.lvAllCategories );
		Cursor c = getContentResolver().query( MoneyContract.Category.CONTENT_URI, null, null, null, null );

		String[] cols = new String[] {MoneyContract.Category.NAME};
		int[] views = new int[] {android.R.id.text1};
		_adapter = new SimpleCursorAdapter( this, android.R.layout.simple_list_item_1, c, cols, views, 0 );
		_lv.setAdapter( _adapter );

		_actionModeCallback = new MyActionModeCallback();
		_lv.setLongClickable( true );
		_lv.setChoiceMode( ListView.CHOICE_MODE_SINGLE );
		_lv.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick( AdapterView<?> adapterView, View view, int position, long id )
			{
				//Log.d( getString( R.string.TAG ), "Long click on " + Long.toString( id ) );
				if( _actionMode != null )
				{
					return false;
				}
				_actionMode = startSupportActionMode( _actionModeCallback );
				_selectedPos = position;
				view.setSelected( true );
				view.setBackgroundResource( android.R.color.holo_blue_light );
				_lv.setItemChecked( position, true );
				return true;
			}
		});

		getSupportLoaderManager().initLoader( LOADER_ID, null, this );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_categories, menu);
        return true;
    }

	private void showEditCategoryDialog( String category, Long itemId )
	{
		AddCategoryDialog dialog = AddCategoryDialog.getInstance( category, itemId );
		dialog.show( getSupportFragmentManager(), "add_category" );
	}

	private void removeItem( long itemId )
	{
		ContentResolver cr = getContentResolver();
		Uri uri = Uri.withAppendedPath( MoneyContract.Category.CONTENT_URI, Long.toString( itemId ) );
		cr.delete( uri, null, null );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem menuItem )
	{
		switch( menuItem.getItemId() )
		{
			case R.id.menuNewItem:
			{
				showEditCategoryDialog( null, -1L );
				return true;
			}
			default: return super.onOptionsItemSelected( menuItem );
		}
	}

//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
//	{
//		ContextMenuInitializer initializer = new ContextMenuInitializer(menu, v, menuInfo);
//		menu = initializer.getMenu();
//	}

	@Override
	public Loader onCreateLoader( int i, Bundle bundle )
	{
		return new CursorLoader( this, MoneyContract.Category.CONTENT_URI, null, null, null, null );
	}

	@Override
	public void onLoadFinished( Loader loader, Object o )
	{
		_adapter.swapCursor( (Cursor)o );
	}

	@Override
	public void onLoaderReset( Loader loader )
	{
		_adapter.swapCursor( null );
	}

	@Override
	public void onDialogPositiveClick( DialogFragment dialog )
	{
		AddCategoryDialog addCategoryDialog = (AddCategoryDialog)dialog;
		String category = addCategoryDialog.getCategory();

		ContentValues values = new ContentValues(  );
		values.put( MoneyContract.Category.NAME, category );

		ContentResolver cr = getContentResolver();
		if( !addCategoryDialog.isUpdate() )
		{
			cr.insert( MoneyContract.Category.CONTENT_URI, values );
		}
		else
		{
			Uri uri = Uri.withAppendedPath( MoneyContract.Category.CONTENT_URI, Long.toString( addCategoryDialog.getItemId() ) );
			cr.update( uri, values, null, null );
		}
	}

	@Override
	public void onDialogNegativeClick( DialogFragment dialog )
	{
		return;
	}

	private class MyActionModeCallback implements ActionMode.Callback
	{
		@Override
		public boolean onCreateActionMode( ActionMode actionMode, Menu menu )
		{
			MenuInflater menuInflater = actionMode.getMenuInflater();
			menuInflater.inflate( R.menu.menu_context, menu );
			return true;
		}

		@Override
		public boolean onPrepareActionMode( ActionMode actionMode, Menu menu )
		{
			return false;
		}

		@Override
		public boolean onActionItemClicked( ActionMode actionMode, MenuItem menuItem )
		{
			int itemPos = ActivityCategories.this._selectedPos;
			long itemId = _lv.getAdapter().getItemId( itemPos );

			ContentResolver cr = getContentResolver();
			Uri uri = Uri.withAppendedPath( MoneyContract.Category.CONTENT_URI, Long.toString( itemId ) );
			Cursor c = cr.query( uri, null, null, null, null );

			if( !c.moveToFirst() )
			{
				throw new RuntimeException("Can't load category data");
			}
			String cat = c.getString( c.getColumnIndex( MoneyContract.Category.NAME ) );

			switch( menuItem.getItemId() )
			{
				case R.id.menuEdit:
				{
					showEditCategoryDialog( cat, itemId );
					actionMode.finish();
					return true;
				}
				case R.id.menuDelete:
				{
					removeItem( itemId );
					actionMode.finish();
					return true;
				}
				default: return false;
			}
		}

		@Override
		public void onDestroyActionMode( ActionMode actionMode )
		{
			if( _lv.getChildCount() > _selectedPos )
			{
				_lv.getChildAt( ActivityCategories.this._selectedPos ).setBackgroundResource( Color.TRANSPARENT );
			}
			_actionMode = null;
		}
	}
}
