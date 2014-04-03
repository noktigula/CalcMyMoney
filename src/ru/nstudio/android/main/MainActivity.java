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

	private Menu			menu;
	private MenuListener menuListener;
	
	public static final int 	RESULT_FIRST_USER_MAIN = 10;
	public static final int		RESULT_FIRST_USER_DETAIL = 11;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_total_overview );

		_pager = (ViewPager)findViewById( R.id.pagerTotalOverview );
		_pager.setAdapter( new FinancePagerAdapter( getSupportFragmentManager() ) );
    	
    	menuListener = new MenuListener( this );
    } // onCreate

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
    }  // onClick


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


	  
//	public void onItemClick(AdapterView<?> adView, View target, int position, long id)
//	{
//		Intent intent;
//		if (id == -1)
//		{
//			intent = new Intent(INTENT_ACTION_ADD);
//		} // if
//		else
//		{
//			TextView tvMonth = (TextView)target.findViewById(R.id.tvMainMonthTitle);
//			intent = this.getIntentForChange((int)id, tvMonth.getText().toString());
//		} //if
//
//        runChangeActivity(intent);
//	} // onItemCLick

//    public void runChangeActivity(Intent intent)
//    {
//        try
//        {
//            startActivityForResult( intent, RESULT_FIRST_USER_MAIN );
//        } // try
//        catch(IllegalArgumentException iae)
//        {
//            Toast.makeText(this, iae.getMessage(), 10000).show();
//        } // catch
//    } // runChangeActivity

//    public Intent getIntentForChange(int id, String monthTitle)
//    {
//        Intent intent = new Intent(INTENT_ACTION_CHANGE);
//        intent.putExtra("ru.nstudio.android.selectedItem", id);
//        intent.putExtra("ru.nstudio.android.monthTitle", monthTitle);
//        return intent;
//    } // getIntentForChange
	  
//	  public void onActivityResult(int requestCode, int resultCode, Intent outputIntent)
//	  {
//		  boolean refresh = false;
//
//		  if (resultCode == RESULT_OK)
//		  {
//			  refresh = outputIntent.getBooleanExtra("ru.nstudio.android.changes", false);
//		  } // if
//		  else if (resultCode == RESULT_FIRST_USER_DETAIL)
//		  {
//			  refresh = outputIntent.getBooleanExtra("ru.nstudio.android.success", false);
//		  } // else if
//
//		  if (refresh)
//		  {
//			  this.makeListCalculations();
//		  } // if
//	  } // onActivityResult
} // class MainActivity