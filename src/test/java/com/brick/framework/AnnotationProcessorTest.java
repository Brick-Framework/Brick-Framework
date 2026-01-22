package com.brick.framework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

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
import com.brick.framework.test_classes.success.AutoIntialiseClass;
import com.brick.framework.test_classes.success.ServiceClassOne;
import com.brick.framework.test_classes.success.ValidatorClassOne;

public class AnnotationProcessorTest {
	@Test
	public void success() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, DuplicateValidatorFound, InvalidValidatorSigantature, DuplicateValidatorIdFound, DuplicateServiceFound, DuplicateServiceIdFound, MultiplePublicConstructorFound, NoPublicConstructorFound, CyclicAutoInitilizationReferenceFound, NoSuchMethodException, SecurityException, InvalidValidatorId, InvalidServiceId {
		AnnotationProcessor annotationProcessor = new AnnotationProcessor("com.brick.framework.test_classes.success");
		
		assertEquals(ValidatorClassOne.class.getMethod("validatorMethodOne"), annotationProcessor.getValidatorMethod("validatorMethodOne"));
		assertEquals(ValidatorClassOne.class.getMethod("validatorMethodTwo"), annotationProcessor.getValidatorMethod("validatorMethodTwo"));
		assertThrows(InvalidValidatorId.class, ()->{
			annotationProcessor.getValidatorMethod("invalid");
		});
		
		assertEquals(AutoIntialiseClass.class,annotationProcessor.getAutoInitilizedObject(AutoIntialiseClass.class).getClass());
		
		assertEquals(ServiceClassOne.class.getMethod("serviceMethodOne"), annotationProcessor.getServiceMethod("serviceMethodOne"));
		assertEquals(ServiceClassOne.class.getMethod("serviceMethodTwo"), annotationProcessor.getServiceMethod("serviceMethodTwo"));
		assertThrows(InvalidServiceId.class, ()->{
			annotationProcessor.getServiceMethod("invalid");
		});
	}
	
	@Test
	public void duplicateValidator() {
		assertThrows(DuplicateValidatorFound.class, ()->{
			new AnnotationProcessor("com.brick.framework.test_classes.duplicate_validator");
		});
	}
	
	@Test
	public void duplicateValidatorId() {
		assertThrows(DuplicateValidatorIdFound.class, ()->{
			new AnnotationProcessor("com.brick.framework.test_classes.duplicate_validatorId");
		});
	}
	
	@Test
	public void duplicateService() {
		assertThrows(DuplicateServiceFound.class, ()->{
			new AnnotationProcessor("com.brick.framework.test_classes.duplicate_service");
		});
	}
	
	@Test
	public void duplicateServiceId() {
		assertThrows(DuplicateServiceIdFound.class, ()->{
			new AnnotationProcessor("com.brick.framework.test_classes.duplicate_serviceId");
		});
	}
	
	@Test
	public void invalidValidatorSignature() {
		assertThrows(InvalidValidatorSigantature.class, ()->{
			new AnnotationProcessor("com.brick.framework.test_classes.invalid_validator_signature");
		});
	}
	
	@Test
	public void multiplePublicConstructorFound() {
		assertThrows(MultiplePublicConstructorFound.class, ()->{
			new AnnotationProcessor("com.brick.framework.test_classes.multiple_public_constructor");
		});
	}
	
	@Test
	public void noPublicConstructorFound() {
		assertThrows(NoPublicConstructorFound.class, ()->{
			new AnnotationProcessor("com.brick.framework.test_classes.no_public_constructor");
		});
	}
	
	@Test
	public void cyclicReference() {
		assertThrows(CyclicAutoInitilizationReferenceFound.class, ()->{
			new AnnotationProcessor("com.brick.framework.test_classes.cyclic_reference");
		});
	}
}
