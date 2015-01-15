package se.lu.esss.linaclego.elements;

import javax.xml.bind.annotation.XmlType;

import se.lu.esss.linaclego.BLEVisitor;

@XmlType(name = "bend")
public class Bend extends BeamlineElement {
	@Override
	public void accept(BLEVisitor visitor) {
		visitor.visit(this);
	}
	
	public double getBendAngle()
	{
		return getParameters().getDoubleValue("bendAngleDeg");
	}
	
	public double getCurvatureRadius()
	{
		return getParameters().getDoubleValue("radOfCurvmm");
	}
	
	public int getFieldIndex()
	{
		return getParameters().getIntValue("fieldIndex");
	}
	
	@Override
	public double getApertureR()
	{
		return getParameters().getDoubleValue("aperRadmm");
	}
	
	public int getHVFlag()
	{
		return getParameters().getIntValue("HVflag");
	}
}
