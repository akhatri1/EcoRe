package gui;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import ecorecycle.RCM;
import ecorecycle.RMOS;

public class EcoReSystem {

	public static void main(String[] args) {
  	
		RMOS recoveredStation = new RMOS();
        //deserialize the quarks.ser file
        try(
          InputStream file = new FileInputStream("data.ser");
          InputStream buffer = new BufferedInputStream(file);
          ObjectInput input = new ObjectInputStream (buffer);
        ){
          //deserialize the List
          recoveredStation = (RMOS) input.readObject();
          //display its data
        }
        catch(ClassNotFoundException ex){
          fLogger.log(Level.SEVERE, "Cannot perform input. Class not found.", ex);
        }
        catch(IOException ex){
          fLogger.log(Level.SEVERE, "Cannot perform input.", ex);
        }
        AdminUI stationUI = new AdminUI(recoveredStation);            	
        stationUI.createAndShowGUI();
        
	}

    private static final Logger fLogger = Logger.getLogger(EcoReSystem.class.getPackage().getName());
  
}
