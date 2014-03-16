package ecorecycle;
import gui.AdminUI;
import gui.UserUI;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JOptionPane;

public class RCM implements Serializable {

	public int machineId;
	public String location;
	public ArrayList<Item> listOfItems = new ArrayList<Item>();
	public Double capacity;
	public Double presentCapacity;
	public ArrayList<Date> lastEmptied = new ArrayList<Date>();
	public String Status;
	public Double money;
	private Double totalWt=0.0;
	


	public ArrayList<Transaction> listOfTransaction = new ArrayList<Transaction>();
	public Transaction currentTransaction;
	public int coupons=0;
	public int weight=0;
	private Double moneyForSession;
	public double recyclableQtd[] = {		0.0, 		0.0,		0.0,		0.0,		0.0,		0.0,	0.0,		0.0,		0.0 };
	public double recyclableAmount[] = {		0.0, 		0.0,		0.0,		0.0,		0.0,		0.0,	0.0,		0.0,		0.0 };


	
	public class Transaction implements Serializable {
		private Date transactionDate;
		private int flagCoupon;
		public ArrayList<Item> transactionItems = new ArrayList<Item>();
		public Double totalWeight = 0.0;
		public Double totalAmount;
		Transaction(){
			this.setTransactionDate(new Date());
			this.setFlagCoupon(coupons);
		}
		public Double getTotalAmount() {
			Double totalAmount = 0.0;
			for(int i=0; i < transactionItems.size(); i++)
				totalAmount += transactionItems.get(i).price*transactionItems.get(i).weight;
			this.totalAmount=totalAmount;
			if(totalAmount > money){
				this.setFlagCoupon(1);
				//TODO REflect change on the UI
			}
			return totalAmount;
		}
		public int getTotalQtd() {
			int totalQtd = transactionItems.size();
			return totalQtd;
		}
		public Date getTransactionDate() {
			return transactionDate;
		}
		public void setTransactionDate(Date transactionDate) {
			this.transactionDate = transactionDate;
		}
		public int getFlagCoupon() {
			return flagCoupon;
		}
		public void setFlagCoupon(int flagCoupon) {
			this.flagCoupon = flagCoupon;
		}
		
	}
	
	public RCM(String location, Double  capacity, Double  money){
		System.out.print("= Generating new RCM at "+location+" with $"+money+" and capacity to support "+capacity+"lbs.\n");
		this.capacity = capacity;
		this.Status = "Disabled";
		this.presentCapacity = capacity;
		currentTransaction = new Transaction();
		this.lastEmptied.add(new Date());
		this.money = money;
		this.location = location;
		this.setCoupons(0);
	}
	
	public Date getLastEmptied() {
		return lastEmptied.get(lastEmptied.size()-1);
	}

	public ArrayList<Item> showRecyclableItemList(){
		return listOfItems;
	}	
	
	public void dropRecyclableItem(Item recyclableItem){
			 this.currentTransaction.transactionItems.add(recyclableItem);
			 this.currentTransaction.totalWeight+=recyclableItem.weight;
			 //decrement current capacity
			 this.presentCapacity -= recyclableItem.weight;
			 System.out.print("= Dropping "+recyclableItem.weight+" of "+ recyclableItem.itemType +" in RCM "+this.location+"\n");		 
	 }
	
	public void dropRandomRecyclableItem(){
		Item recyclableItem = listOfItems.get(new Random().nextInt(listOfItems.size()));
		recyclableItem.setWeight(new Random().nextDouble()*10);
		dropRecyclableItem(recyclableItem); 
	 }
	 public void addRecyclableItem(Item recyclableItem){
		//checks if the itemType don't already exist 
		 if(this.listOfItems.indexOf(recyclableItem)==-1) {
			 this.listOfItems.add(recyclableItem);
			   System.out.print("= Adding item type: "+recyclableItem.itemType+"\n");
		 }
	 }
	 
	 public Boolean validateItem(Item recyclableItem){
		 //Test Machine Status
		 if(!this.Status.equals("Enabled")){
//    	   JOptionPane.showInternalMessageDialog(null, " = Error: Can't drop item because the RCM is not enabled.\n", "Alert", JOptionPane.ERROR_MESSAGE); 
			 System.out.printf("= Error: Can't drop item because the RCM is not enabled.\n");
			 return false;
		 } 
		 //Test Capacity
		 if(this.presentCapacity < recyclableItem.weight){
			System.out.printf("= Error: That item is too heavy!\n");
			return false;
		 }
		 //Test ItemType
		for(int k=0; k < listOfItems.size(); k++){
			if(recyclableItem.itemType.equals(listOfItems.get(k).itemType))
				return true;
		} 	
		System.out.printf("= Error: This RCM does not accept this item type!\n");
		return false;

	 }	 
	 public String checkStatus(){
		 return Status;
	 }
	 
	 public Transaction getLastTransaction(){
		 return this.listOfTransaction.get(listOfTransaction.size()-1);
	 }
	 public Double  SessionMoney(){
		 return this.moneyForSession;
	 }

	public void activate() {
			this.Status = "Enabled";
			System.out.print("= Activating "+this.location+" machine!\n");
	}
	
	
	public int getCoupons() {
		return coupons;
	}
	public Double getTotalWeightOfMachine(){
		totalWt = 0.0;
		for(int i=0;i < this.listOfTransaction.size();i++){
			 totalWt = 		totalWt+ this.listOfTransaction.get(i).totalWeight;
		}
//		System.out.print("= Computing total weight.. This transaction had "+			 System.out.print("= Computing total weight.. This transaction had "+ +". \n"); +". \n");
		return totalWt;
	}
	public double getWeightofMachine(int days){
		Date myDate = null;
		totalWt = 0.0;
		Calendar calendar = Calendar.getInstance();
		//calendar.setTime(myDate);
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date newDate = calendar.getTime();
		for(int i=0;i <listOfTransaction.size();i++){
			if(this.listOfTransaction.get(i).transactionDate.after(newDate)){
				totalWt = totalWt+ this.listOfTransaction.get(i).totalWeight;;
			}
		}
		return totalWt;
	}


public void finishTransaction () {
	this.listOfTransaction.add(currentTransaction);
	if(currentTransaction.getTotalAmount() <= money){
		this.money-=currentTransaction.getTotalAmount();
	}
	
	this.currentTransaction = new Transaction();
}
	public void setCoupons(int coupons) {
		this.coupons = coupons;
	}
	public int getNumberOfTransaction(int days){
		Date myDate = null;
		int n=0;
		Calendar calendar = Calendar.getInstance();
		//calendar.setTime(myDate);
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date newDate = calendar.getTime();
		for(int i=0;i <listOfTransaction.size();i++){
			if(this.listOfTransaction.get(i).transactionDate.after(newDate)){
				n++;
			}
		}
		return n;
	}
	public Double getTotalValueOfCoupons(int days){
		Double couponAmount = 0.0;
		Date myDate = null;
		Calendar calendar = Calendar.getInstance();
		//calendar.setTime(myDate);
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date newDate = calendar.getTime();
		for(int i=0;i < this.listOfTransaction.size();i++){
			if(this.listOfTransaction.get(i).flagCoupon == 1 && this.listOfTransaction.get(i).transactionDate.after(newDate)){
				couponAmount += this.listOfTransaction.get(i).totalAmount;
			}
		}
		return couponAmount;
	}
	public Double getTotalWeightOfItem(String type, int days){
		Double totalWeight = 0.0;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date newDate = calendar.getTime();
		for(int i=0;i < this.listOfTransaction.size();i++){
			if(this.listOfTransaction.get(i).transactionDate.after(newDate))
				for(int j=0;j < this.listOfTransaction.get(i).transactionItems.size();j++)
						if (this.listOfTransaction.get(i).transactionItems.get(j).itemType.equalsIgnoreCase(type)) 
							totalWeight += this.listOfTransaction.get(i).transactionItems.get(j).weight;
			}
		return totalWeight;
	}
	public Double getTotalMoneyOfItem(String type, int days){
		Double totalMoney = 0.0;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date newDate = calendar.getTime();
		for(int i=0;i < this.listOfTransaction.size();i++){
			if(this.listOfTransaction.get(i).transactionDate.after(newDate))
				for(int j=0;j < this.listOfTransaction.get(i).transactionItems.size();j++)
						if (this.listOfTransaction.get(i).transactionItems.get(j).itemType.equalsIgnoreCase(type)) 
							totalMoney += (this.listOfTransaction.get(i).transactionItems.get(j).price * this.listOfTransaction.get(i).transactionItems.get(j).weight);
			}
		return totalMoney;
	}
	public int getTotalItemsOfType(String type, int days){
		int n = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date newDate = calendar.getTime();
		for(int i=0;i < this.listOfTransaction.size();i++){
			if(this.listOfTransaction.get(i).transactionDate.after(newDate))
				for(int j=0;j < this.listOfTransaction.get(i).transactionItems.size();j++)
						if (this.listOfTransaction.get(i).transactionItems.get(j).itemType.equalsIgnoreCase(type)) 
							n++;
			}
		return n;
	}
	public int getTotalTransactionsWithType(String type, int days){
		int n = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date newDate = calendar.getTime();
		for(int i=0;i < this.listOfTransaction.size();i++){
			if(this.listOfTransaction.get(i).transactionDate.after(newDate))
				for(int j=0;j < this.listOfTransaction.get(i).transactionItems.size();j++)
						if (this.listOfTransaction.get(i).transactionItems.get(j).itemType.equalsIgnoreCase(type)) { 
							n++;
							break;
						}
			}
		return n;
	}	
	public Double getTotalValueOfCash(int days){
		Double cashAmount = 0.0;
		Date myDate = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");
		//calendar.setTime(myDate);
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date newDate = calendar.getTime();
		System.out.println("\n= Start Date : " + sdf.format(calendar.getTime()));

		for(int i=0;i < this.listOfTransaction.size();i++){
			if(this.listOfTransaction.get(i).flagCoupon == 0 && this.listOfTransaction.get(i).transactionDate.after(newDate)){
				cashAmount += this.listOfTransaction.get(i).totalAmount;
				System.out.println("\n =Date : " + sdf.format(this.listOfTransaction.get(i).transactionDate)+" $"+this.listOfTransaction.get(i).totalAmount);
				
			}
		}
		return cashAmount;
	}
	public int getNumberOfTimesMachineEmptied(int days){
		Date myDate = null;
		int n=0;
		Calendar calendar = Calendar.getInstance();
		//calendar.setTime(myDate);
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date newDate = calendar.getTime();
		for(int i=0; i< lastEmptied.size(); i++){
			if(lastEmptied.get(i).after(newDate)){
				n++;
			}
		}
		return n;
	}
	public int getNumberOfItems(int days){
		Date myDate = null;
		int totalItems =0;
		Calendar calendar = Calendar.getInstance();
		//calendar.setTime(myDate);
		calendar.add(Calendar.DAY_OF_YEAR, -days);
		Date newDate = calendar.getTime();
		for(int i=0;i<this.listOfTransaction.size(); i++){
			if(this.listOfTransaction.get(i).transactionDate.after(newDate)){
				totalItems += this.listOfTransaction.get(i).transactionItems.size();
			}
		}
		return totalItems;
	}
	public void setLastEmptied(Date lastEmptied) {
		this.lastEmptied.add(lastEmptied);
		this.presentCapacity = capacity;
	}
	
}


