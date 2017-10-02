package net.reeuwijk.qlik.aai.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.stub.StreamObserver;
import net.reeuwijk.qlik.aai.BundledRows;
import net.reeuwijk.qlik.aai.Capabilities;
import net.reeuwijk.qlik.aai.ConnectorGrpc;
import net.reeuwijk.qlik.aai.DataType;
import net.reeuwijk.qlik.aai.FunctionDefinition;
import net.reeuwijk.qlik.aai.FunctionType;
import net.reeuwijk.qlik.aai.Parameter;
import net.reeuwijk.qlik.aai.RegexpServer;
import net.reeuwijk.qlik.aai.Row;

public class SSEServer extends ConnectorGrpc.ConnectorImplBase {

	private static final String PLUGIN_VERSION = "0.1";
	private static final String PLUGIN_ID = "aai-java-regexp";
	private ArrayList<Class<SSEFunction>> sseFunctions = new ArrayList<Class<SSEFunction>>();

	static final Logger logger = Logger.getLogger(RegexpServer.class.getName());

	public void registerFunction(Class<SSEFunction> sseFunction) {
		sseFunctions.add(sseFunction);
	}

	@Override
	public void getCapabilities(net.reeuwijk.qlik.aai.Empty request,
			io.grpc.stub.StreamObserver<net.reeuwijk.qlik.aai.Capabilities> responseObserver) {
		logger.info("Received getCapabilities");
		net.reeuwijk.qlik.aai.Capabilities.Builder capabilitiesBuilder = Capabilities.newBuilder();
		capabilitiesBuilder = capabilitiesBuilder.setAllowScript(false).setPluginVersion(PLUGIN_VERSION)
				.setPluginIdentifier(PLUGIN_ID);
		for (int i = 0; i < sseFunctions.size(); i++) {
			net.reeuwijk.qlik.aai.FunctionDefinition.Builder fb = FunctionDefinition.newBuilder();
			Class<SSEFunction> cls = sseFunctions.get(i);
			Method m;
			try {
				m = cls.getMethod("getName", new Class<?>[0]);
				fb = fb.setName((String) m.invoke(null, new Object[0]));
				m = cls.getMethod("getFunctionType", new Class<?>[0]);
				fb = fb.setFunctionType((FunctionType) m.invoke(null, new Object[0]));
				m = cls.getMethod("getReturnType", new Class<?>[0]);
				fb = fb.setReturnType((DataType) m.invoke(null, new Object[0])).setFunctionId(i);
				m = cls.getMethod("getParameters", new Class<?>[0]);
				String parameters = (String) m.invoke(null, new Object[0]);
				String[] parameter = parameters.split(",");
				for (int j = 0; j < parameter.length; j++) {
					String[] items = parameter[j].split(":");
					net.reeuwijk.qlik.aai.Parameter.Builder pb = Parameter.newBuilder();
					pb = pb.setName(items[0]);
					if (items[1].equals("String")) {
						pb = pb.setDataType(DataType.STRING);
					} else if (items[1].equals("Numeric")) {
						pb = pb.setDataType(DataType.NUMERIC);
					} else if (items[1].equals("Dual")) {
						pb = pb.setDataType(DataType.DUAL);
					} else {
						pb = pb.setDataType(DataType.UNRECOGNIZED);
					}
					fb = fb.addParams(pb.build());
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			capabilitiesBuilder = capabilitiesBuilder.addFunctions(fb.build());
		}
		responseObserver.onNext(capabilitiesBuilder.build());
		responseObserver.onCompleted();
	}

	public io.grpc.stub.StreamObserver<net.reeuwijk.qlik.aai.BundledRows> executeFunction(
			io.grpc.stub.StreamObserver<net.reeuwijk.qlik.aai.BundledRows> responseObserver) {

		int functionId = Constant.FUNCTION_HEADER.get().getFunctionId();

		Class<SSEFunction> cls = sseFunctions.get(functionId);
		SSEFunction sseFunction;
		try {
			sseFunction = cls.newInstance();
			logger.info("Executing function: "+sseFunction.getClass().getName());
			StreamObserver<BundledRows> response = new StreamObserver<BundledRows>() {
				@Override
				public void onNext(BundledRows value) {
					List<Row> inputRows = value.getRowsList();
					for (Row inRow : inputRows) {
						sseFunction.executeRow(inRow);
					}
					responseObserver.onNext(sseFunction.getReturnRows());
				}

				@Override
				public void onError(Throwable t) {
					logger.log(Level.WARNING, "Encountered Error somewhere", t);
				}

				@Override
				public void onCompleted() {
					responseObserver.onCompleted();
				}
			};
			return response;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseObserver;
	}
}