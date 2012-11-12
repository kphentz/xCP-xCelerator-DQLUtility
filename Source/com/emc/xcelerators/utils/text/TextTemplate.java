package com.emc.xcelerators.utils.text;

import java.util.Collection;
import java.util.Map;

/**
 * @author Tord Svensson, EMC Software
 * @version 1.0 (Sep 17, 2008)
 */
public interface TextTemplate {

	String apply(Map<String, String> attrs);

	TextTemplate partial(Map<String, String> attrs);

	Collection<String> getVariables();
}
