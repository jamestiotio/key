package org.key_project.sed.key.core.test.testcase;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.junit.Test;
import org.key_project.sed.key.core.test.Activator;
import org.key_project.sed.key.core.util.ASTNodeByEndIndexSearcher;
import org.key_project.util.eclipse.BundleUtil;
import org.key_project.util.test.util.TestUtilsUtil;

/**
 * Tests {@link ASTNodeByEndIndexSearcher}.
 * @author Martin Hentschel
 */
public class ASTNodeByEndIndexSearcherTest extends TestCase {
   /**
    * Tests the search result of the search process via
    * {@link ASTNodeByEndIndexSearcher#search(org.eclipse.jdt.core.dom.ASTNode, int)}.
    */
   @Test
   public void testSearch() throws CoreException, InterruptedException {
      // Create test project
      IJavaProject project = TestUtilsUtil.createJavaProject("ASTNodeByEndIndexSearcherTest_testSearchResult");
      BundleUtil.extractFromBundleToWorkspace(Activator.PLUGIN_ID, "data/simpleIf", project.getProject().getFolder("src"));
      // Get method
      IMethod method = TestUtilsUtil.getJdtMethod(project, "SimpleIf", "min", "I", "I");
      assertNotNull(method);
      // Create AST
      IJavaElement element = JavaCore.create(method.getResource());
      assertTrue(element instanceof ICompilationUnit);
      ASTParser parser = ASTParser.newParser(AST.JLS4);
      parser.setSource((ICompilationUnit)element);
      ASTNode root = parser.createAST(null);
      // Search method
      ASTNode methodNode = ASTNodeByEndIndexSearcher.search(root, method.getSourceRange().getOffset() + method.getSourceRange().getLength());
      assertNotNull(methodNode);
      assertTrue(methodNode instanceof MethodDeclaration);
      assertEquals(method.getElementName(), ((MethodDeclaration)methodNode).getName().toString());
      assertEquals(method.getSourceRange().getOffset() + method.getSourceRange().getLength(), methodNode.getStartPosition() + methodNode.getLength());
      // Test not existing end index
      ASTNode nullNode = ASTNodeByEndIndexSearcher.search(root, method.getSourceRange().getOffset() + method.getSourceRange().getLength() - 1);
      assertNull(nullNode);
      // Test invalid index
      nullNode = ASTNodeByEndIndexSearcher.search(root, -1);
      assertNull(nullNode);
      // Test invalid root
      nullNode = ASTNodeByEndIndexSearcher.search(null, method.getSourceRange().getOffset() + method.getSourceRange().getLength());
      assertNull(nullNode);
   }
}