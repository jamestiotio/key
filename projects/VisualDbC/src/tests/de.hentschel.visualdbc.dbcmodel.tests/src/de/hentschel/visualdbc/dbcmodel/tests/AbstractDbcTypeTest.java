/*******************************************************************************
 * Copyright (c) 2011 Martin Hentschel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Hentschel - initial API and implementation
 *******************************************************************************/

/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hentschel.visualdbc.dbcmodel.tests;

import de.hentschel.visualdbc.dbcmodel.AbstractDbcType;
import de.hentschel.visualdbc.dbcmodel.DbcClass;
import de.hentschel.visualdbc.dbcmodel.DbcInvariant;
import de.hentschel.visualdbc.dbcmodel.DbcmodelFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Abstract Dbc Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link de.hentschel.visualdbc.dbcmodel.AbstractDbcType#getInvariant(java.lang.String) <em>Get Invariant</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public abstract class AbstractDbcTypeTest extends AbstractDbcTypeContainerTest {

   /**
    * Constructs a new Abstract Dbc Type test case with the given name.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   public AbstractDbcTypeTest(String name) {
      super(name);
   }

   /**
    * Returns the fixture for this Abstract Dbc Type test case.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected AbstractDbcType getFixture() {
      return (AbstractDbcType)fixture;
   }

   /**
    * Tests the '{@link de.hentschel.visualdbc.dbcmodel.AbstractDbcType#getInvariant(java.lang.String) <em>Get Invariant</em>}' operation.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @see de.hentschel.visualdbc.dbcmodel.AbstractDbcType#getInvariant(java.lang.String)
    * @generated NOT
    */
   public void testGetInvariant__String() {
      // Create model
      DbcClass container = DbcmodelFactory.eINSTANCE.createDbcClass();
      DbcInvariant invariantA = DbcmodelFactory.eINSTANCE.createDbcInvariant();
      invariantA.setCondition("invariantA");
      container.getInvariants().add(invariantA);
      DbcInvariant invariantB = DbcmodelFactory.eINSTANCE.createDbcInvariant();
      invariantB.setCondition("invariantB");
      container.getInvariants().add(invariantB);
      DbcInvariant invariantC = DbcmodelFactory.eINSTANCE.createDbcInvariant();
      invariantC.setCondition("invariantC");
      container.getInvariants().add(invariantC);
      DbcInvariant invariantD = DbcmodelFactory.eINSTANCE.createDbcInvariant();
      invariantD.setCondition("invariantD");
      // Execute test
      assertEquals(invariantA, container.getInvariant(invariantA.getCondition()));
      assertEquals(invariantB, container.getInvariant(invariantB.getCondition()));
      assertEquals(invariantC, container.getInvariant(invariantC.getCondition()));
      assertNull(container.getInvariant(invariantD.getCondition()));
   }

} //AbstractDbcTypeTest