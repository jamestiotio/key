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

package de.hentschel.visualdbc.interactive.proving.ui.finder;

import de.hentschel.visualdbc.datasource.model.IDSClass;
import de.hentschel.visualdbc.datasource.model.IDSConnection;
import de.hentschel.visualdbc.datasource.model.IDSConstructor;
import de.hentschel.visualdbc.datasource.model.IDSEnum;
import de.hentschel.visualdbc.datasource.model.IDSInterface;
import de.hentschel.visualdbc.datasource.model.IDSMethod;
import de.hentschel.visualdbc.datasource.model.IDSOperationContract;
import de.hentschel.visualdbc.datasource.model.IDSPackage;
import de.hentschel.visualdbc.datasource.model.IDSProvable;
import de.hentschel.visualdbc.datasource.model.exception.DSException;
import de.hentschel.visualdbc.dbcmodel.DbcClass;
import de.hentschel.visualdbc.dbcmodel.DbcConstructor;
import de.hentschel.visualdbc.dbcmodel.DbcEnum;
import de.hentschel.visualdbc.dbcmodel.DbcInterface;
import de.hentschel.visualdbc.dbcmodel.DbcMethod;
import de.hentschel.visualdbc.dbcmodel.DbcOperationContract;
import de.hentschel.visualdbc.dbcmodel.DbcPackage;
import de.hentschel.visualdbc.dbcmodel.IDbCProvable;

/**
 * <p>
 * Implementation of {@link IDSFinder} searches the data source instance
 * for a given DbC model instance.
 * </p>
 * <p>
 * They are instantiated via an {@link IDSFinderFactory}. Implementations should
 * be subclasses of {@link AbstractDSFinder}. The default {@link IDSFinder}
 * {@link DefaultDSFinder} can handle all not {@code null} {@link IDSConnection}s.
 * </p>
 * @author Martin Hentschel
 * @see IDSFinderFactory
 * @see DefaultDSFinderFactory
 * @see AbstractDSFinder
 * @see DefaultDSFinder
 */
public interface IDSFinder {
   /**
    * Returns the used {@link IDSConnection}.
    * @return The used {@link IDSConnection}.
    */
   public IDSConnection getConnection();

   /**
    * Finds an {@link IDSPackage}.
    * @param toSearch The {@link DbcPackage} to search for.
    * @return The found {@link IDSPackage} or a thrown {@link DSException} if no one was found.
    * @throws DSException Occurred Exception.
    */
   public IDSPackage findPackage(DbcPackage toSearch) throws DSException;
   
   /**
    * Finds an {@link IDSClass}.
    * @param toSearch The {@link DbcClass} to search for.
    * @return The found {@link IDSClass} or a thrown {@link DSException} if no one was found.
    * @throws DSException Occurred Exception.
    */
   public IDSClass findClass(DbcClass toSearch) throws DSException;

   /**
    * Finds an {@link IDSInterface}.
    * @param toSearch The {@link DbcInterface} to search for.
    * @return The found {@link IDSInterface} or a thrown {@link DSException} if no one was found.
    * @throws DSException Occurred Exception.
    */
   public IDSInterface findInterface(DbcInterface toSearch) throws DSException;

   /**
    * Finds an {@link IDSEnum}.
    * @param toSearch The {@link DbcEnum} to search for.
    * @return The found {@link IDSEnum} or a thrown {@link DSException} if no one was found.
    * @throws DSException Occurred Exception.
    */
   public IDSEnum findEnum(DbcEnum toSearch) throws DSException;

   /**
    * Finds an {@link IDSMethod}.
    * @param toSearch The {@link DbcMethod} to search for.
    * @return The found {@link IDSMethod} or a thrown {@link DSException} if no one was found.
    * @throws DSException Occurred Exception.
    */
   public IDSMethod findMethod(DbcMethod toSearch) throws DSException;

   /**
    * Finds an {@link IDSConstructor}.
    * @param toSearch The {@link DbcConstructor} to search for.
    * @return The found {@link IDSConstructor} or a thrown {@link DSException} if no one was found.
    * @throws DSException Occurred Exception.
    */
   public IDSConstructor findConstructor(DbcConstructor toSearch) throws DSException;

   /**
    * Finds an {@link IDSOperationContract}.
    * @param toSearch The {@link DbcOperationContract} to search for.
    * @return The found {@link IDSOperationContract} or a thrown {@link DSException} if no one was found.
    * @throws DSException Occurred Exception.
    */
   public IDSOperationContract findOperationContract(DbcOperationContract toSearch) throws DSException;

   /**
    * Finds an {@link IDSProvable}.
    * @param toSearch The {@link IDbCProvable} to search for.
    * @return The found {@link IDSProvable} or a thrown {@link DSException} if no one was found.
    * @throws DSException Occurred Exception.
    */
   public IDSProvable findProvable(IDbCProvable toSearch) throws DSException;
}