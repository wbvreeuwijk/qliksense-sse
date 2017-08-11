package net.reeuwijk.qlik.aai.util;

import java.net.reeuwijk.qlik.aai.CommonRequestHeader;
import java.net.reeuwijk.qlik.aai.FunctionRequestHeader;
import java.net.reeuwijk.qlik.aai.ScriptRequestHeader;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import net.reeuwijk.qlik.aai.RegexpServer;

public class SSEHeaderInterceptor implements ServerInterceptor {

	static final Logger logger = Logger.getLogger(RegexpServer.class.getName());

	@Override
	public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
			final Metadata requestHeaders, ServerCallHandler<ReqT, RespT> next) {
		logger.finer("header received from client:" + requestHeaders);
		Context ctx = Context.current();
		try {
			byte[] h;
			if ((h = requestHeaders.get(Constant.SCRIPT_REQUEST_HEADER)) != null) {
				ScriptRequestHeader scriptRequestHeader = ScriptRequestHeader.parseFrom(h);
				ctx = ctx.withValue(Constant.SCRIPT_HEADER,scriptRequestHeader);
			}
			if ((h = requestHeaders.get(Constant.FUNCTION_REQUEST_HEADER)) != null) {
				FunctionRequestHeader functionRequestHeader = FunctionRequestHeader.parseFrom(h);
				ctx = ctx.withValue(Constant.FUNCTION_HEADER,functionRequestHeader);
			}
			if ((h = requestHeaders.get(Constant.COMMON_REQUEST_HEADER)) != null) {
				CommonRequestHeader commonRequestHeader = CommonRequestHeader.parseFrom(h);
				ctx = ctx.withValue(Constant.COMMON_HEADER,commonRequestHeader);
			}
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Contexts.interceptCall(ctx, call, requestHeaders, next);
	}
}