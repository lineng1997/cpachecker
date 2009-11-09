/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker. 
 *
 *  Copyright (C) 2007-2008  Dirk Beyer and Erkan Keremoglu.
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
 *    http://www.cs.sfu.ca/~dbeyer/CPAchecker/
 */
package cpa.invariant.dump;

import cfa.objectmodel.CFAFunctionDefinitionNode;
import cpa.common.defaults.MergeSepOperator;
import cpa.common.defaults.StopNeverOperator;
import cpa.common.interfaces.AbstractDomain;
import cpa.common.interfaces.AbstractElement;
import cpa.common.interfaces.ConfigurableProgramAnalysis;
import cpa.common.interfaces.MergeOperator;
import cpa.common.interfaces.Precision;
import cpa.common.interfaces.PrecisionAdjustment;
import cpa.common.interfaces.StopOperator;
import cpa.common.interfaces.TransferRelation;
import cpa.invariant.util.InvariantSymbolicFormulaManager;
import cpa.invariant.util.MathsatInvariantSymbolicFormulaManager;

/**
 * CPA used to capture the invariants that ought to be dumped.
 * The CPA does not do anything but to serve as a way to collect
 * invariants via strengthening from the other CPAs in cpa.invariant.
 * 
 * Note that once the CPA algorithm has finished running, a call
 * to dumpInvariants() is needed to process the reachable states
 * and produce the actual invariants.
 *  
 * @author g.theoduloz
 */
public class DumpInvariantCPA implements ConfigurableProgramAnalysis {

  private final DumpInvariantDomain abstractDomain;
  private final MergeOperator mergeOperator;
  private final StopOperator stopOperator;
  private final TransferRelation transferRelation;
  private final InvariantSymbolicFormulaManager symbolicFormulaManager;
  
  public DumpInvariantCPA(String merge, String stop)
  {
    symbolicFormulaManager = MathsatInvariantSymbolicFormulaManager.getInstance();
    abstractDomain = new DumpInvariantDomain();
    mergeOperator = new MergeSepOperator();
    stopOperator = new StopNeverOperator();
    transferRelation = new DumpInvariantTransferRelation(this);
  }
  
  public InvariantSymbolicFormulaManager getSymbolicFormulaManager()
  {
    return symbolicFormulaManager;
  }
  
  @Override
  public AbstractDomain getAbstractDomain() {
    return abstractDomain;
  }

  @Override
  public <AE extends AbstractElement> AE getInitialElement(CFAFunctionDefinitionNode node) {
    return (AE) abstractDomain.getTopElement();
  }

  @Override
  public Precision getInitialPrecision(CFAFunctionDefinitionNode pNode) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MergeOperator getMergeOperator() {
    return mergeOperator;
  }

  @Override
  public PrecisionAdjustment getPrecisionAdjustment() {
    return null;
  }

  @Override
  public StopOperator getStopOperator() {
    return stopOperator;
  }

  @Override
  public TransferRelation getTransferRelation() {
    return transferRelation;
  }

}
