/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2010  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.util.symbpredabstraction.interfaces;

import org.sosy_lab.cpachecker.cfa.objectmodel.CFAEdge;
import org.sosy_lab.cpachecker.exceptions.CPATransferException;
import org.sosy_lab.cpachecker.util.symbpredabstraction.Abstraction;
import org.sosy_lab.cpachecker.util.symbpredabstraction.PathFormula;
import org.sosy_lab.cpachecker.util.symbpredabstraction.AbstractionPredicate;

public interface FormulaManager {

  /**
   * @return a concrete representation of an abstract formula
   * The formula returned is a "generic" version, not instantiated to any
   * particular "SSA step" (see SymbolicFormulaManager.instantiate()).
   */
  public SymbolicFormula toConcrete(Region af);

  /**
   * Creates a formula representing an AND of the two argument.
   * @param pf a PathFormula
   * @param e a CFA edge
   * @return The formula (pf & e), and the new/updated SSAMap
   * @throws CPATransferException 
   */
  public PathFormula makeAnd(PathFormula pf, CFAEdge e) throws CPATransferException;

  /**
   * Creates a new path formula representing an OR of the two arguments. Differently
   * from {@link SymbolicFormulaManager#makeOr(SymbolicFormula, SymbolicFormula)},
   * it also merges the SSA maps and creates the necessary adjustments to the
   * formulas if the two SSA maps contain different values for the same variables.
   *
   * @param pF1 a PathFormula
   * @param pF2 a PathFormula
   * @return (pF1 | pF2)
   */
  public PathFormula makeOr(PathFormula pF1, PathFormula pF2);

  /**
   * Creates a new Abstraction object that represents the formula "true".
   */
  public Abstraction makeTrueAbstraction(SymbolicFormula previousBlockFormula);
  
  /**
   * Creates a new empty path formula.
   */
  public PathFormula makeEmptyPathFormula();
  
  /**
   * Creates a new empty path formula, but copies SSAMap, reachingPathsFormula
   * and branchingCounter from oldFormula.
   */
  public PathFormula makeEmptyPathFormula(PathFormula oldFormula);

  /**
   * Creates a conjunction of a pathFormula and a formula.
   * The other formula will be instantiated with the indices from the pathFormula
   * first.
   * 
   * @return (pPathFormula & pOtherFormula)
   */
  public PathFormula makeAnd(PathFormula pPathFormula, SymbolicFormula pOtherFormula);
  
  /**
   * creates a Predicate for an atom that defines it
   */
  public AbstractionPredicate makePredicate(SymbolicFormula atom);
  
  /**
   * Creates a Predicate for the atom "false".
   */
  public AbstractionPredicate makeFalsePredicate();
  
  /**
   * Get predicate corresponding to a variable.
   * @param var A symbolic formula representing the variable. The same formula has to been passed to makePredicate earlier.
   * @return a Predicate
   */
  public AbstractionPredicate getPredicate(SymbolicFormula var);

}
