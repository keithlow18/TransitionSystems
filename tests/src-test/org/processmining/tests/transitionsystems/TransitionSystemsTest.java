package org.processmining.tests.transitionsystems;
import junit.framework.TestCase;

import org.junit.Test;
import org.processmining.contexts.cli.CLI;

public class TransitionSystemsTest extends TestCase {

  @Test
  public void testTransitionSystems1() throws Throwable {
    String args[] = new String[] {"-l"};
    CLI.main(args);
  }

  @Test
  public void testTransitionSystems2() throws Throwable {
    String testFileRoot = System.getProperty("test.testFileRoot", ".");
    String args[] = new String[] {"-f", testFileRoot+"/TransitionSystems_Example.txt"};
    
    CLI.main(args);
  }
  
  public static void main(String[] args) {
    junit.textui.TestRunner.run(TransitionSystemsTest.class);
  }
  
}
