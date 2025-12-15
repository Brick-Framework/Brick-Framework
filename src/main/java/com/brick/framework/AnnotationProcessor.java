package com.brick.framework;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.brick.framework.annotations.AutoIntialize;
import com.brick.framework.annotations.Identifier;
import com.brick.framework.annotations.Service;
import com.brick.framework.annotations.Validator;
import com.brick.framework.exception.CyclicAutoInitilizationReferenceFound;
import com.brick.framework.exception.DuplicateServiceFound;
import com.brick.framework.exception.DuplicateServiceIdFound;
import com.brick.framework.exception.DuplicateValidatorFound;
import com.brick.framework.exception.DuplicateValidatorIdFound;
import com.brick.framework.exception.InvalidServiceId;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.framework.exception.InvalidValidatorSigantature;
import com.brick.framework.exception.MultiplePublicConstructorFound;
import com.brick.framework.exception.NoPublicConstructorFound;
import com.brick.framework.utility.AnnotationUtils;
import com.brick.logger.Logger;

public class AnnotationProcessor {
	private String packageName;
	private Set<Class<?>> classSet;
	
	private Map<Class<?>,Object> autoInitializerMap;
	private Map<String,Method> validatorMap;
	private Map<String,Method> serviceMap;
	
	protected AnnotationProcessor(String packageName) throws ClassNotFoundException, IOException, DuplicateValidatorFound, InvalidValidatorSigantature, DuplicateValidatorIdFound, DuplicateServiceFound, DuplicateServiceIdFound, MultiplePublicConstructorFound, NoPublicConstructorFound, CyclicAutoInitilizationReferenceFound, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.packageName = packageName;
		this.autoInitializerMap = new HashMap<Class<?>, Object>();
		this.validatorMap = new HashMap<String, Method>();
		this.serviceMap = new HashMap<String, Method>();
		
		this.classSet = this.scanPackages();
		this.filterAndMap();
		this.createObjects();
	}
	
	/*
	 * Description: Create Objects for AutoInitizerMap
	 */
	private void createObjects() throws MultiplePublicConstructorFound, NoPublicConstructorFound, CyclicAutoInitilizationReferenceFound, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Logger.info("Creating Object for Auto Initilize Components");
		Map<Class<?>, List<Class<?>>> graph = this.createGraph();
		this.cyclicReferenceCheck(graph);
		
		Queue<Class<?>> queue = new LinkedList<Class<?>>();
		
		
		// Creating an Outdegree Map to Initialise Classes with Zero Dependency First, then other Initilize Other Classes
		Map<Class<?>, Integer> outdegreeMap = new HashMap<Class<?>, Integer>();
		for( Map.Entry<Class<?>, List<Class<?>>> entry : graph.entrySet() ) {
			outdegreeMap.put(entry.getKey(), entry.getValue().size());
			if( entry.getValue().isEmpty() ) {
				queue.offer( entry.getKey() );
			}
		}
		
		while( !queue.isEmpty() ) {
			Class<?> current = queue.poll();
			
			Constructor<?> constructor = current.getConstructors()[0];
			Class<?>[] parameters =  constructor.getParameterTypes();
			
			Object[] constructorArguments = new Object[parameters.length];
			
			for( int i = 0; i < parameters.length; i++ ) {
				constructorArguments[i] = this.autoInitializerMap.get(parameters[i]);
			}
			
			this.autoInitializerMap.put(current, constructor.newInstance(constructorArguments) );
			Logger.info("Object Created for :"+ current.getName());
			
			for( Map.Entry<Class<?>, List<Class<?>>> entry: graph.entrySet() ) {
				if( entry.getKey() == current ) {
					continue;
				}
				
				if( entry.getValue().contains(current) ) {
					entry.getValue().remove(current);
					
					if( entry.getValue().isEmpty() ) {
						queue.offer(entry.getKey());
					}
				}
			}
		}
	}
	
	/*
	 * Description: Does a Cyclic Reference Check on Graph
	 */
	private void cyclicReferenceCheck(Map<Class<?>, List<Class<?>>> graph) throws CyclicAutoInitilizationReferenceFound {
		//DFS for Cyclic Check
        Set<Class<?>> visited = new HashSet<>();
        Set<Class<?>> recursionStack = new HashSet<>();

        for( Class<?> node : graph.keySet() ){
            if( dfs(node,graph,visited,recursionStack) ){
                CyclicAutoInitilizationReferenceFound cyclicAutoInitilizationReferenceFound = new CyclicAutoInitilizationReferenceFound("Cyclic Reference Found in AutoInitilize Components");
                Logger.logException(cyclicAutoInitilizationReferenceFound);
                throw cyclicAutoInitilizationReferenceFound;
            }
        }
	}
	
	/*
	 * Description: Uses DFS Algorithm to detect cycle in graph
	 */
	private boolean dfs(Class<?> node, Map<Class<?>, List<Class<?>>> graph, Set<Class<?>> visited,Set<Class<?>> recursionStack){
	    if( recursionStack.contains(node) ){
	        return true;
	    }
	
	    if( visited.contains(node) ){
	        return false;
	    }
	
	    visited.add(node);
	    recursionStack.add(node);
	
	    for( Class<?> neighbor : graph.get(node) ){
	        if( dfs(neighbor,graph,visited,recursionStack) ){
	            return true;
	        }
	    }
	
	    recursionStack.remove(node);
	    return false;
	}
	
	/*
	 * Description: Create Graph for Object Creation
	 */
	private Map<Class<?>, List<Class<?>>> createGraph() throws MultiplePublicConstructorFound, NoPublicConstructorFound{
		Map<Class<?>, List<Class<?>>> graph = new HashMap<Class<?>, List<Class<?>>>();
		
		for( Class<?> c : this.autoInitializerMap.keySet() ) {
			Constructor<?>[] constructorList = c.getConstructors();
			
			if( constructorList.length == 0 ) {
				NoPublicConstructorFound noPublicConstructorFound = new NoPublicConstructorFound(c.getName());
				Logger.logException(noPublicConstructorFound);
				throw noPublicConstructorFound;
			}
			
			if( constructorList.length > 1 ) {
				MultiplePublicConstructorFound multiplePublicConstructorFound = new MultiplePublicConstructorFound( c.getName() );
				Logger.logException(multiplePublicConstructorFound);
				throw multiplePublicConstructorFound;
			}
			
			Constructor<?> constructor = constructorList[0];
			
			List<Class<?>> dependency = new ArrayList<Class<?>>();
			for( Class<?> param: constructor.getParameterTypes() ) {
				dependency.add(param);
			}
			
			graph.put(c, dependency );
		}
		
		return graph;
	}
	
	/*
	 * Description Scans Entire Package Recursively and returns a Set of All Classes
	 */
	private Set<Class<?>> scanPackages() throws IOException, ClassNotFoundException{
		Logger.info("Scanning Packages for Annotation Processing");
		
		Set<Class<?>> classes = new HashSet<>();
		
		// 1. Convert package name (com.example) to path (com/example)
        String path = this.packageName.replace('.', '/');
        
        // 2. Get the ClassLoader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        // 3. Find all resources matching this path (handles multiple jars/directories)
        Enumeration<URL> resources = classLoader.getResources(path);
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            
            if (resource.getProtocol().equals("file")) {
                // Handle file system (e.g., running in IDE)
                String filePath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8.name());
                findClassesInDirectory(new File(filePath), packageName, classes);
            } else if (resource.getProtocol().equals("jar")) {
                // Handle JAR file (e.g., running in production)
                findClassesInJar(resource, path, classes);
            }
        }
		
		Logger.info("Package Scan for Annotation Processing Completed");
		
		return classes;
	}
	
	/*
	 * Description: Filter Classes and Method based on annotation and get references
	 */
	public void filterAndMap() throws DuplicateValidatorFound, InvalidValidatorSigantature, DuplicateValidatorIdFound, DuplicateServiceFound, DuplicateServiceIdFound {
		Logger.info("Starting Filtering for Scanned Classes");
		
        Set<String> seenValidator = new HashSet<String>();
        Set<String> seenService = new HashSet<String>();
        for (Class<?> clazz : this.classSet) {
        	
        	// Adding Classes to AutoInitiliser Lists
        	if( AnnotationUtils.isAnnotationPresent(clazz, AutoIntialize.class) && !clazz.isAnnotation() ) {
        		this.autoInitializerMap.put(clazz,null);
        	}
        	
        	
        	// Checking for Validator Classes and Adding Validator with their id in map
        	if( AnnotationUtils.isAnnotationPresent(clazz, Validator.class) ) {
        		
        		Validator v = clazz.getAnnotation(Validator.class);
        		
        		if( seenValidator.contains( v.name() ) ) {
        			DuplicateValidatorFound duplicateValidatorFound = new DuplicateValidatorFound( v.name() );
        			Logger.logException(duplicateValidatorFound);
        			throw duplicateValidatorFound;
        		}
        		
        		seenValidator.add(v.name());
        		
        		Method[] methods = clazz.getMethods();
        		for( Method m : methods ) {
        			if( m.isAnnotationPresent(Identifier.class) ) {
        				Identifier i = m.getAnnotation(Identifier.class);
        				
        				// Checking Return Type
        				if( m.getReturnType() != boolean.class ) {
        					InvalidValidatorSigantature invalidValidatorSigantature = new InvalidValidatorSigantature(i.id(), m.getReturnType() ); 
        					Logger.logException(invalidValidatorSigantature);
        					throw invalidValidatorSigantature;
        				}

        				
        				// Checking for Duplicates
        				if( validatorMap.containsKey( i.id() ) ) {
        					DuplicateValidatorIdFound duplicateValidatorIdFound = new DuplicateValidatorIdFound(i.id());
        					Logger.logException(duplicateValidatorIdFound);
        					throw duplicateValidatorIdFound;
        				}
        				validatorMap.put(i.id(), m);
        			}
        		}
        	}
        	
        	// Checking for Service Classes and Adding Service with their id in map
        	if( AnnotationUtils.isAnnotationPresent(clazz, Service.class) ) {
        		
        		
        		Service s = clazz.getAnnotation(Service.class);
        		
        		if( seenService.contains(s.name()) ) {
        			DuplicateServiceFound duplicateServiceFound = new DuplicateServiceFound(s.name());
        			Logger.logException(duplicateServiceFound);
        			throw duplicateServiceFound;
        		}
        		
        		seenService.add(s.name());
        		
        		Method[] methods = clazz.getMethods();
        		for( Method m : methods ) {
        			if( m.isAnnotationPresent(Identifier.class) ) {
        				Identifier i = m.getAnnotation(Identifier.class);
        				
        				//Checking for Duplicates
        				if( serviceMap.containsKey( i.id() ) ) {
        					DuplicateServiceIdFound duplicateServiceIdFound = new DuplicateServiceIdFound( i.id() );
        					Logger.logException(duplicateServiceIdFound);
        					throw duplicateServiceIdFound;
        				}
        				
        				serviceMap.put(i.id(), m);
        			}
        		}
        	}
        }
        
        Logger.info("Filtering for Scanned Classes Completed");
	}
	
	/**
	* Recursive directory scanner
	*/
    private void findClassesInDirectory(File directory, String packageName, Set<Class<?>> classes) throws ClassNotFoundException {
       if (!directory.exists()) {
           return;
       }

       File[] files = directory.listFiles();
       if (files == null) return;

       for (File file : files) {
           if (file.isDirectory()) {
               // Recursive call
               findClassesInDirectory(file, packageName + "." + file.getName(), classes);
           } else if (file.getName().endsWith(".class")) {
               // Convert file path to class name
               String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
               try {
                   classes.add(Class.forName(className));
               } catch (NoClassDefFoundError e) {
                   Logger.logError(e);
               }
           }
       }
   }

   /**
    * JAR scanner
    */
   private void findClassesInJar(URL resource, String path, Set<Class<?>> classes) throws IOException, ClassNotFoundException {
       JarURLConnection jarConnection = (JarURLConnection) resource.openConnection();
       JarFile jarFile = jarConnection.getJarFile();
       Enumeration<JarEntry> entries = jarFile.entries();

       while (entries.hasMoreElements()) {
           JarEntry entry = entries.nextElement();
           String entryName = entry.getName();

           // Check if entry starts with the package path and is a class file
           if (entryName.startsWith(path) && entryName.endsWith(".class")) {
               // Convert path (com/example/MyClass.class) to name (com.example.MyClass)
               String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
               try {
                   classes.add(Class.forName(className));
               } catch (NoClassDefFoundError e) {
                   Logger.logError(e);
               }
           }
       }
   }
   
   /*
    * Description: Returns Validator Method based on serviceId
    */
   public Method getValidatorMethod(String validatorId) throws InvalidValidatorId {
	   if( this.validatorMap.containsKey(validatorId) ) {
		   return this.validatorMap.get(validatorId);
	   }
	   
	   InvalidValidatorId invalidValidatorId = new InvalidValidatorId(validatorId);
	   Logger.logException(invalidValidatorId);
	   throw invalidValidatorId;
   }
   
   /*
    * Description Returns AutoInitialized Object based on class;
    */
   public Object getAutoInitilizedObject(Class<?> clazz) {
	   return this.autoInitializerMap.get(clazz);
   }
   
   /*
    * Description: Returns Service Method Based on ServiceUId;
    */
   public Method getServiceMethod(String serviceId) throws InvalidServiceId {
	   if( this.serviceMap.containsKey(serviceId) ) {
		   return this.serviceMap.get(serviceId);
	   }
	   
	   InvalidServiceId invalidServiceId = new InvalidServiceId(serviceId);
	   Logger.logException(invalidServiceId);
	   throw invalidServiceId;
   }
 
}
