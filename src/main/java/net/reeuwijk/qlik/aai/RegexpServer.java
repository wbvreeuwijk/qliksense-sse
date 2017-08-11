package net.reeuwijk.qlik.aai;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.Reflection;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import net.reeuwijk.qlik.aai.util.SSEFunction;
import net.reeuwijk.qlik.aai.util.SSEHeaderInterceptor;
import net.reeuwijk.qlik.aai.util.SSEServer;

public class RegexpServer {

	private static final int GRPC_PORT = 50053;

	static final Logger logger = Logger.getLogger(RegexpServer.class.getName());

	private Server server;

	public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {
		final RegexpServer server = new RegexpServer();
		server.start();
		server.blockUntilShutdown();
	}

	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

	@SuppressWarnings("unchecked")
	private void start() throws IOException, ClassNotFoundException {
		SSEHeaderInterceptor headerInterceptor = new SSEHeaderInterceptor();
		SSEServer s = new SSEServer();
		ClassPath cp = ClassPath.from(ClassLoader.getSystemClassLoader());
		ImmutableSet<ClassInfo> classes = cp.getTopLevelClasses("net.reeuwijk.qlik.aai.functions");
		ImmutableList<ClassInfo> cs = classes.asList();
		for(int i = 0;  i < cs.size(); i++) {
			s.registerFunction((Class<SSEFunction>) Class.forName(cs.get(i).getName()));
		}
		server = ServerBuilder.forPort(GRPC_PORT)
				.addService(ServerInterceptors.intercept(s, headerInterceptor))
				// .addService(new RegexpImpl())
				.build().start();
		logger.info("Server started, listening on " + GRPC_PORT);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("*** Shutting down gRPC server since JVM is shutting down");
				RegexpServer.this.stop();
				System.err.println("*** Server shut down ***");
			}
		});

	}

	protected void stop() {
		if (server != null) {
			server.shutdown();
		}

	}

}
