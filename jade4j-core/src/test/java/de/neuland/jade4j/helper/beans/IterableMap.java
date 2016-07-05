package de.neuland.jade4j.helper.beans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class IterableMap extends LinkedHashMap<String, String> implements Iterable<String> {

    @Override
    public Iterator<String> iterator() {
        return this.values().iterator();
    }

}
