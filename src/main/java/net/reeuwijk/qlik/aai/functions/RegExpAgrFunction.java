package net.reeuwijk.qlik.aai.functions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.reeuwijk.qlik.aai.BundledRows;
import net.reeuwijk.qlik.aai.DataType;
import net.reeuwijk.qlik.aai.Dual;
import net.reeuwijk.qlik.aai.FunctionType;
import net.reeuwijk.qlik.aai.Row;
import net.reeuwijk.qlik.aai.BundledRows.Builder;
import net.reeuwijk.qlik.aai.util.SSEFunction;

public class RegExpAgrFunction implements SSEFunction
{
	
	private StringBuffer str = new StringBuffer();
	
	private static final String FUNCTION_NAME = "RegExpAgr";

	public static String getParameters() {
		return "Pattern:String,Replacement:String,Text:String";
	}

	public static DataType getReturnType() {
		return DataType.STRING;
	}

	public static FunctionType getFunctionType() {
		return FunctionType.AGGREGATION;
	}

	public static String getName() {
		return FUNCTION_NAME;
	}

	public void executeRow(Row inRow) {
		String result = "";
		if (inRow.getDualsCount() == 3) {
			Pattern p = Pattern.compile((inRow.getDuals(0).getStrData()));
			Matcher m = p.matcher(inRow.getDuals(2).getStrData());
			if (m.matches()) {
				result = m.replaceAll(inRow.getDuals(1).getStrData());
			} else {
				result = inRow.getDuals(2).getStrData();
			}
		}
		if(str.length() > 0) str.append(",");
		str.append(result);
	}


	public BundledRows getReturnRows() {
		Builder returnRowsBuilder = BundledRows.newBuilder();
		returnRowsBuilder.addRows(Row.newBuilder().addDuals(Dual.newBuilder().setStrData(str.toString()).build()).build());
		return returnRowsBuilder.build();
	}
}
