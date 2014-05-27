package ru.nstudio.android.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import ru.nstudio.android.MenuListener;
import ru.nstudio.android.R;

public class MainActivity extends ActionBarActivity implements OnClickListener
{
	private ViewPager _pager;
	
	public static final int 	RESULT_FIRST_USER_MAIN = 10;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_total_overview );

		_pager = (ViewPager)findViewById( R.id.pagerTotalOverview );
		_pager.setAdapter( new FinancePagerAdapter( getSupportFragmentManager() ) );
    }

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater().inflate( R.menu.menu, menu );
		return true;
	}

    public void onClick(DialogInterface v, int buttonID)
    {
        switch (buttonID)
        {
            case AlertDialog.BUTTON_POSITIVE:

                return;

            default: break;
        }
    }


	@Override
	public boolean onOptionsItemSelected( MenuItem menuItem )
	{
		switch( menuItem.getItemId() )
		{
			case R.id.menuNewItem:
			{
				Intent intent = new Intent( getResources().getString( R.string.INTENT_ACTION_ADD ) );
				startActivityForResult( intent, RESULT_FIRST_USER_MAIN );
				return true;
			}
			case R.id.menuCategories:
			{
				Intent intent = new Intent( getResources().getString( R.string.INTENT_ACTION_SHOW_ALL_CATEGORIES ) );
				startActivity( intent );
				return true;
			}
			default: return super.onOptionsItemSelected( menuItem );
		}
	}
}