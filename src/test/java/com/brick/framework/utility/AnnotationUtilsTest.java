package com.brick.framework.utility;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;

import com.brick.framework.annotations.AutoIntialize;
import com.brick.framework.annotations.Service;
import com.brick.framework.annotations.Validator;

public class AnnotationUtilsTest {
	@Test
	public void isAnnotationPresent_test() {
		assertFalse( AnnotationUtils.isAnnotationPresent(null, Service.class));
		assertTrue( AnnotationUtils.isAnnotationPresent(A.class, Service.class));
		assertTrue( AnnotationUtils.isAnnotationPresent(B.class, Validator.class));
		assertTrue( AnnotationUtils.isAnnotationPresent(A.class, AutoIntialize.class));
		assertTrue( AnnotationUtils.isAnnotationPresent(B.class, AutoIntialize.class));
		assertFalse( AnnotationUtils.isAnnotationPresent(A.class, Validator.class) );
		assertFalse( AnnotationUtils.isAnnotationPresent(A.class, Override.class) );
	}
}

@Service(name="A")
class A{
}

@Validator(name="B")
class B{
}
