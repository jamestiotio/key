package org.key_project.sed.ui.visualization.test.testcase;

import org.eclipse.graphiti.features.context.IMultiDeleteInfo;
import org.junit.Test;
import org.key_project.sed.ui.visualization.util.EditableMultiDeleteInfo;

import junit.framework.TestCase;

/**
 * Tests for {@link EditableMultiDeleteInfo}.
 * @author Martin Hentschel
 */
public class EditableMultiDeleteInfoTest extends TestCase {
   /**
    * Tests the defined number via {@link EditableMultiDeleteInfo#getNumber()}
    * and {@link EditableMultiDeleteInfo#setNumber(int)}.
    */
   @Test
   public void testNumber() {
      // Test default constructor
      IMultiDeleteInfo defaultInfo = new EditableMultiDeleteInfo(true, false);
      assertTrue(defaultInfo.isShowDialog());
      assertFalse(defaultInfo.isDeleteCanceled());
      assertEquals(1, defaultInfo.getNumber());
      // Test pre defined number
      EditableMultiDeleteInfo info = new EditableMultiDeleteInfo(false, true, 2);
      assertFalse(info.isShowDialog());
      assertTrue(info.isDeleteCanceled());
      assertEquals(2, info.getNumber());
      // Change number
      info.setNumber(3);
      assertFalse(info.isShowDialog());
      assertTrue(info.isDeleteCanceled());
      assertEquals(3, info.getNumber());
   }
}