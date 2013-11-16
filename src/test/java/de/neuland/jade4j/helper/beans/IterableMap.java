package de.neuland.jade4j.helper.beans;

import java.util.HashMap;
import java.util.Iterator;

public class IterableMap extends HashMap<String, String> implements Iterable<String> {

    @Override
    public Iterator<String> iterator() {
        return this.values().iterator();
    }

}
