package soot.util;
import jedd.*;
import java.util.*;

public class MapNumberer implements Numberer {
    Map map = new HashMap();
    ArrayList al = new ArrayList();
    int nextIndex = 1;
    public void add( Object o ) {
        if( !map.containsKey(o) ) {
            map.put( o, new Integer(nextIndex) );
            al.add(o);
            nextIndex++;
        }
    }
    public Object get( int number ) {
        return al.get(number);
    }
    public int get( Object o ) {
        if( o == null ) return 0;
        Integer i = (Integer) map.get(o);
        if( i == null ) throw new RuntimeException( "couldn't find "+o );
        return i.intValue();
    }
    public int size() { return nextIndex; }
    public MapNumberer() { al.add(null); }
}
