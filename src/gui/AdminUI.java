package gui;
import ecorecycle.Item;
//import ecorecycle.*;


import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import ecorecycle.RCM;
import ecorecycle.RMOS;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


@SuppressWarnings("serial")
public class AdminUI extends JPanel implements  ActionListener, 
												TableModelListener, 
												WindowListener{
	//UI Variables Declaration
	JToggleButton unit, cash;
	JButton newMachine, removeMachine, changeItem, addItem,activateRCM,loadMachine, EmptyMachine,submitBt ;
	JTextArea transactionItemsTextArea;
	JTextField loginTF, passwdTF;
	JComboBox graphReportCB;
	static JComboBox RCMCB;
	static JComboBox dayCB;
	JLabel totalAmount,machinesLabel, statsLabel, itemTypes, loginL, passwdL, loginStatusL;
    JLabel [] separator = new JLabel[16];
    final static JFXPanel weightFX = new JFXPanel();
    final static JFXPanel moneyFX = new JFXPanel();
    final static JFXPanel itemsFX = new JFXPanel();
    final static JFXPanel transactionFX = new JFXPanel();
    static JTextArea weightTA;
	JTextArea moneyTA;
	JTextArea itemsTA;
	JTextArea transactionsTA;
    static JScrollPane weightSP;
	static JScrollPane moneySP;
	static JScrollPane itemsSP;
	static JScrollPane transactionsSP;
    static DefaultTableModel defTableModel;
    static int numberOfDays=0, selectedTab=0;
	private JTable table;
	Container topMenuContainer, loginContainer, statsContainer1,statsContainer2,statsContainer3,statsContainer4;
	static JTabbedPane tabbedPane;
	
    String[] columnNames = {"ID",
            "Location",
            "Capacity",
            "$ Available",
            "Status",
            "Item Types",
            "Last Emptied"};
	private final String IMG_PATHS[] = {
			"./img/auiHeader.png",
			"./img/RecyclingMachines.png",
			"cuiUnitKgButton",
			"./img/newMachine.png",
			"./img/ItemTypes.png",
			"./img/buttons/Slice-",
			"./img/Stats.png",
			"./img/logo.png"};
	static JFrame frame;

	//Model variables
	public static RMOS station = new RMOS();
	private static int selectedRcm;
	private  String [] usr= {"Guilherme", "Ankit"};
	private String passwd = "admin";
    private static final Logger fLogger = Logger.getLogger(EcoReSystem.class.getPackage().getName());

	
	
	//Class constructor
	public AdminUI (RMOS stationRecovered) {
		//Basic Layout Definitions
		super(new FlowLayout());
		AdminUI.station=stationRecovered;
		this.setBackground(Color.WHITE);
		loadLoginScreen();
		
	}
	private boolean authenticate() {
		if ((loginTF.getText().contentEquals(usr[1]) ||
				loginTF.getText().contentEquals(usr[0]) ) 
				&& passwdTF.getText().contentEquals(passwd)  ) {
			System.out.print("Login OK!\n");			
			return true;
		}
		else {
			System.out.print("= Login not OK!\n");
			return false;
		}
	}
	private void loadAdminPanel() {
	loadTable();

	loadMenuBar();

	//Creating the header
	JLabel header = loadImage(IMG_PATHS[0]);
	machinesLabel = loadImage(IMG_PATHS[1]);
	itemTypes = loadImage(IMG_PATHS[4]);
	
	Container topLabels = new Container();
	topLabels.setLayout(new BoxLayout(topLabels, BoxLayout.LINE_AXIS));
	topLabels.add(machinesLabel);
	
	Container lowLabels = new Container();
	lowLabels.setLayout(new BoxLayout(lowLabels, BoxLayout.X_AXIS));

	String[] graphReportSelector = { "Graphics", "Report" };
	String[] RCMSelector = { "All RCMs", "Selected RCM" };
	String[] daysSelector = { "All time", "Day", "Week", "Month", "# of days" };

	//Create the combo box, select item at index 4.
	//Indices start at 0, so 4 specifies the pig.
	graphReportCB = new JComboBox(graphReportSelector);
	graphReportCB.setSelectedIndex(0);
	graphReportCB.addActionListener(this);

	RCMCB = new JComboBox(RCMSelector);
	RCMCB.setSelectedIndex(0);
	RCMCB.addActionListener(this);

	dayCB = new JComboBox(daysSelector);
	dayCB.setSelectedIndex(0);
	dayCB.addActionListener(this);
	
	separator[11].setText("|");
	separator[13].setText("|");
	separator[12].setText("|");
	separator[14].setText("|           ");
	
	lowLabels.add(itemTypes);
	lowLabels.add(separator[13]);	
	lowLabels.add(graphReportCB);
	lowLabels.add(separator[11]);	
	lowLabels.add(RCMCB);
	lowLabels.add(separator[12]);	
	lowLabels.add(dayCB);
	lowLabels.add(separator[14]);
	
	Container centerContainer = new Container();
	centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.LINE_AXIS));
	
    JScrollPane tableScrollPane = new JScrollPane(table);
	tableScrollPane .setPreferredSize(new Dimension(460,100));

	centerContainer.add(new Box.Filler(new Dimension(20,20),new Dimension(20,20),new Dimension(20,20)));
	Container tableContainer = new Container();
	
	
	tableContainer.add(topMenuContainer);
	tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.PAGE_AXIS));
	tableContainer.add(table.getTableHeader(), BorderLayout.PAGE_START);
	tableContainer.add(tableScrollPane , BorderLayout.CENTER);
	centerContainer.add(tableContainer);

	weightTA = new JTextArea("weight");
    weightSP = new JScrollPane(weightTA);
    weightSP.setBorder(null);
	weightTA.setFont(new Font("Letter Gothic Std", Font.BOLD, 14));
	weightTA.setBackground(new Color(246,246,246));
	weightTA.setSize(750,150);
    weightSP.setPreferredSize(weightTA.getSize());
	weightTA.setEditable(false);
	weightSP.setVisible(false);

	weightFX.setName("Weight");
	statsContainer1 = new Container();
	statsContainer1 .setLayout(new FlowLayout());
	statsContainer1.add(weightFX);
	statsContainer1.add(weightSP);

	moneyTA = new JTextArea("money");
    moneySP = new JScrollPane(moneyTA);
    moneySP.setBorder(null);
	moneyTA.setFont(new Font("Letter Gothic Std", Font.BOLD, 14));
	moneyTA.setBackground(new Color(246,246,246));
	moneyTA.setSize(750,150);
    moneySP.setPreferredSize(moneyTA.getSize());
	moneyTA.setEditable(false);
	moneySP.setVisible(false);
	
	moneyFX.setName("Money");
	statsContainer2 = new Container();
	statsContainer2 .setLayout(new FlowLayout());
	statsContainer2.add(moneyFX);
	statsContainer2.add(moneySP);

	itemsTA = new JTextArea("item");
    itemsSP = new JScrollPane(itemsTA);
    itemsSP.setBorder(null);
	itemsTA.setFont(new Font("Letter Gothic Std", Font.BOLD, 14));
	itemsTA.setBackground(new Color(246,246,246));
	itemsTA.setSize(750,150);
    itemsSP.setPreferredSize(itemsTA.getSize());
	itemsTA.setEditable(false);
	itemsSP.setVisible(false);
	
	itemsFX.setName("Item");
	statsContainer3 = new Container();
	statsContainer3 .setLayout(new FlowLayout());
	statsContainer3.add(itemsFX);
	statsContainer3.add(itemsSP);

	transactionsTA = new JTextArea("transaction");
    transactionsSP = new JScrollPane(transactionsTA);
    transactionsSP.setBorder(null);
	transactionsTA.setFont(new Font("Letter Gothic Std", Font.BOLD, 14));
	transactionsTA.setBackground(new Color(246,246,246));
	transactionsTA.setSize(750,150);
    transactionsSP.setPreferredSize(transactionsTA.getSize());
	transactionsTA.setEditable(false);
	transactionsSP.setVisible(false);
	
	transactionFX.setName("Transaction");
	statsContainer4 = new Container();
	statsContainer4 .setLayout(new FlowLayout());
	statsContainer4.add(transactionFX);
	statsContainer4.add(transactionsSP);


    tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Weight", null, statsContainer1, "Presents statistics related to weight");
    tabbedPane.addTab("Money", null, statsContainer2, "Presents the statistics about money");
    tabbedPane.addTab("Items", null, statsContainer3, "Presents the statistics about the item types");
    tabbedPane.addTab("Transactions", null, statsContainer4, "Presents the statistics about the transactions in each machine");
    tabbedPane.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            selectedTab = tabbedPane.getSelectedIndex();
            refreshInfo();
        }
    });
	
	Platform.runLater(new Runnable() {
        @Override
        public void run() {
            initFX(weightFX);
        }
	});
	Platform.runLater(new Runnable() {
        @Override
        public void run() {
    		initFX(moneyFX);
        }
	});
	
	Platform.runLater(new Runnable() {
        @Override
        public void run() {
            initFX(itemsFX);
        }
	});
	Platform.runLater(new Runnable() {
        @Override
        public void run() {
    		initFX(transactionFX);
        }
	});
	
	add(header);
	add(topLabels);
	add(centerContainer);		
	add(lowLabels);
	add(tabbedPane);
	
	refreshInfo();
	
	}
	private void loadLoginScreen(){
		
		JLabel logo = loadImage(IMG_PATHS[7]);
		
		loginL = new JLabel("Login");
		loginL.setFont(new Font("Lobster 1.4", Font.BOLD, 16));
		loginL.setForeground(new Color(83,88,95));
		loginL.setPreferredSize(new Dimension(50,20));
		
		loginTF = new JTextField();
		loginTF.setPreferredSize(new Dimension(100,20));
		
		passwdL = new JLabel("Password");
		passwdL.setBorder(null);
		passwdL.setFont(new Font("Lobster 1.4", Font.BOLD, 16));
		passwdL.setForeground(new Color(83,88,95));
		passwdL.setPreferredSize(new Dimension(70,20));

		passwdTF = new JPasswordField();
		passwdTF.setPreferredSize(new Dimension(100,20));
		passwdTF.setSize(100,20);
	    submitBt = new JButton("Submit");
	    submitBt.setBorder(null);
	    submitBt.setFont(new Font("Lobster 1.4", Font.BOLD, 16));
	    submitBt.setForeground(new Color(42,195,207));
	    submitBt.addActionListener(this);

		loginStatusL = new JLabel("");
		loginStatusL.setFont(new Font("Lobster 1.4", Font.BOLD, 16));
		loginStatusL.setForeground(new Color(83,88,95));
		loginStatusL.setPreferredSize(new Dimension(240,20));


		loginContainer = new Container();
		loginContainer.setLayout(new FlowLayout());
		loginContainer.add(logo);
		loginContainer.add(loginL);
		loginContainer.add(loginTF);
		loginContainer.add(passwdL);
		loginContainer.add(passwdTF);
	    loginContainer.add(submitBt);
		loginContainer.add(loginStatusL);
		add(loginContainer);
	}
		private void loadMenuBar() {
		    //Creating the menu bar
		    newMachine = new JButton("New Machine");
		    newMachine.setBorder(null);
		    newMachine.setFont(new Font("Letter Gothic Std", Font.BOLD, 12));
			newMachine.setForeground(new Color(42,195,207));
			newMachine.addActionListener(this);

		    removeMachine = new JButton("Remove Machine");
		    removeMachine .setBorder(null);
		    removeMachine .setFont(new Font("Letter Gothic Std", Font.BOLD, 12));
		    removeMachine .setForeground(new Color(42,195,207));
		    removeMachine.addActionListener(this);
		    
		    addItem = new JButton("Add Item type");
		    addItem .setBorder(null);
		    addItem .setFont(new Font("Letter Gothic Std", Font.BOLD, 12));
		    addItem .setForeground(new Color(42,195,207));
		    addItem .addActionListener(this);
		    
		    changeItem = new JButton("Change Items");
		    changeItem.setBorder(null);
		    changeItem.setFont(new Font("Letter Gothic Std", Font.BOLD, 12));
		    changeItem.setForeground(new Color(42,195,207));
		    changeItem.addActionListener(this);
		    
		    activateRCM = new JButton("Activate/Deactivate");
		    activateRCM.setBorder(null);
		    activateRCM.setFont(new Font("Letter Gothic Std", Font.BOLD, 12));
		    activateRCM.setForeground(new Color(42,195,207));
		    activateRCM.addActionListener(this);
		    
		    loadMachine = new JButton("Load Machine");
		    loadMachine .setBorder(null);
		    loadMachine .setFont(new Font("Letter Gothic Std", Font.BOLD, 12));
		    loadMachine .setForeground(new Color(42,195,207));
		    loadMachine .addActionListener(this);
		    
		    EmptyMachine = new JButton("Empty Machine");
		    EmptyMachine .setBorder(null);
		    EmptyMachine .setFont(new Font("Letter Gothic Std", Font.BOLD, 12));
		    EmptyMachine .setForeground(new Color(42,195,207));
		    EmptyMachine .addActionListener(this);
		    
		    for (int i =0; i<separator.length;i++) {
			    separator[i]= new JLabel(" = ");
			    separator[i].setBorder(null);
			    separator[i].setFont(new Font("Letter Gothic Std", Font.BOLD, 12));
			    separator[i].setForeground(new Color(166,170,169));
		    }
			topMenuContainer = new Container();
			topMenuContainer.setLayout(new BoxLayout(topMenuContainer, BoxLayout.LINE_AXIS));
			topMenuContainer.add(newMachine);
			topMenuContainer.add(separator[0]);		
			topMenuContainer.add(removeMachine);
			topMenuContainer.add(separator[1]);		
			topMenuContainer.add(changeItem);
			topMenuContainer.add(separator[2]);		
			topMenuContainer.add(activateRCM);
			topMenuContainer.add(separator[3]);		
			topMenuContainer.add(loadMachine);
			topMenuContainer.add(separator[4]);
			topMenuContainer.add(EmptyMachine);
	}

		private void loadTable() {
			//JTable initialization
			Object[] newData = {0,"","","","","",""};
		    Object[][] data = {newData};
		    
		    // Preventing the user input
		    defTableModel = new DefaultTableModel(data,columnNames) {
		    	  public boolean isCellEditable(int row, int column) {
		    		  if(column >0&& column<4)
		    			  return true;
		    		  else
		    			  return false;
		    	    }
		    };
		    
		    //Instantiating the JTable
			table = new JTable(defTableModel);
			
			//Adding the selection handler
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
	        ListSelectionModel rowSM = table.getSelectionModel();
	        rowSM.addListSelectionListener(new ListSelectionListener() {
	            public void valueChanged(ListSelectionEvent e) {
	                if (e.getValueIsAdjusting()) return;
	                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	                if (lsm.isSelectionEmpty()) {
	                	selectedRcm = 0;
	                } else {
	                    selectedRcm = lsm.getMinSelectionIndex();
	                }
	            }
	        });
		    defTableModel.addTableModelListener(this);	    
		    defTableModel.removeRow(0);
		    
	        //Populating the table
		    for (int i =0; i< station.getMachines().size(); i++) {
		    	Object[] newData1 = {i,
		    			station.getMachine(i).location,
		    			station.getMachine(i).presentCapacity, 
		    			station.getMachine(i).money,
		    			station.getMachine(i).Status,
		    			station.getMachine(i).listOfItems.size(),
		    			station.getMachine(i).getLastEmptied()};
	    			defTableModel.addRow(newData1);	    	
		    }		
		    table.setDefaultRenderer(Object.class, new NumberCellRenderer());
	}
		
		public void actionPerformed(ActionEvent e) {
		       if(e.getSource() == newMachine) {
		    	   JTextField machineId,Location,capacity,Money;
					JLabel machineLabel ,LocationLabel , capacityLabel, MoneyLabel;
					//JButton Add, Cancel;
				    	   JPanel myPanel = new JPanel(new GridLayout(4,2));
				    	   LocationLabel = new JLabel("Location");
				    	   capacityLabel = new JLabel("Capacity");
				    	   MoneyLabel = new JLabel("Money");
				    	   Location = new JTextField();
				    	   capacity = new JTextField();
				    	   Money = new JTextField();
				    	   myPanel.add(LocationLabel);
				    	   myPanel.add(Location);
				    	   myPanel.add(capacityLabel);
				    	   myPanel.add(capacity);
				    	   myPanel.add(MoneyLabel);
				    	   myPanel.add(Money);
				    	   
				    	   int result = JOptionPane.showConfirmDialog(null, myPanel, 
					               "Create New RCM", JOptionPane.OK_CANCEL_OPTION);
				    	   if (result == JOptionPane.OK_OPTION) {
				    		   String s2 = Location.getText();
				    		   Double s3 = Double.valueOf(capacity.getText());
				    		   Double s4 = Double.valueOf(Money.getText());
					    	   station.addRCM(new RCM(s2, s3, s4));
					    	   int i =station.getMachines().size();
						    	Object[] newData1 = {i,
						    			s2,
						    			s3, 
						    			s4,
						    			"Disabled",
						    			0};
					    			defTableModel.addRow(newData1);
			    	   		}   
       					} else if(e.getSource() == removeMachine) {
				    	   station.removeRCM(station.getMachine(selectedRcm));
				    	   defTableModel.removeRow(selectedRcm);
       					}
						 else if(e.getSource() == changeItem) {
					    	   JPanel myPanel1 = new JPanel(new GridLayout(9,3));
					    	   JTextField [] listPrices= new JTextField[9];
					    	   JCheckBox [] checkItem = new JCheckBox[9]; 
					    	   int i=0,j =0;
					    	   //add all checkboxes
					    	   for (i=0; i < station.getAvailableItemTypes().length; i++) {
			    				   checkItem[i]= new JCheckBox(RMOS.getAvailableItemTypes()[i].itemType,false);
			    				   listPrices[i]=new JTextField(RMOS.getAvailableItemTypes()[i].price.toString());
					    	   }
					    	   
					    	   //check items/checkboxes already added
					    	   for (i=0; i < station.getAvailableItemTypes().length; i++) {
					    		   for(j=0; j< station.getMachine(selectedRcm).listOfItems.size();j++) {
					    			   if(station.getMachine(selectedRcm).listOfItems.get(j).getId()==RMOS.getAvailableItemTypes()[i].getId()) {
					    				   checkItem[i]= new JCheckBox(RMOS.getAvailableItemTypes()[i].itemType,true);
					    				   listPrices[i]=new JTextField(station.getMachine(selectedRcm).listOfItems.get(j).price.toString());
					    				   break;
					    			   }
					    			   else {
					    				   checkItem[i]= new JCheckBox(RMOS.getAvailableItemTypes()[i].itemType,false);
					    				   listPrices[i]=new JTextField(RMOS.getAvailableItemTypes()[i].price.toString());
					    			   }
					    		   }
					    		   
					    		   myPanel1.add(checkItem[i]);
					    		   myPanel1.add(listPrices[i]);
					    	   }
					    	   	   
					    	   int result1 = JOptionPane.showConfirmDialog(null, myPanel1, 
						               "Change RCM Items", JOptionPane.OK_CANCEL_OPTION);
					    	   if (result1 == JOptionPane.OK_OPTION) {
					    		   station.getMachine(selectedRcm).listOfItems.clear();
					    		   for (int i1=0; i1 < station.getAvailableItemTypes().length; i1++) {
						    			   if(checkItem[i1].isSelected()) {
						    				   station.getMachine(selectedRcm).addRecyclableItem(
						    						   new Item(checkItem[i1].getText(), 
						    						   Double.valueOf(listPrices[i1].getText()),i1));
						    			   }
					    		   		}
					    		   refreshTable();
					    	   }
					   }
						 else if(e.getSource() == activateRCM) {
							 if (station.getMachine(selectedRcm).Status=="Enabled")
								 station.getMachine(selectedRcm).Status="Disabled";
							 else
							 station.getMachine(selectedRcm).Status="Enabled";
				    		   refreshTable();

						 }
						 else if(e.getSource() == loadMachine) {
							 UserUI costumerUI = new UserUI(station.getMachine(selectedRcm));
							 costumerUI.load();
							 refreshGraphic();
						 }
						 else if(e.getSource() == EmptyMachine){
							 station.getMachine(selectedRcm).setLastEmptied(new Date());
							 station.getMachine(selectedRcm).presentCapacity=station.getMachine(selectedRcm).capacity;
							 refreshTable();
						 }
						 else if(e.getSource() == submitBt){
							 if(authenticate()==true) {
								 hideLoginPanel();
								 loadAdminPanel();
							 } else {
								 loginStatusL.setText("Try a valid user/passwd combination.");
							 }
						 }
						 else if(e.getSource() == RCMCB){
							 refreshInfo();
						 }
						 else if(e.getSource() == dayCB){
							 refreshInfo();
						 }
						 else if(e.getSource() == graphReportCB){
							 refreshInfo();
						 }
		       }
   
		
		
		private void hideLoginPanel() {
			loginContainer.setVisible(false);
			
		}
		
	static void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
		
        Scene scene = createBarGraphic(fxPanel.getName());
        fxPanel.setScene(scene);
    }

	private static Scene createBarGraphic(String name) {
		System.out.print(name);
    	final NumberAxis yAxis = new NumberAxis();
                
        final CategoryAxis xAxis = new CategoryAxis();

        final BarChart<String,Number> bc = 
            new BarChart<String,Number>(xAxis,yAxis);
        
        XYChart.Series series = new XYChart.Series();
        
	        for(int i=0; i<station.getMachines().size();i++) {
	        	if (name.equals("Weight")) {// weight is selected
	        		if(dayCB.getSelectedIndex()==0) {// All Time
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getWeightofMachine(365*10)));	        			
	        		}
	        		else if(dayCB.getSelectedIndex()==1) {// Day
		        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getWeightofMachine(1)));
        			}
	        		else if(dayCB.getSelectedIndex()==2) {// Week
		        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getWeightofMachine(7)));
	        		}
	        		else if(dayCB.getSelectedIndex()==3) {// Month
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getWeightofMachine(30)));
	        		}
	        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getWeightofMachine(numberOfDays)));
	        		}
	        	} else if (name.equals("Money")) {// money is selected
//	                XYChart.Series seriesc = new XYChart.Series();
	        		if(dayCB.getSelectedIndex()==0) {// All Time
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCash(365*10)));	        			
//	        			seriesc.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCoupons(365*10)));	        			
	        		}
	        		else if(dayCB.getSelectedIndex()==1) {// Day
		        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCash(1)));
//		        			seriesc.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCoupons(1)));	        			
        			}
	        		else if(dayCB.getSelectedIndex()==2) {// Week
		        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCash(7)));
//		        			seriesc.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCoupons(7)));	        			
	        		}
	        		else if(dayCB.getSelectedIndex()==3) {// Month
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCash(30)));
//	        			seriesc.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCoupons(30)));	        			
	        		}
	        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCash(numberOfDays)));
//	        			seriesc.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCoupons(numberOfDays)));	        			
	        		}
	        	} else if (name.equals("Item")) {//Items is selected
	        		if(dayCB.getSelectedIndex()==0) {// All Time
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfItems(365*10)));	        			
	        		}
	        		else if(dayCB.getSelectedIndex()==1) {// Day
		        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfItems(1)));
        			}
	        		else if(dayCB.getSelectedIndex()==2) {// Week
		        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfItems(7)));
	        		}
	        		else if(dayCB.getSelectedIndex()==3) {// Month
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfItems(30)));
	        		}
	        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfItems(numberOfDays)));
	        		}
	        	} else if (name.equals("Transaction")) {// Transactions is selected
	        		if(dayCB.getSelectedIndex()==0) {// All Time
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfTransaction(365*10)));	        			
	        		}
	        		else if(dayCB.getSelectedIndex()==1) {// Day
		        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfTransaction(1)));
        			}
	        		else if(dayCB.getSelectedIndex()==2) {// Week
		        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfTransaction(7)));
	        		}
	        		else if(dayCB.getSelectedIndex()==3) {// Month
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfTransaction(30)));
	        		}
	        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
	        			series.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfTransaction(numberOfDays)));
	        		}
        		}
	        }
        bc.getData().addAll(series);	
        Scene  scene  = new Scene(bc,750,150);
        scene.getStylesheets().add("chart.css");
        return (scene);
    }

	private static void updateWeightBG(JFXPanel fxPanel) {
        Scene scene = fxPanel.getScene();
    	NumberAxis yAxis1 = new NumberAxis();
        CategoryAxis xAxis1 = new CategoryAxis();
        BarChart<String,Number> bc1 = new BarChart(xAxis1, yAxis1);		
        XYChart.Series series1 = new XYChart.Series();
        
        for(int i=0; i<station.getMachines().size();i++) {
        	if (selectedTab==0) {// weight is selected
				if (RCMCB.getSelectedIndex()==0){// All Machines Stats
	
		        		if(dayCB.getSelectedIndex()==0) {// All Time
		        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getWeightofMachine(365*10)));	        			
		        		}
		        		else if(dayCB.getSelectedIndex()==1) {// Day
			        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getWeightofMachine(1)));
	        			}
		        		else if(dayCB.getSelectedIndex()==2) {// Week
			        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getWeightofMachine(7)));
		        		}
		        		else if(dayCB.getSelectedIndex()==3) {// Month
		        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getWeightofMachine(30)));
		        		}
		        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
		        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getWeightofMachine(numberOfDays)));
		        		}
	
				}
	        	else {// Selected machine
		        		if(dayCB.getSelectedIndex()==0) {// All Time
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
		        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
		        							station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 365*10)));
		        		}
		        		else if(dayCB.getSelectedIndex()==1) {// Day
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 1)));
	        			}
		        		else if(dayCB.getSelectedIndex()==2) {// Week
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 7)));
		        		}
		        		else if(dayCB.getSelectedIndex()==3) {// Month
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 30)));
		        		}
		        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, numberOfDays)));
		        		}
        		}
    		}
    	}
        bc1.getData().addAll(series1);
        scene.setRoot(bc1);
    }
	private static void updateMoneyBG(JFXPanel moneyfx) {
		   	Scene scene = moneyfx.getScene();
	    	NumberAxis yAxis1 = new NumberAxis();
	        CategoryAxis xAxis1 = new CategoryAxis();
	        BarChart<String,Number> bc1 = new BarChart(xAxis1, yAxis1);		
	        XYChart.Series series1 = new XYChart.Series();
	        
	        for(int i=0; i<station.getMachines().size();i++) {
					if (RCMCB.getSelectedIndex()==0){// All Machines Stats
		
			        		if(dayCB.getSelectedIndex()==0) {// All Time
			        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCash(365*10)+station.getMachine(i).getTotalValueOfCoupons(365*10)));
			        		}
			        		else if(dayCB.getSelectedIndex()==1) {// Day
				        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCash(1)+station.getMachine(i).getTotalValueOfCoupons(1)));
		        			}
			        		else if(dayCB.getSelectedIndex()==2) {// Week
				        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCash(7)+station.getMachine(i).getTotalValueOfCoupons(7)));
			        		}
			        		else if(dayCB.getSelectedIndex()==3) {// Month
			        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCash(30)+station.getMachine(i).getTotalValueOfCoupons(30)));
			        		}
			        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
			        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getTotalValueOfCash(numberOfDays)+station.getMachine(i).getTotalValueOfCoupons(numberOfDays)));
			        		}
		
					}
		        	else {// Selected machine
			        		if(dayCB.getSelectedIndex()==0) {// All Time
			        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
			        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
			        							station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 365*10)));
			        		}
			        		else if(dayCB.getSelectedIndex()==1) {// Day
			        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
		        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
	        							station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 1)));
		        			}
			        		else if(dayCB.getSelectedIndex()==2) {// Week
			        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
		        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
	        							station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 7)));
			        		}
			        		else if(dayCB.getSelectedIndex()==3) {// Month
			        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
		        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
	        							station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 30)));
			        		}
			        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
			        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
		        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
	        							station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, numberOfDays)));
			        		}
	        		}
	    		}
	        bc1.getData().addAll(series1);
	        scene.setRoot(bc1);


	}
	private static void updateItemsBG(JFXPanel itemsfx) {
   		Scene scene = itemsfx.getScene();
    	NumberAxis yAxis1 = new NumberAxis();
        CategoryAxis xAxis1 = new CategoryAxis();
        BarChart<String,Number> bc1 = new BarChart(xAxis1, yAxis1);		
        XYChart.Series series1 = new XYChart.Series();
        
        for(int i=0; i<station.getMachines().size();i++) {
				if (RCMCB.getSelectedIndex()==0){// All Machines Stats
	
		        		if(dayCB.getSelectedIndex()==0) {// All Time
		        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfItems(365*10)));
		        		}
		        		else if(dayCB.getSelectedIndex()==1) {// Day
			        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfItems(1)));
	        			}
		        		else if(dayCB.getSelectedIndex()==2) {// Week
			        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfItems(7)));
		        		}
		        		else if(dayCB.getSelectedIndex()==3) {// Month
		        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfItems(30)));
		        		}
		        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
		        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfItems(numberOfDays)));
		        		}
	
				}
	        	else {// Selected machine
		        		if(dayCB.getSelectedIndex()==0) {// All Time
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
		        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
		        							station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 365*10)));
		        		}
		        		else if(dayCB.getSelectedIndex()==1) {// Day
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 1)));
	        			}
		        		else if(dayCB.getSelectedIndex()==2) {// Week
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 7)));
		        		}
		        		else if(dayCB.getSelectedIndex()==3) {// Month
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 30)));
		        		}
		        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, numberOfDays)));
		        		}
        		}
    		}
        bc1.getData().addAll(series1);
        scene.setRoot(bc1);
	}

	private static void updateTransactionBG(JFXPanel transactionfx) {
   		Scene scene = transactionfx.getScene();
    	NumberAxis yAxis1 = new NumberAxis();
        CategoryAxis xAxis1 = new CategoryAxis();
        BarChart<String,Number> bc1 = new BarChart(xAxis1, yAxis1);		
        XYChart.Series series1 = new XYChart.Series();
        
        for(int i=0; i<station.getMachines().size();i++) {
				if (RCMCB.getSelectedIndex()==0){// All Machines Stats
	
		        		if(dayCB.getSelectedIndex()==0) {// All Time
		        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfTransaction(365*10)));
		        		}
		        		else if(dayCB.getSelectedIndex()==1) {// Day
			        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfTransaction(1)));
	        			}
		        		else if(dayCB.getSelectedIndex()==2) {// Week
			        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfTransaction(7)));
		        		}
		        		else if(dayCB.getSelectedIndex()==3) {// Month
		        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfTransaction(30)));
		        		}
		        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
		        			series1.getData().add(new XYChart.Data(station.getMachine(i).location,station.getMachine(i).getNumberOfTransaction(numberOfDays)));
		        		}
	
				}
	        	else {// Selected machine
		        		if(dayCB.getSelectedIndex()==0) {// All Time
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
		        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
		        							station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 365*10)));
		        		}
		        		else if(dayCB.getSelectedIndex()==1) {// Day
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 1)));
	        			}
		        		else if(dayCB.getSelectedIndex()==2) {// Week
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 7)));
		        		}
		        		else if(dayCB.getSelectedIndex()==3) {// Month
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 30)));
		        		}
		        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
		        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++)  
	        					series1.getData().add(new XYChart.Data(station.getAvailableItemTypes()[i1].itemType,
        							station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, numberOfDays)));
		        		}
        		}
    		}
        bc1.getData().addAll(series1);
        scene.setRoot(bc1);		
	}


		public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
	    int column = e.getColumn();
	    if(e.getType()==e.UPDATE){
	        String columnName = table.getColumnName(column);
	        Object data = table.getValueAt(row, column);
	        if (columnName =="Location") {
	        	station.getMachine(selectedRcm).location = (String) data;
	        	System.out.print("= Location changed to "+(String) data);
	        }
	        if (columnName == "Capacity") {
	        	station.getMachine(selectedRcm).capacity = Double.parseDouble((String) data);
	        	station.getMachine(selectedRcm).presentCapacity = Double.parseDouble((String) data);
	        	System.out.print("\n= Capacity changed to "+(String) data);
	        }
	        if (columnName == "$ Available") {
	        	station.getMachine(selectedRcm).money = Double.parseDouble((String) data);
	        	System.out.print("\n= $ available changed to "+(String) data);
	        }
	        
	    } else if (e.getType()==e.DELETE) {
	    	
	    }
	
	}
		public JButton loadImageBtn(String path) {
        try {
           String pressedPath = "./img/buttons/click/"+ path.substring(14,path.length());
           String disabledPath = "./img/buttons/disabled/"+ path.substring(14,path.length());
           BufferedImage enabled = ImageIO.read(new File(path));
           BufferedImage pressed = ImageIO.read(new File(pressedPath));
           BufferedImage disabled = ImageIO.read(new File(disabledPath));
           ImageIcon enabledIcon = new ImageIcon(enabled);
           ImageIcon pressedIcon = new ImageIcon(pressed);
           ImageIcon disabledIcon = new ImageIcon(disabled);
           JButton btn = new JButton(enabledIcon);
           btn.setPressedIcon(pressedIcon);
           btn.setDisabledIcon(disabledIcon);
           btn.setBorder(null);
           return btn;
        } catch (IOException e) {
           e.printStackTrace();
           return null;
   		}
     }
		public JLabel loadImage(String path) {
        try {
           BufferedImage img = ImageIO.read(new File(path));
           ImageIcon icon = new ImageIcon(img);
           JLabel imgLabel = new JLabel(icon);
           return imgLabel;
        } catch (IOException e) {
           e.printStackTrace();
           return null;
   		}
     }
		public static void refreshTable(){
			//defTableModel.removeRow(selectedRcm);
		    for (int i =0; i< station.getMachines().size(); i++) {
		    	defTableModel.removeRow(defTableModel.getRowCount()-1);
		    }
		    for (int i =0; i< station.getMachines().size(); i++) {
		    	Object[] newData1 = {i,
		    			station.getMachine(i).location,
		    			station.getMachine(i).presentCapacity, 
		    			station.getMachine(i).money,
		    			station.getMachine(i).Status,
		    			station.getMachine(i).listOfItems.size(),
		    			station.getMachine(i).lastEmptied};
	    			defTableModel.addRow(newData1);	    	
		    }
		}

		public void refreshInfo(){
			 if (graphReportCB.getSelectedIndex()==0) {
				 	refreshGraphic();
			 }
			 else {
				 	refreshStatistics();
			 }
		}
		
		public static void refreshGraphic(){
			System.out.print("= Refreshing graphic\n");

    		ActionListener task = new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
		        	if(selectedTab==0) {
		        		AdminUI.weightSP.setVisible(false);
		        		AdminUI.weightFX.setVisible(true);
		    			Platform.runLater(new Runnable() {
		    				public void run() {
		    					updateWeightBG(weightFX);
		    				}
	    		        });
		        	} else if(selectedTab==1) {
		        		AdminUI.moneySP.setVisible(false);
		        		AdminUI.moneyFX.setVisible(true);
		    			Platform.runLater(new Runnable() {
		    				public void run() {
		    					updateMoneyBG(moneyFX);
		    				}
	    		        });
		        	} else if(selectedTab==2) {
		        		AdminUI.itemsSP.setVisible(false);
		        		AdminUI.itemsFX.setVisible(true);
		    			Platform.runLater(new Runnable() {
		    				public void run() {
		    					updateItemsBG(itemsFX);
		    				}
	    		        });
		        	} else if(selectedTab==3) {
		        		AdminUI.transactionsSP.setVisible(false);
		        		AdminUI.transactionFX.setVisible(true);
		    			Platform.runLater(new Runnable() {
		    				public void run() {
		    					updateTransactionBG(transactionFX);
		    				}
	    		        });
		        	}
    			}
	        };
	        Timer timer = new Timer(2000, task); //fire every half second
	        timer.setRepeats(false);
	        timer.start();

		}
		public void refreshStatistics () {
			System.out.print("= Refreshing stats\n");
        	if(selectedTab==0) {
        		AdminUI.weightSP.setVisible(true);
        		AdminUI.weightFX.setVisible(false);
        		refreshWeightStats();
        	}else if(selectedTab==1) {
           		AdminUI.moneySP.setVisible(true);
        		AdminUI.moneyFX.setVisible(false);
        		refreshMoneyStats();
 			}else if(selectedTab==2) {
 	       		AdminUI.itemsSP.setVisible(true);
        		AdminUI.itemsFX.setVisible(false);
        		refreshItemsStats();
 			}else if(selectedTab==3) {
 	       		AdminUI.transactionsSP.setVisible(true);
        		AdminUI.transactionFX.setVisible(false);
        		refreshTransactionsStats();
 			}
//
		}
		private void refreshWeightStats() {
			if (RCMCB.getSelectedIndex()==0){
			weightTA.setText("--------------------------------------------\n"
					+ "All RCMs Statistics\n"
					+ "--------------------------------------------\n");
    		if(dayCB.getSelectedIndex()==0) {// All Time
    			weightTA.setText(weightTA.getText()+"Period: All Time\n"
    					+ "--------------------------------------------\n");
    			for(int i1=0; i1<station.getMachines().size();i1++) 
        			weightTA.setText(weightTA.getText()+String.format("%.2f",station.getMachine(i1).getWeightofMachine(365*10))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
    			weightTA.setText(weightTA.getText()+ "--------------------------------------------\n");
    			}
    		else if(dayCB.getSelectedIndex()==1) {// Day
        			weightTA.setText(weightTA.getText()+"Period: Day\n"
        					+ "--------------------------------------------\n");
        			for(int i1=0; i1<station.getMachines().size();i1++) 
            			weightTA.setText(weightTA.getText()+String.format("%.2f",station.getMachine(i1).getWeightofMachine(1))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
        			weightTA.setText(weightTA.getText()+ "--------------------------------------------\n");
			}
    		else if(dayCB.getSelectedIndex()==2) {// Week
    			weightTA.setText(weightTA.getText()+"Period: Week\n"
    					+ "--------------------------------------------\n");
    			for(int i1=0; i1<station.getMachines().size();i1++) 
        			weightTA.setText(weightTA.getText()+String.format("%.2f",station.getMachine(i1).getWeightofMachine(7))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
    			weightTA.setText(weightTA.getText()+ "--------------------------------------------\n");
    		}
    		else if(dayCB.getSelectedIndex()==3) {// Month
    			weightTA.setText(weightTA.getText()+"Period: Month\n"
    					+ "--------------------------------------------\n");
    			for(int i1=0; i1<station.getMachines().size();i1++) 
        			weightTA.setText(weightTA.getText()+String.format("%.2f",station.getMachine(i1).getWeightofMachine(30))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
    			weightTA.setText(weightTA.getText()+ "--------------------------------------------\n");
    		}
    		else if(dayCB.getSelectedIndex()==4) {// Number of Days
    			weightTA.setText(weightTA.getText()+"Period: Number of Day\n"
    					+ "--------------------------------------------\n");
    			for(int i1=0; i1<station.getMachines().size();i1++) 
        			weightTA.setText(weightTA.getText()+String.format("%.2f",station.getMachine(i1).getWeightofMachine(numberOfDays))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
    			weightTA.setText(weightTA.getText()+ "--------------------------------------------\n");
    		}
			
			
		} else {//Selected RCM
			weightTA.setText("--------------------------------------------\n"
					+ station.getMachine(selectedRcm).location+" Statistics\n"
					+ "--------------------------------------------\n");
    		if(dayCB.getSelectedIndex()==0) {// All Time
    			weightTA.setText(weightTA.getText()+"Period: All Time\n"
    					+ "--------------------------------------------\n");
    			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++) 
    				if (station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 365*10) >0.0)
    					weightTA.setText(weightTA.getText()+String.format("%.2f",station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 365*10))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
    			weightTA.setText(weightTA.getText()+ "--------------------------------------------\n");
    			}
    		else if(dayCB.getSelectedIndex()==1) {// Day
        			weightTA.setText(weightTA.getText()+"Period: Day\n"
        					+ "--------------------------------------------\n");
        			for(int i1=0; i1<station.getMachines().size();i1++) 
        				if (station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 1) >0.0)
        					weightTA.setText(weightTA.getText()+String.format("%.2f",station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 1))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
        			weightTA.setText(weightTA.getText()+ "--------------------------------------------\n");
			}
    		else if(dayCB.getSelectedIndex()==2) {// Week
    			weightTA.setText(weightTA.getText()+"Period: Week\n"
    					+ "--------------------------------------------\n");
    			for(int i1=0; i1<station.getMachines().size();i1++) 
    				if (station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 7) >0.0)
    					weightTA.setText(weightTA.getText()+String.format("%.2f",station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 7))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
    				weightTA.setText(weightTA.getText()+ "--------------------------------------------\n");
    		}
    		else if(dayCB.getSelectedIndex()==3) {// Month
    			weightTA.setText(weightTA.getText()+"Period: Month\n"
    					+ "--------------------------------------------\n");
    			for(int i1=0; i1<station.getMachines().size();i1++) 
    				if (station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 30) >0.0)
    					weightTA.setText(weightTA.getText()+String.format("%.2f",station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, 30))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
    			weightTA.setText(weightTA.getText()+ "--------------------------------------------\n");
    		}
    		else if(dayCB.getSelectedIndex()==4) {// Number of Days
    			weightTA.setText(weightTA.getText()+"Period: Number of Day\n"
    					+ "--------------------------------------------\n");
    			for(int i1=0; i1<station.getMachines().size();i1++) 
    				if (station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, numberOfDays) >0.0)
    					weightTA.setText(weightTA.getText()+String.format("%.2f",station.getMachine(selectedRcm).getTotalWeightOfItem(station.getAvailableItemTypes()[i1].itemType, numberOfDays))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
    			weightTA.setText(weightTA.getText()+ "--------------------------------------------\n");
    		}
			
		}
    
			}
		private void refreshMoneyStats() {
			if (RCMCB.getSelectedIndex()==0){
				moneyTA.setText("--------------------------------------------\n"
						+ "All RCMs Statistics\n"
						+ "--------------------------------------------\n");
        		if(dayCB.getSelectedIndex()==0) {// All Time
        			moneyTA.setText(moneyTA.getText()+"Period: All Time (Value in Money + Value in Coupon)\n"
        					+ "--------------------------------------------\n");
        			for(int i1=0; i1<station.getMachines().size();i1++) 
            			moneyTA.setText(moneyTA.getText()+String.format("%.2f + %.2f = %.2f",station.getMachine(i1).getTotalValueOfCash(365*10),station.getMachine(i1).getTotalValueOfCoupons(365*10),station.getMachine(i1).getTotalValueOfCash(365*10)+station.getMachine(i1).getTotalValueOfCoupons(365*10))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
        			moneyTA.setText(moneyTA.getText()+ "--------------------------------------------\n");
        			}
        		else if(dayCB.getSelectedIndex()==1) {// Day
            			moneyTA.setText(moneyTA.getText()+"Period: Day\n"
            					+ "--------------------------------------------\n");
            			for(int i1=0; i1<station.getMachines().size();i1++) 
                			moneyTA.setText(moneyTA.getText()+String.format("%.2f \t+\t %.2f \t=\t %.2f",station.getMachine(i1).getTotalValueOfCash(1),station.getMachine(i1).getTotalValueOfCoupons(1),station.getMachine(i1).getTotalValueOfCash(1)+station.getMachine(i1).getTotalValueOfCoupons(1))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
            			moneyTA.setText(moneyTA.getText()+ "--------------------------------------------\n");
    			}
        		else if(dayCB.getSelectedIndex()==2) {// Week
        			moneyTA.setText(moneyTA.getText()+"Period: Week\n"
        					+ "--------------------------------------------\n");
        			for(int i1=0; i1<station.getMachines().size();i1++) 
            			moneyTA.setText(moneyTA.getText()+String.format("%.2f \t+\t %.2f \t=\t %.2f",station.getMachine(i1).getTotalValueOfCash(7),station.getMachine(i1).getTotalValueOfCoupons(7),station.getMachine(i1).getTotalValueOfCash(7)+station.getMachine(i1).getTotalValueOfCoupons(7))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
        			moneyTA.setText(moneyTA.getText()+ "--------------------------------------------\n");
        		}
        		else if(dayCB.getSelectedIndex()==3) {// Month
        			moneyTA.setText(moneyTA.getText()+"Period: Month\n"
        					+ "--------------------------------------------\n");
        			for(int i1=0; i1<station.getMachines().size();i1++) 
            			moneyTA.setText(moneyTA.getText()+String.format("%.2f \t+\t %.2f \t=\t %.2f",station.getMachine(i1).getTotalValueOfCash(30),station.getMachine(i1).getTotalValueOfCoupons(30),station.getMachine(i1).getTotalValueOfCash(30)+station.getMachine(i1).getTotalValueOfCoupons(30))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
        			moneyTA.setText(moneyTA.getText()+ "--------------------------------------------\n");
        		}
        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
        			moneyTA.setText(moneyTA.getText()+"Period: Number of Day\n"
        					+ "--------------------------------------------\n");
        			for(int i1=0; i1<station.getMachines().size();i1++) 
            			moneyTA.setText(moneyTA.getText()+String.format("%.2f \t+\t %.2f \t=\t %.2f",station.getMachine(i1).getTotalValueOfCash(numberOfDays),station.getMachine(i1).getTotalValueOfCoupons(numberOfDays),station.getMachine(i1).getTotalValueOfCash(numberOfDays)+station.getMachine(i1).getTotalValueOfCoupons(numberOfDays))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
        			moneyTA.setText(moneyTA.getText()+ "--------------------------------------------\n");
        		}
				
				
			} else {//Selected RCM
				moneyTA.setText("--------------------------------------------\n"
						+ station.getMachine(selectedRcm).location+" Statistics\n"
						+ "--------------------------------------------\n");
        		if(dayCB.getSelectedIndex()==0) {// All Time
        			moneyTA.setText(moneyTA.getText()+"Period: All Time\n"
        					+ "--------------------------------------------\n");
        			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++) 
        				if (station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 365*10) >0.0)
        					moneyTA.setText(moneyTA.getText()+String.format("%.2f",station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 365*10))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
        			moneyTA.setText(moneyTA.getText()+ "--------------------------------------------\n");
        			}
        		else if(dayCB.getSelectedIndex()==1) {// Day
            			moneyTA.setText(moneyTA.getText()+"Period: Day\n"
            					+ "--------------------------------------------\n");
            			for(int i1=0; i1<station.getMachines().size();i1++) 
            				if (station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 1) >0.0)
            					moneyTA.setText(moneyTA.getText()+String.format("%.2f",station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 1))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
            			moneyTA.setText(moneyTA.getText()+ "--------------------------------------------\n");
    			}
        		else if(dayCB.getSelectedIndex()==2) {// Week
        			moneyTA.setText(moneyTA.getText()+"Period: Week\n"
        					+ "--------------------------------------------\n");
        			for(int i1=0; i1<station.getMachines().size();i1++) 
        				if (station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 7) >0.0)
        					moneyTA.setText(moneyTA.getText()+String.format("%.2f",station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 7))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
        				moneyTA.setText(moneyTA.getText()+ "--------------------------------------------\n");
        		}
        		else if(dayCB.getSelectedIndex()==3) {// Month
        			moneyTA.setText(moneyTA.getText()+"Period: Month\n"
        					+ "--------------------------------------------\n");
        			for(int i1=0; i1<station.getMachines().size();i1++) 
        				if (station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 30) >0.0)
        					moneyTA.setText(moneyTA.getText()+String.format("%.2f",station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, 30))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
        			moneyTA.setText(moneyTA.getText()+ "--------------------------------------------\n");
        		}
        		else if(dayCB.getSelectedIndex()==4) {// Number of Days
        			moneyTA.setText(moneyTA.getText()+"Period: Number of Day\n"
        					+ "--------------------------------------------\n");
        			for(int i1=0; i1<station.getMachines().size();i1++) 
        				if (station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, numberOfDays) >0.0)
        					moneyTA.setText(moneyTA.getText()+String.format("%.2f",station.getMachine(selectedRcm).getTotalMoneyOfItem(station.getAvailableItemTypes()[i1].itemType, numberOfDays))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
        			moneyTA.setText(moneyTA.getText()+ "--------------------------------------------\n");
        		}
				
			}
        }
		
		private void refreshItemsStats() {
			if (RCMCB.getSelectedIndex()==0){
				itemsTA.setText("--------------------------------------------\n"
						+ "All RCMs Statistics\n"
						+ "--------------------------------------------\n");
	    		if(dayCB.getSelectedIndex()==0) {// All Time
	    			itemsTA.setText(itemsTA.getText()+"Period: All Time\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	        			itemsTA.setText(itemsTA.getText()+String.format("%d",station.getMachine(i1).getNumberOfItems(365*10))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
	    			itemsTA.setText(itemsTA.getText()+ "--------------------------------------------\n");
	    			}
	    		else if(dayCB.getSelectedIndex()==1) {// Day
	        			itemsTA.setText(itemsTA.getText()+"Period: Day\n"
	        					+ "--------------------------------------------\n");
	        			for(int i1=0; i1<station.getMachines().size();i1++) 
	            			itemsTA.setText(itemsTA.getText()+String.format("%d",station.getMachine(i1).getNumberOfItems(1))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
	        			itemsTA.setText(itemsTA.getText()+ "--------------------------------------------\n");
				}
	    		else if(dayCB.getSelectedIndex()==2) {// Week
	    			itemsTA.setText(itemsTA.getText()+"Period: Week\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	        			itemsTA.setText(itemsTA.getText()+String.format("%d",station.getMachine(i1).getNumberOfItems(7))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
	    			itemsTA.setText(itemsTA.getText()+ "--------------------------------------------\n");
	    		}
	    		else if(dayCB.getSelectedIndex()==3) {// Month
	    			itemsTA.setText(itemsTA.getText()+"Period: Month\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	        			itemsTA.setText(itemsTA.getText()+String.format("%d",station.getMachine(i1).getNumberOfItems(30))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
	    			itemsTA.setText(itemsTA.getText()+ "--------------------------------------------\n");
	    		}
	    		else if(dayCB.getSelectedIndex()==4) {// Number of Days
	    			itemsTA.setText(itemsTA.getText()+"Period: Number of Day\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	        			itemsTA.setText(itemsTA.getText()+String.format("%d",station.getMachine(i1).getNumberOfItems(numberOfDays))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
	    			itemsTA.setText(itemsTA.getText()+ "--------------------------------------------\n");
	    		}
				
				
			} else {//Selected RCM
				itemsTA.setText("--------------------------------------------\n"
						+ station.getMachine(selectedRcm).location+" Statistics\n"
						+ "--------------------------------------------\n");
	    		if(dayCB.getSelectedIndex()==0) {// All Time
	    			itemsTA.setText(itemsTA.getText()+"Period: All Time\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++) 
	    				if (station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 365*10) >0.0)
	    					itemsTA.setText(itemsTA.getText()+String.format("%d",station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 365*10))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
	    			itemsTA.setText(itemsTA.getText()+ "--------------------------------------------\n");
	    			}
	    		else if(dayCB.getSelectedIndex()==1) {// Day
	        			itemsTA.setText(itemsTA.getText()+"Period: Day\n"
	        					+ "--------------------------------------------\n");
	        			for(int i1=0; i1<station.getMachines().size();i1++) 
	        				if (station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 1) >0.0)
	        					itemsTA.setText(itemsTA.getText()+String.format("%d",station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 1))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
	        			itemsTA.setText(itemsTA.getText()+ "--------------------------------------------\n");
				}
	    		else if(dayCB.getSelectedIndex()==2) {// Week
	    			itemsTA.setText(itemsTA.getText()+"Period: Week\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	    				if (station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 7) >0.0)
	    					itemsTA.setText(itemsTA.getText()+String.format("%d",station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 7))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
	    				itemsTA.setText(itemsTA.getText()+ "--------------------------------------------\n");
	    		}
	    		else if(dayCB.getSelectedIndex()==3) {// Month
	    			itemsTA.setText(itemsTA.getText()+"Period: Month\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	    				if (station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 30) >0.0)
	    					itemsTA.setText(itemsTA.getText()+String.format("%d",station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, 30))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
	    			itemsTA.setText(itemsTA.getText()+ "--------------------------------------------\n");
	    		}
	    		else if(dayCB.getSelectedIndex()==4) {// Number of Days
	    			itemsTA.setText(itemsTA.getText()+"Period: Number of Day\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	    				if (station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, numberOfDays) >0.0)
	    					itemsTA.setText(itemsTA.getText()+String.format("%d",station.getMachine(selectedRcm).getTotalItemsOfType(station.getAvailableItemTypes()[i1].itemType, numberOfDays))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
	    			itemsTA.setText(itemsTA.getText()+ "--------------------------------------------\n");
	    		}
				
			}			
		}
		private void refreshTransactionsStats() {
			if (RCMCB.getSelectedIndex()==0){
				transactionsTA.setText("--------------------------------------------\n"
						+ "All RCMs Statistics\n"
						+ "--------------------------------------------\n");
	    		if(dayCB.getSelectedIndex()==0) {// All Time
	    			transactionsTA.setText(transactionsTA.getText()+"Period: All Time\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	        			transactionsTA.setText(transactionsTA.getText()+String.format("%d",station.getMachine(i1).getNumberOfTransaction(365*10))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
	    			transactionsTA.setText(transactionsTA.getText()+ "--------------------------------------------\n");
	    			}
	    		else if(dayCB.getSelectedIndex()==1) {// Day
	        			transactionsTA.setText(transactionsTA.getText()+"Period: Day\n"
	        					+ "--------------------------------------------\n");
	        			for(int i1=0; i1<station.getMachines().size();i1++) 
	            			transactionsTA.setText(transactionsTA.getText()+String.format("%d",station.getMachine(i1).getNumberOfTransaction(1))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
	        			transactionsTA.setText(transactionsTA.getText()+ "--------------------------------------------\n");
				}
	    		else if(dayCB.getSelectedIndex()==2) {// Week
	    			transactionsTA.setText(transactionsTA.getText()+"Period: Week\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	        			transactionsTA.setText(transactionsTA.getText()+String.format("%d",station.getMachine(i1).getNumberOfTransaction(7))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
	    			transactionsTA.setText(transactionsTA.getText()+ "--------------------------------------------\n");
	    		}
	    		else if(dayCB.getSelectedIndex()==3) {// Month
	    			transactionsTA.setText(transactionsTA.getText()+"Period: Month\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	        			transactionsTA.setText(transactionsTA.getText()+String.format("%d",station.getMachine(i1).getNumberOfTransaction(30))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
	    			transactionsTA.setText(transactionsTA.getText()+ "--------------------------------------------\n");
	    		}
	    		else if(dayCB.getSelectedIndex()==4) {// Number of Days
	    			transactionsTA.setText(transactionsTA.getText()+"Period: Number of Day\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	        			transactionsTA.setText(transactionsTA.getText()+String.format("%d",station.getMachine(i1).getNumberOfTransaction(numberOfDays))+"\t\t\t\t "+station.getMachine(i1).location+"\n");	        			
	    			transactionsTA.setText(transactionsTA.getText()+ "--------------------------------------------\n");
	    		}
				
				
			} else {//Selected RCM
				transactionsTA.setText("--------------------------------------------\n"
						+ station.getMachine(selectedRcm).location+" Statistics\n"
						+ "--------------------------------------------\n");
	    		if(dayCB.getSelectedIndex()==0) {// All Time
	    			transactionsTA.setText(transactionsTA.getText()+"Period: All Time\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getAvailableItemTypes().length;i1++) 
	    				if (station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 365*10) >0.0)
	    					transactionsTA.setText(transactionsTA.getText()+String.format("%d",station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 365*10))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
	    			transactionsTA.setText(transactionsTA.getText()+ "--------------------------------------------\n");
	    			}
	    		else if(dayCB.getSelectedIndex()==1) {// Day
	        			transactionsTA.setText(transactionsTA.getText()+"Period: Day\n"
	        					+ "--------------------------------------------\n");
	        			for(int i1=0; i1<station.getMachines().size();i1++) 
	        				if (station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 1) >0.0)
	        					transactionsTA.setText(transactionsTA.getText()+String.format("%d",station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 1))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
	        			transactionsTA.setText(transactionsTA.getText()+ "--------------------------------------------\n");
				}
	    		else if(dayCB.getSelectedIndex()==2) {// Week
	    			transactionsTA.setText(transactionsTA.getText()+"Period: Week\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	    				if (station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 7) >0.0)
	    					transactionsTA.setText(transactionsTA.getText()+String.format("%d",station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 7))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
	    				transactionsTA.setText(transactionsTA.getText()+ "--------------------------------------------\n");
	    		}
	    		else if(dayCB.getSelectedIndex()==3) {// Month
	    			transactionsTA.setText(transactionsTA.getText()+"Period: Month\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	    				if (station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 30) >0.0)
	    					transactionsTA.setText(transactionsTA.getText()+String.format("%d",station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, 30))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
	    			transactionsTA.setText(transactionsTA.getText()+ "--------------------------------------------\n");
	    		}
	    		else if(dayCB.getSelectedIndex()==4) {// Number of Days
	    			transactionsTA.setText(transactionsTA.getText()+"Period: Number of Day\n"
	    					+ "--------------------------------------------\n");
	    			for(int i1=0; i1<station.getMachines().size();i1++) 
	    				if (station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, numberOfDays) >0.0)
	    					transactionsTA.setText(transactionsTA.getText()+String.format("%d",station.getMachine(selectedRcm).getTotalTransactionsWithType(station.getAvailableItemTypes()[i1].itemType, numberOfDays))+"\t\t\t\t\t"+station.getAvailableItemTypes()[i1].itemType+"\n");
	    			transactionsTA.setText(transactionsTA.getText()+ "--------------------------------------------\n");
	    		}
				
			}
		}

		public void createAndShowGUI() {
		
		    //Create and set up the window.
		    frame = new JFrame("RMOS");
		    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		    //Add content to the window.
		    frame.add(new AdminUI(station));
		
		    //Display the window.
		    frame.setBounds(200, 200, 800, 600);
		    frame.setVisible(true);
		    frame.addWindowListener(this);
		    
		}
		@Override
		public void windowClosing(WindowEvent e) {
			System.out.print("Closing Window.\n");
		        ActionListener task1 = new ActionListener() {
		            boolean alreadyDisposed = false;
		            public void actionPerformed(ActionEvent e) {
		                if (frame.isDisplayable()) {
		                    alreadyDisposed = true;
		                    frame.dispose();
		                }
		            }
		        };
		        Timer timer2 = new Timer(500, task1); //fire every half second
		        timer2.setRepeats(false);
		        timer2.start();
		      //serialize the List
		        try (
		          OutputStream file = new FileOutputStream("data.ser");
		          OutputStream buffer = new BufferedOutputStream(file);
		          ObjectOutput output = new ObjectOutputStream(buffer);
		        ){
		          output.writeObject(station);
		        }  
		        catch(IOException ex){
		          fLogger.log(Level.SEVERE, "Cannot perform output.", ex);
		        }
		
		}
		@Override
		public void windowOpened(WindowEvent e) {}
		@Override
		public void windowClosed(WindowEvent e) {}
		@Override
		public void windowIconified(WindowEvent e) {}
		@Override
		public void windowDeiconified(WindowEvent e) {}
		@Override
		public void windowActivated(WindowEvent e) {}
		@Override
		public void windowDeactivated(WindowEvent e) {}	
		//Utility Class to format the table numbers properly
		public class NumberCellRenderer extends DefaultTableCellRenderer {
		    DecimalFormat numberFormat = new DecimalFormat("#,###.##;(#,###.##)");

		    @Override
		    public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		        Component c = super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);
		        if (c instanceof JLabel && value instanceof Number) {
		            JLabel label = (JLabel) c;
		            Number num = (Number) value;
		            String text = numberFormat.format(num);
		            label.setText(text);
		        }
		        return c;
		    }
		}
}