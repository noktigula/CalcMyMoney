package ru.nstudio.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import ru.nstudio.android.DBHelper;

import android.content.Intent;
/**
 * Created with IntelliJ IDEA.
 * User: noktigula
 * Date: 02.03.13
 * Time: 19:37
 * To change this template use File | Settings | File Templates.
 */
public class DeleteDialog implements DialogInterface.OnClickListener
{
    private int         itemId;
    private AlertDialog dialog;
    private Context     context;

    public DeleteDialog(Context context, int id)
    {
        this.itemId = id;
        this.context = context;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.deleteDialogTitle);
        builder.setMessage(R.string.deleteDialogFinalAsk);

        builder.setPositiveButton(R.string.dialogOK, this);
        builder.setNegativeButton(R.string.dialogCancel, this);

        this.dialog = builder.create();
    } // DeleteDialog

    public void show()
    {
        if(this.dialog != null)
        {
            dialog.show();
        } // if
        else
        {
            throw new NullPointerException("Dialog is null!");
        } // else
    } // show

    @Override
    public void onClick(DialogInterface v, int buttonId)
    {
        Class mainActivity = MainActivity.class;
        Class contextClass = this.context.getClass();
        if(buttonId == AlertDialog.BUTTON_POSITIVE)
        {
            if(contextClass == mainActivity)
            {
                this.deleteMonth();
            } // if
            else
            {
                this.deleteOperation();
            } // else
        } // buttonId
    } // onClick

    private void deleteMonth()
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.CURRENT_DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = "CAST (strftime('%Y', " + DBHelper.FINANCE_DATE + " ) AS INTEGER) = ? AND " +
                             "CAST (strftime('%m', " + DBHelper.FINANCE_DATE + " ) AS INTEGER) = ? ";

        int year = this.itemId / 100;
        int month = this.itemId % 100;

        String[] whereArgs = new String[]{Integer.toString(year), Integer.toString(month)};

        db.delete(DBHelper.FINANCE, whereClause, whereArgs);

       //Intent intent = new Intent();
        MainActivity activity = (MainActivity) this.context;
        activity.makeListCalculations();
    } // deleteItem

    private void deleteOperation()
    {

    } // deleteOperation

}

