package ru.nstudio.android;

import java.util.GregorianCalendar;

import android.os.Parcel;
import android.os.Parcelable;

public class FinanceOperation implements Parcelable
{
	private String explain;
	private double price;
	private int quantity;
	private int	   type;
	private GregorianCalendar date;
	public static final int TYPE_INCOME = 1;
	public static final int TYPE_EXPEND = 0;
	
	public FinanceOperation(String strExplain, double dPrice, int iQty, boolean isIncome, GregorianCalendar gcDate)
	{
		this.setExplain(strExplain);
		this.setPrice(dPrice);
		this.setQuantity(iQty);
		this.setType(isIncome);
		this.setDate(gcDate);
	} // ctor
	
	private FinanceOperation(Parcel in)
	{
		explain = in.readString();
		price = in.readDouble();
		quantity = in.readInt();
		type = in.readInt();
		date = (GregorianCalendar) in.readSerializable();		
	} // конструктор дл€ случа€ восстановлени€ из Parcel
	
	public boolean setExplain(String strExplain)
	{
		this.explain = strExplain;
		return true;
	} // setExplain
	
	public boolean setPrice(double dPrice)
	{
		if (dPrice < 0) return false;
		
		this.price = dPrice;
		return true;
	} // boolean setPrice
	
	public boolean setQuantity (int iQty)
	{
		if (iQty < 0) return false;
		
		this.quantity = iQty;
		return true;
	} // setQuantity
	
	public void setType (boolean isIncome)
	{
		this.type = (isIncome) ? TYPE_INCOME : TYPE_EXPEND;
	} // setType
	
	public boolean setDate (GregorianCalendar gcDate)
	{
		/*ѕроверка допустимых значений*/
		this.date = gcDate;
		return true;
	} // setDate
	
	public String getExplain()
	{
		return this.explain;
	}
	
	public double getPrice()
	{
		return this.price;
	}
	
	public int getQuantity()
	{
		return this.quantity;
	}
	
	public int getType()
	{
		return this.type;
	}
	
	public GregorianCalendar getDate()
	{
		return this.date;
	}

	public int describeContents() 
	{
		// TODO Auto-generated method stub
		return 0;
	} // describeContents

	public void writeToParcel(Parcel dest, int flags) 
	{
		dest.writeString(getExplain());
		dest.writeDouble(getPrice());
		dest.writeInt(getQuantity());
		dest.writeInt(getType());
		dest.writeSerializable(getDate());		
	} // writeToParcel
	
	public static final Parcelable.Creator<FinanceOperation> CREATOR = new Parcelable.Creator<FinanceOperation>() 
	{
		public FinanceOperation createFromParcel(Parcel in)
		{
			return new FinanceOperation(in);
		} //createFromParcel
		
		public FinanceOperation[] newArray(int size)
		{
			return new FinanceOperation[size];
		} // newArray
	}; // ¬строенный класс Creator
} // class FinalOperations implements Parcelable
