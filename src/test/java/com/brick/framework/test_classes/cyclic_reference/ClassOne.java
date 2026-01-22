package com.brick.framework.test_classes.cyclic_reference;

import com.brick.framework.annotations.AutoIntialize;

@AutoIntialize
public class ClassOne {
	public ClassOne(ClassTwo classTwo) {
	}
}
