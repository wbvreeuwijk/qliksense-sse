package net.reeuwijk.qlik.aai.util;

import java.util.logging.Logger;

import net.reeuwijk.qlik.aai.BundledRows;
import net.reeuwijk.qlik.aai.DataType;
import net.reeuwijk.qlik.aai.FunctionType;
import net.reeuwijk.qlik.aai.RegexpServer;
import net.reeuwijk.qlik.aai.Row;

public interface SSEFunction {
	
	static final Logger logger = Logger.getLogger(RegexpServer.class.getName());
	
	public static String getName() {
		return null;
	};
	
	public static String getParameters() {
		return null;
	};
	
	public static DataType getReturnType() {
		return DataType.UNRECOGNIZED;
	}
	public static FunctionType getFunctionType() {
		return FunctionType.UNRECOGNIZED;
	}

	public void executeRow(Row inRow);

	public BundledRows getReturnRows();
	
}
