package de.lucaswerkmeister.graaleneyj.runtime;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

/**
 * A registry for persistent objects, managed by the {@link ZContext}.
 * 
 * @see ZContext#getPersistentObjectRegistry()
 */
public class ZPersistentObjectRegistry extends DynamicObject {

	ZPersistentObjectRegistry(Shape shape) {
		super(shape);
	}

}
