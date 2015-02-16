package de.neuland.jade4j.util;

import static de.neuland.jade4j.util.ArgumentSplitter.split;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

/**
 * @author dusan.zatkovsky, 2/9/15
 */
public class ArgumentSplitterTest  {

    @Test
    public void testSplit() throws Exception {

        List<String> args;
        args = split("foo.faa('this is arg1'),'this is arg2'");
        assertEquals(args.size(), 2);
        assertEquals( "foo.faa('this is arg1')", args.get(0));
        assertEquals( "'this is arg2'", args.get(1));

        args = split("foo.faa ( 'this is arg1'), 'this is arg2'");
        assertEquals(args.size(), 2);
        assertEquals( "foo.faa ( 'this is arg1')", args.get(0));
        assertEquals( "'this is arg2'", args.get(1));

        args = split("foo.faa(\"this is arg1\"),\"this is arg2\"");
        assertEquals(args.size(), 2);
        assertEquals( "foo.faa(\"this is arg1\")", args.get(0));
        assertEquals( "\"this is arg2\"", args.get(1));

        args = split("foo.faa ( \"this is arg1\" ) , \"this is arg2\" ");
        assertEquals(args.size(), 2);
        assertEquals( "foo.faa ( \"this is arg1\" )", args.get(0));
        assertEquals( "\"this is arg2\"", args.get(1));

        args = split("1");
        assertEquals(args.size(), 1);
        assertEquals( "1", args.get(0));

        args = split("1,2,3");
        assertEquals(args.size(), 3);
        assertEquals( "1", args.get(0));
        assertEquals( "2", args.get(1));
        assertEquals( "3", args.get(2));

        args = split("1 , 2, 3");
        assertEquals(args.size(), 3);
        assertEquals( "1", args.get(0));
        assertEquals( "2", args.get(1));
        assertEquals( "3", args.get(2));

        args = split("1 , '2', 3");
        assertEquals(args.size(), 3);
        assertEquals( "1", args.get(0));
        assertEquals( "'2'", args.get(1));
        assertEquals( "3", args.get(2));

        args = split("'1' , '2', '3'");
        assertEquals(args.size(), 3);
        assertEquals( "'1'", args.get(0));
        assertEquals( "'2'", args.get(1));
        assertEquals( "'3'", args.get(2));

        args = split("bar(bazz, 'ba(z,z)'), 123");
        assertEquals(args.size(), 2);
        assertEquals( "bar(bazz, 'ba(z,z)')", args.get(0));
        assertEquals( "123", args.get(1));

        args = split("'aaa', bar(bazz, foo('ba(z,z)')), 123");
        assertEquals(args.size(), 3);
        assertEquals("'aaa'", args.get(0));
        assertEquals( "bar(bazz, foo('ba(z,z)'))", args.get(1));
        assertEquals( "123", args.get(2));

        args = split("123, '1,2,3', \"a,b,c\"");
        assertEquals(args.size(), 3);
        assertEquals( "123", args.get(0));
        assertEquals( "'1,2,3'", args.get(1));
        assertEquals( "\"a,b,c\"", args.get(2));
    }
}