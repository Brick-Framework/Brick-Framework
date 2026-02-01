package com.brick.framework.utility;

import java.util.HashSet;
import java.util.Set;
import java.lang.annotation.Annotation;

public class AnnotationUtils {
	
	private AnnotationUtils() {
		super();
	}

	public static boolean isAnnotationPresent(Class<?>  element, Class<? extends Annotation> targetAnnotation) {
        return isAnnotationPresent(element, targetAnnotation, new HashSet<>());
    }


    private static boolean isAnnotationPresent(Class<?>  element, Class<? extends Annotation> targetAnnotation, Set<Class<?>> visited) {
        
        // 1. Prevent infinite recursion (cycles in meta-annotations)
        if (element == null || !visited.add(element)) {
            return false;
        }

        // 2. Direct Check: Is the annotation present directly on this element?
        if (element.isAnnotationPresent(targetAnnotation)) {
            return true;
        }

        // 3. Recursive Step: Check the annotations *on* this element
        // We iterate over all annotations present on the current element
        for (Annotation annotation : element.getAnnotations()) {
            
            // Optimization: Skip java.lang.annotation package to avoid deep scanning JDK internals
            Class<? extends Annotation> annotationType = annotation.annotationType();
            String packageName = annotationType.getPackageName();
            
            if (packageName.startsWith("java.lang.annotation") && 
                !packageName.equals(targetAnnotation.getPackageName())) {
                continue;
            }

            // Recurse: Check if the annotation type itself has the target annotation
            if (isAnnotationPresent(annotationType, targetAnnotation, visited)) {
                return true;
            }
        }

        return false;
    }

}
