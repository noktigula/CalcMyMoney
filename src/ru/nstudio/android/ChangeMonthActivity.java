package ru.nstudio.android;

//import android.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import ru.nstudio.android.FinanceOperation;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ChangeMonthActivity extends ListActivity implements /*OnClickListener,*/ OnItemClickListener
{
	private Button 						btnOk;
	private ListView 					lvAddFinances;
	private View						vFooter;
	private View						vHeader;
	//private ArrayList<FinanceOperation> alOperations;
	private DBHelper					dbHelper;
	private SQLiteDatabase				db;
	private int							month;
	private int							year;
	public  boolean 					wasChanges;
	private TextView					tvMonthDescription;
    private final String INTENT_ACTION_SHOW_DETAILS = "ru.nstudio.android.showDetails";
		
	private static int RESULT_FIRST_USER_DETAIL = 11;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_month);
	
		Intent parentIntent = getIntent();
		int idItem = parentIntent.getIntExtra("ru.nstudio.android.selectedItem", -1);

		if(idItem == -1) throw new IllegalArgumentException("ERROR: can`t load month details - can`t get month ID");
		
		this.month = idItem % 100;
		this.year = idItem / 100;
		
		String monthTitle = new String(parentIntent.getStringExtra("ru.nstudio.android.monthTitle") + " " + Integer.toString(year));

		this.tvMonthDescription = (TextView)findViewById(R.id.tvMonthDescription);
		this.tvMonthDescription.setText(monthTitle);
		
		this.initDatabase();

		this.vFooter = getLayoutInflater().inflate(R.layout.tip_add_new_details, null);
		
		createListView();
		
		this.wasChanges = false;

        registerForContextMenu(this.lvAddFinances);
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
        AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) acmi.id;

        switch(item.getGroupId())
        {
            case ContextMenuInitializer.CONTEXT_MENU_CHANGE:
            {
                this.showOperationDetails(id);
                break;
            } // change

            case ContextMenuInitializer.CONTEXT_MENU_DELETE:
            {
                this.deleteOperation(id);
                break;
            } // delete

            default:
            {
                break;
            } // def
        } //switch

        return true;
    } // onContextItemSelected
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent intent = new Intent();
			intent.putExtra("ru.nstudio.android.changes", this.wasChanges);
			setResult(RESULT_OK, intent);
		} // if
		return super.onKeyDown(keyCode, event);
	} // onKeyDow
	
	public void initDatabase()
	{
		if(this.dbHelper == null)
		{
			this.dbHelper = new DBHelper(this, DBHelper.CURRENT_DATABASE_VERSION);
		} // if
		if(this.db == null)
		{
			this.db = this.dbHelper.getWritableDatabase();
		} // if
		
		if(!this.db.isOpen())
			this.db = this.dbHelper.getWritableDatabase();
	} // initDatabase

	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) 
	{
		this.showOperationDetails((int) id);
	} // OnItemClick

    public void showOperationDetails(int id)
    {
        Intent intent = new Intent(this.INTENT_ACTION_SHOW_DETAILS);
        intent.putExtra("ru.nstudio.android.idFinance", id);

        intent.putExtra("ru.nstudio.android.month", this.month);
        intent.putExtra("ru.nstudio.android.year", this.year);

        startActivityForResult(intent, RESULT_FIRST_USER_DETAIL);
    } // showOperationDetails

    public void deleteOperation(int id)
    {
        DeleteDialog deleteDialog = new DeleteDialog(this, id);
        deleteDialog.show();
    } // deleteOperation
	
	public void createListView()
	{
		this.initDatabase();
		if (this.lvAddFinances != null)
		{
			this.lvAddFinances.removeFooterView(this.vFooter);		
		} // if
		this.lvAddFinances = null;
		this.lvAddFinances = getListView();
				
		this.lvAddFinances.addFooterView(this.vFooter, null, true);
		
//		String[] columns = new String[]{"idFinance", "reason", "price", "quantity", "type", "financeDate"};
//		String whereClause  = new String("strftime('%Y', financeDate) = ? and strftime('%m', financeDate) = ?");
		String monthWithLeadingZero;
		if ((this.month / 10) == 0)
		{
			monthWithLeadingZero = new String("0" + Integer.toString(this.month));
		} // if month without leading zero
		else
		{
			monthWithLeadingZero = new String(Integer.toString(this.month));
		} // else

		String[] whereArgs = new String[] {Integer.toString(this.year), monthWithLeadingZero};
//		String order = new String("strftime('%d', financeDate), idFinance");
//
//		Cursor c = this.db.query("Finance", columns, whereClause, whereArgs, null, null, order);

		String query = " SELECT " +
					   "f." + DBHelper.Finance.ID 		+ ", " +
					   "f." + DBHelper.Finance.REASON 	+ ", " +
					   "f." + DBHelper.Finance.PRICE 	+ ", " +
					   "f." + DBHelper.Finance.QUANTITY + ", " +
					   "f." + DBHelper.Finance.DATE 	+ ", " +
					   "f." + DBHelper.Finance.TYPE 	+ ", " +
					   "c." + DBHelper.Category.NAME    +
					   " FROM " + DBHelper.Finance.TABLE_NAME + " AS f " +
					   " INNER JOIN " + DBHelper.Category.TABLE_NAME +  " AS c " +
					   		"ON f." + DBHelper.Finance.ID + " = c." + DBHelper.Category.ID +
					   " WHERE strftime('%Y', " + DBHelper.Finance.DATE + ") = ? " +
						  "AND strftime('%m', " + DBHelper.Finance.DATE + ") = ? " +
					   " ORDER BY strftime('%d', " + DBHelper.Finance.DATE + "), " + DBHelper.Finance.ID;


		Cursor c = this.db.rawQuery(query, whereArgs);
		
		MonthDetailsAdapter mda = new MonthDetailsAdapter(this, this.getLayoutInflater(), c);
		
		lvAddFinances.setAdapter(mda);				
		lvAddFinances.setOnItemClickListener(this);
		
		c.close();
		this.db.close();
	} // createListView
	
	public void onActivityResult(int requestCode, int resultCode, Intent outputIntent)
	{
		if (resultCode == RESULT_FIRST_USER_DETAIL)
		{
			this.wasChanges = outputIntent.getBooleanExtra("ru.nstudio.android.success", false);
			if (this.wasChanges)
			{
				this.createListView();
			} // if
		} //if result_code ok
	} // onActivityResult
		
} // Activity
