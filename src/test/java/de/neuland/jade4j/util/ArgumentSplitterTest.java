package de.neuland.jade4j.util;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;

/**
 * @author dusan.zatkovsky, 2/9/15
 */
public class ArgumentSplitterTest  {

    @Test
    public void testSplit() throws Exception {

        List<String> args;
        args = ArgumentSplitter.split("foo.faa('this is arg1'),'this is arg2'");
        assertEquals(args.size(), 2);
        assertEquals( "foo.faa('this is arg1')", args.get(0));
        assertEquals( "'this is arg2'", args.get(1));

        args = ArgumentSplitter.split("foo.faa ( 'this is arg1'), 'this is arg2'");
        assertEquals(args.size(), 2);
        assertEquals( "foo.faa ( 'this is arg1')", args.get(0));
        assertEquals( "'this is arg2'", args.get(1));

        args = ArgumentSplitter.split("foo.faa(\"this is arg1\"),\"this is arg2\"");
        assertEquals(args.size(), 2);
        assertEquals( "foo.faa(\"this is arg1\")", args.get(0));
        assertEquals( "\"this is arg2\"", args.get(1));

        args = ArgumentSplitter.split("foo.faa ( \"this is arg1\" ) , \"this is arg2\" ");
        assertEquals(args.size(), 2);
        assertEquals( "foo.faa ( \"this is arg1\" )", args.get(0));
        assertEquals( "\"this is arg2\"", args.get(1));

        args = ArgumentSplitter.split("1");
        assertEquals(args.size(), 1);
        assertEquals( "1", args.get(0));

        args = ArgumentSplitter.split("1,2,3");
        assertEquals(args.size(), 3);
        assertEquals( "1", args.get(0));
        assertEquals( "2", args.get(1));
        assertEquals( "3", args.get(2));

        args = ArgumentSplitter.split("1 , 2, 3");
        assertEquals(args.size(), 3);
        assertEquals( "1", args.get(0));
        assertEquals( "2", args.get(1));
        assertEquals( "3", args.get(2));

        args = ArgumentSplitter.split("1 , '2', 3");
        assertEquals(args.size(), 3);
        assertEquals( "1", args.get(0));
        assertEquals( "'2'", args.get(1));
        assertEquals( "3", args.get(2));

        args = ArgumentSplitter.split("'1' , '2', '3'");
        assertEquals(args.size(), 3);
        assertEquals( "'1'", args.get(0));
        assertEquals( "'2'", args.get(1));
        assertEquals( "'3'", args.get(2));


    }
}