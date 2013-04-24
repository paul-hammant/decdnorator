package com.paulhammant.decdnorator;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class PathFinderTest {

    @Test
    public void fromFileShouldReplacePaths() {
        String path = PathFinderTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        PathFinder pathFinder = PathFinder.fromFile(new File(path)).replace("jar/target/test-classes", "mary/has/a/little/lamb");
        assertTrue(pathFinder.getBasePath().indexOf("lamb") > -1);
    }

    @Test
    public void fromFileShouldNotReplacePaths() {
        String path = PathFinderTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        PathFinder pathFinder = PathFinder.fromFile(new File(path)).replace("dfasdgfqertqer", "mary/has/a/little/lamb");
        assertTrue(pathFinder.getBasePath().indexOf("lamb") == -1);
    }

    @Test
    public void fromClassShouldReplacePaths() {
        PathFinder pathFinder = PathFinder.fromClass(PathFinderTest.class).replace("jar/target/test-classes", "mary/has/a/little/lamb");
        assertTrue(pathFinder.getBasePath().indexOf("lamb") > -1);
    }

    @Test
    public void fromClassShouldNotReplacePaths() {
        PathFinder pathFinder = PathFinder.fromClass(PathFinderTest.class).replace("zdfsdfwdrfgadsfgs", "mary/has/a/little/lamb");
        assertTrue(pathFinder.getBasePath().indexOf("lamb") == -1);
    }


}
