package com.brick.framework;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.brick.framework.utility.ConfigKeys;
import com.brick.utilities.Config;

public class ServiceThreadManager {
	
	private static ServiceThreadManager instance;
	
	public static synchronized ServiceThreadManager getInstance() {
        if (instance == null) {
            instance = new ServiceThreadManager();
        }
        return instance;
	}
	
	private ThreadPoolExecutor threadPoolExecutor;
	
	private ServiceThreadManager() {
		int minThreads = Config.getConfigIntegerOrDefault(ConfigKeys.SERVICE_MIN_THREADS,  ConfigKeys.SERVICE_DEFAULT_MIN_THREADS);
		int maxThreads = Config.getConfigIntegerOrDefault(ConfigKeys.SERVICE_MAX_THREADS, ConfigKeys.SERVICE_DEFAULT_MAX_THREADS);
		int idleTimeout =  Config.getConfigIntegerOrDefault(ConfigKeys.SERVICE_TIMEOUT, ConfigKeys.SERVICE_DEFAULT_TIMEOUT);
		int queueLength = Config.getConfigIntegerOrDefault(ConfigKeys.SERVICE_QUEUE_LENGTH, ConfigKeys.SERVICE_DEFAULT_QUEUE_LENGTH);
				
		this.threadPoolExecutor = new ThreadPoolExecutor(
				minThreads,
				maxThreads,
				idleTimeout, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(queueLength),
				Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.AbortPolicy()
				);
	}
	
	public Future<Void> submitTask(Runnable r) {
		return this.threadPoolExecutor.submit(r,null);
	}

}
