package ecorecycle;

import java.io.*;
import java.util.*;

public class RMOS implements Serializable {	
	
	private ArrayList<RCM> allRCMs = new ArrayList<RCM>();
	
	private static Item availableItemTypes[]=new Item[9];
	
	public RMOS() {
		availableItemTypes[0] = new Item ("Mixed",0.0051,0);
		availableItemTypes[1] = new Item ("Cartons",0.05,1);
		availableItemTypes[2] = new Item ("Landfill",0.02,2);
		availableItemTypes[3] = new Item ("Glass",0.009,3);
		availableItemTypes[4] = new Item ("Cans",0.7,4);
		availableItemTypes[5] = new Item ("Cardboard",0.35,5);
		availableItemTypes[6] = new Item ("Plastics",0.17,6);
		availableItemTypes[7] = new Item ("Paper",0.1,7);
		availableItemTypes[8] = new Item ("Food Waste",0.009,8);
	}
	
	public static Item getRandomType() {
		int i = new Random().nextInt(8);
		return availableItemTypes[i];
	}
	public int getAvailableTypeId(String s) {
		for (int i = 0; i < availableItemTypes.length; i++)
			if (availableItemTypes[i].itemType.equals(s)) return i;
		return -1;
	}
		
	public ArrayList<RCM> getMachines(){
		return allRCMs;
	}
	public RCM getMachine(int i){
		return allRCMs.get(i);
	}
	
	public void addRCM(RCM rcmObj){
		allRCMs.add(rcmObj);		
	}
	
	public void removeRCM(RCM obj){
		allRCMs.remove(obj);
	}
		
	public String checkOperationalStatus(RCM obj){
		int j;
		for(j=0;j<allRCMs.size();j++){
			if(obj.equals(allRCMs.get(j))){
				return allRCMs.get(j).Status;
			}
		}
		return null;
	}
		
	 public void changeRecyclableItems(){
		 
	 }
	
	public void changePrice(RCM obj,Item item, Double price){
		int k = obj.listOfItems.indexOf(item);
		obj.listOfItems.get(k).price = price;
	}
	
	public Double  checkMoney(RCM obj){
		return obj.money;
	}
	
	public Double  checkCapacity(RCM obj){  // Return the capacity 
		
		return (obj.presentCapacity)/(obj.capacity)*100;
	}
	
	public Date getLastDateEmptied(RCM obj){
		return obj.lastEmptied.get(obj.listOfTransaction.size()-1);
	}
	
	public RCM returnMostUsedMachine(){
		int k, max;
		RCM maxObj;
		max = allRCMs.get(0).listOfTransaction.size();
		maxObj = allRCMs.get(0);
		for(k=1;k< allRCMs.size();k++){
			if(allRCMs.get(k).listOfTransaction.size() > max){
				max = allRCMs.get(k).listOfTransaction.size();
				maxObj = allRCMs.get(k);
			}
		}
		return maxObj;
	}
	
	public void display(){
		int k;
		for(k=0;k <allRCMs.size(); k++){
			System.out.println(allRCMs.get(k).machineId);
			System.out.println(allRCMs.get(k).location);
			System.out.println(allRCMs.get(k).Status);
		}
	}

	public static Item[] getAvailableItemTypes() {
		return availableItemTypes;
	}

	public String toString(){
		String test="";
		for(int i=0; i<allRCMs.size();i++) {
			test+=(allRCMs.get(i).location+"\n");
			for(int j=0; j< allRCMs.get(i).listOfItems.size();j++)
				test+=(allRCMs.get(i).listOfItems.get(j).getId()+"\n");
		}
		return test;
		
	}
	
	public static void setAvailableItemTypes(Item availableItemTypes[]) {
		RMOS.availableItemTypes = availableItemTypes;
	}
	public String getMaxTransactionMachine(int days){
		int max;
		max = allRCMs.get(0).getNumberOfTransaction(days);
		String Location = allRCMs.get(0).location;
		for(int i=1;i<allRCMs.size();i++){
			if(max <  allRCMs.get(i).getNumberOfTransaction(days)){
				max = allRCMs.get(i).getNumberOfTransaction(days);
				Location = allRCMs.get(i).location;
			}
		}
		return Location;
	}

}
