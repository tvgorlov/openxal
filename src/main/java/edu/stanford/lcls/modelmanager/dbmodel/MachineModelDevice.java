package edu.stanford.lcls.modelmanager.dbmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MachineModelDevice {
	static final private List<String> PROPERTY_NAME;
	static final private int PROPERTY_SIZE;
	static private List<String> propertyType;
	private List<Object> propertyValue;
	
	/**
	 * Static initializer 
	 */
	static {
		PROPERTY_NAME = Arrays.asList(new String[] {
				"ELEMENT_NAME", "DEVICE_PROPERTY", "DEVICE_VALUE", "ZPOS" });
		PROPERTY_SIZE = PROPERTY_NAME.size();
		propertyType = Arrays.asList(new String[PROPERTY_SIZE]);
	}
	
	/**
	 * Primary constructor
	 */	
	
	public MachineModelDevice(ArrayList<Object> propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	public MachineModelDevice(List<Object> propertyValue) {
		this.propertyValue = propertyValue;
	}

	public MachineModelDevice() {
		this(Arrays.asList(new Object[PROPERTY_SIZE]));
	}

	//About propertyName
	public static String getPropertyName(int index) {
		return PROPERTY_NAME.get(index);
	}

	public static List<String> getAllPropertyName() {
		return PROPERTY_NAME;
	}

	//About propertyType
	public static String getPropertyType(int index) {
		return propertyType.get(index);
	}

	public static String getPropertyType(String propertyName) {
		return getPropertyType(PROPERTY_NAME.indexOf(propertyName));
	}

	public static List<String> getAllPropertyType() {
		return propertyType;
	}
	
	public static void setPropertyType(int index, String propertyDBType, int propertyDBSize) {		
		if(propertyDBType.equals("NUMBER"))
			propertyType.set(index, "Double");
		else if(propertyDBType.equals("DATE"))
			propertyType.set(index, "Date");
		else if(propertyDBType.equals("VARCHAR2"))
			propertyType.set(index, "String");
		else
			propertyType.set(index, "Other");
	}

	public static void setPropertyType(int index, String propertyType) {
		MachineModelDevice.propertyType.set(index, propertyType);
	}

	public static void setPropertyType(String propertyName, String propertyType) {
		MachineModelDevice.propertyType.set(PROPERTY_NAME.indexOf(propertyName),
				propertyType);
	}

	//About PropertyValue
	public Object getPropertyValue(int index) {
		return propertyValue.get(index);
	}

	public Object getPropertyValue(String propertyName) {
		return getPropertyValue(PROPERTY_NAME.indexOf(propertyName));
	}

	public List<Object> getAllPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(int index, Object propertyValue) {
		this.propertyValue.set(index, propertyValue);
	}

	public void setPropertyValue(String propertyName, Object propertyValue) {
		this.propertyValue.set(PROPERTY_NAME.indexOf(propertyName),
				propertyValue);
	}

	//About propertySize
	public static int getPropertySize() {
		return PROPERTY_SIZE;
	}

}
