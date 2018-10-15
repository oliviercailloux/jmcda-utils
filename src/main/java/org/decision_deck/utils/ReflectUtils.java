package org.decision_deck.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.base.Preconditions;

public class ReflectUtils {

    static public Class<?> toClass(Type type) {
	final Class<?> toClass;
	if (type instanceof Class) {
	    toClass = (Class<?>) type;
	} else if (type instanceof ParameterizedType) {
	    final ParameterizedType pType = (ParameterizedType) type;
	    toClass = (Class<?>) pType.getRawType();
	} else {
	    throw new IllegalArgumentException("Type: " + type + " should be a class or a parameterized type.");
	}
	return toClass;
    }

    static public boolean isAssignableFrom(Class<?> type1, Class<?> type2, boolean requireExactTypes) {
	Preconditions.checkNotNull(type1);
	Preconditions.checkNotNull(type2);
	if (requireExactTypes) {
	    return type1.equals(type2);
	}
	return type1.isAssignableFrom(type2);
    }

    static public boolean isAssignableFrom(Type type1, Type type2, boolean requireExactTypes) {
	Preconditions.checkNotNull(type1);
	Preconditions.checkNotNull(type2);
	final Class<?> class1 = toClass(type1);
	final Class<?> class2 = toClass(type2);
	if (!isAssignableFrom(class1, class2, requireExactTypes)) {
	    return false;
	}
	final boolean t1Param = type1 instanceof ParameterizedType;
	final boolean t2Param = type2 instanceof ParameterizedType;
	if (t1Param != t2Param) {
	    /** Can't say for sure. Maybe one type is simply List.class e.g. */
	    return true;
	}
	/** Note that the following two conditions are equivalent. */
	if (!t1Param || !t2Param) {
	    return true;
	}

	final ParameterizedType p1 = (ParameterizedType) type1;
	final ParameterizedType p2 = (ParameterizedType) type2;
	final Type[] paramTypes1 = p1.getActualTypeArguments();
	final Type[] paramTypes2 = p2.getActualTypeArguments();
	if (paramTypes1.length != paramTypes2.length) {
	    /** Pretty strange, but let's keep it safe. */
	    return true;
	}
	for (int i = 0; i < paramTypes1.length; ++i) {
	    final Type pT1 = paramTypes1[i];
	    final Type pT2 = paramTypes2[i];
	    if (!isAssignableFrom(pT1, pT2, requireExactTypes)) {
		return false;
	    }
	}
	return true;
    }

    static public Type getParameterType(Type type) {
	if (!(type instanceof ParameterizedType)) {
	    throw new IllegalArgumentException("To type: " + type + " is not parameterized.");
	}
	ParameterizedType pType = (ParameterizedType) type;
	Type[] fieldArgTypes = pType.getActualTypeArguments();
	if (fieldArgTypes.length != 1) {
	    throw new IllegalArgumentException("To type: " + type + " is not parameterized with one argument.");
	}
	return fieldArgTypes[0];
    }
}
