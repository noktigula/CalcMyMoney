package ru.nstudio.android.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.nstudio.android.ContextMenuInitializer;
import ru.nstudio.android.DeleteDialog;
import ru.nstudio.android.MenuListener;
import ru.nstudio.android.R;

public class MainActivity extends ActionBarActivity implements OnItemClickListener, OnClickListener
{
	private ViewPager _pager;

	private Menu			menu;
	private MenuListener menuListener;
	
	public static final int 	RESULT_FIRST_USER_MAIN = 10;
	public static final int		RESULT_FIRST_USER_DETAIL = 11;

    private static String INTENT_ACTION_CHANGE = "ru.nstudio.android.changeMonth";
    private static String INTENT_ACTION_ADD    = "ru.nstudio.android.addDetails";
	
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        ContextMenuInitializer initializer = new ContextMenuInitializer(menu, v, menuInfo);
        menu = initializer.getMenu();
    } // onCreateContextMenu

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TableLayout tl = (TableLayout) acmi.targetView;

        if(tl.getChildCount() <= 0)
        {
            return false;
        } // if

        TextView tvMonth = (TextView) tl.getChildAt(0);
        String monthTitle = tvMonth.getText().toString();

        Intent intent;

        switch (item.getGroupId())
        {
            case ContextMenuInitializer.CONTEXT_MENU_CHANGE:
            {
                intent = this.getIntentForChange((int)acmi.id, monthTitle);
                runChangeActivity(intent);
                break;
            }  // case change

            case ContextMenuInitializer.CONTEXT_MENU_DELETE:
            {
                deleteMonthInfo((int)acmi.id);
                break;
            }  // case delete

            default:
            {
                break;
            } // default
        } // switch

        return super.onContextItemSelected(item);
    }   // onContextItemSelected

    public void deleteMonthInfo(int monthYearCode)
    {
        DeleteDialog dialog = new DeleteDialog(this, monthYearCode);
        dialog.show();
    } // deleteMonthInfo

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
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		
		this.menu = menu;
		//return true;
		return super.onCreateOptionsMenu(menu);
	} // onCreateOptionsMenu

	  
	public void onItemClick(AdapterView<?> adView, View target, int position, long id)
	{
		Intent intent;
		if (id == -1)
		{
			intent = new Intent(INTENT_ACTION_ADD);
		} // if
		else
		{
			TextView tvMonth = (TextView)target.findViewById(R.id.tvMainMonthTitle);
			intent = this.getIntentForChange((int)id, tvMonth.getText().toString());
		} //if
		
        runChangeActivity(intent);
	} // onItemCLick

    public void runChangeActivity(Intent intent)
    {
        try
        {
            startActivityForResult(intent, RESULT_FIRST_USER_MAIN);
        } // try
        catch(IllegalArgumentException iae)
        {
            Toast.makeText(this, iae.getMessage(), 10000).show();
        } // catch
    } // runChangeActivity

    public Intent getIntentForChange(int id, String monthTitle)
    {
        Intent intent = new Intent(INTENT_ACTION_CHANGE);
        intent.putExtra("ru.nstudio.android.selectedItem", id);
        intent.putExtra("ru.nstudio.android.monthTitle", monthTitle);
        return intent;
    } // getIntentForChange
	  
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