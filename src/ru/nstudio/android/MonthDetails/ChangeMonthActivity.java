package ru.nstudio.android.MonthDetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;

import android.view.Menu;
import android.view.MenuItem;

import ru.nstudio.android.details.DetailsActivity;
import ru.nstudio.android.MonthDetails.Adapters.MonthOverviewPagerAdapter;
import ru.nstudio.android.R;

public class ChangeMonthActivity extends ActionBarActivity
{
	private ViewPager		_pager;
	private int 			_idItem;
	private ActionMode		_actionMode;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_month_details );

		Intent parentIntent = getIntent();
		_idItem = parentIntent.getIntExtra(getString( R.string.key_selected_item ), -1);
		String monthTitle = parentIntent.getStringExtra( getString( R.string.key_month_title ) );

		if(_idItem == -1) throw new IllegalArgumentException("ERROR: can`t load _month details - can`t get _month ID");

		_pager = (ViewPager)findViewById( R.id.pagerMonth );
		_pager.setAdapter( new MonthOverviewPagerAdapter( _idItem, getSupportFragmentManager() ) );
		_pager.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener()
			{
				@Override
				public void onPageSelected( int position )
				{
					if( _actionMode != null )
					{
						_actionMode.finish();
					}
					getActionBar().setSelectedNavigationItem( position );
				}
			});

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );

		StringBuilder stringBuilder = new StringBuilder( );
		stringBuilder.append( monthTitle );
		stringBuilder.append( " " );
		stringBuilder.append( Integer.toString( _idItem/100 ) );

		actionBar.setTitle( stringBuilder.toString() );

		ActionBar.TabListener tabListener = new ActionBar.TabListener()
		{
			@Override
			public void onTabSelected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction )
			{
				_pager.setCurrentItem( tab.getPosition() );
			}

			@Override
			public void onTabUnselected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction )
			{

			}

			@Override
			public void onTabReselected( ActionBar.Tab tab, FragmentTransaction fragmentTransaction )
			{

			}
		};

		actionBar.addTab( actionBar.newTab().setText( "Операции" ).setTabListener( tabListener ) );
		actionBar.addTab( actionBar.newTab().setText( "Категории" ).setTabListener( tabListener ) );
	} // onCreate

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater().inflate( R.menu.menu_change_month, menu );
		return true;
	}

	@Override
	public void onSupportActionModeStarted( ActionMode mode )
	{
		super.onSupportActionModeStarted( mode );
		_actionMode = mode;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem menuItem )
	{
		switch( menuItem.getItemId() )
		{
			case R.id.menuNewItem:
			{
				Intent intent = new Intent( this, DetailsActivity.class );

				intent.putExtra(getString( R.string.key_month ), _idItem%100 );
				intent.putExtra(getString( R.string.key_year ), _idItem/100 );

				startActivity( intent );
				return true;
			}
			default: return super.onOptionsItemSelected( menuItem );
		}
	}
}
