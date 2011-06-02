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
package org.sosy_lab.cpachecker.util.predicates.mathsat;

import java.util.List;

import org.sosy_lab.cpachecker.util.predicates.Model;
import org.sosy_lab.cpachecker.util.predicates.interfaces.Formula;
import org.sosy_lab.cpachecker.util.predicates.interfaces.InterpolatingTheoremProver;

import com.google.common.base.Preconditions;

public class MathsatInterpolatingProver implements InterpolatingTheoremProver<Integer> {

    private final MathsatFormulaManager mgr;
    private long env;
    
    private final boolean useSharedEnv;

    public MathsatInterpolatingProver(MathsatFormulaManager pMgr, boolean shared) {
        mgr = pMgr;
        env = 0;
        useSharedEnv = shared;
    }

    @Override
    public void init() {
        Preconditions.checkState(env == 0);

        env = mgr.createEnvironment(useSharedEnv, false);

        int ok = mathsat.api.msat_init_interpolation(env);
        assert(ok == 0);
    }

    @Override
    public Integer addFormula(Formula f) {
        Preconditions.checkState(env != 0);

        long t = ((MathsatFormula)f).getTerm();
        if (!useSharedEnv) {
            t = mathsat.api.msat_make_copy_from(env, t, mgr.getMsatEnv());
        }
        int group = mathsat.api.msat_create_itp_group(env);
        mathsat.api.msat_set_itp_group(env, group);
        mathsat.api.msat_assert_formula(env, t);
        return group;
    }

    @Override
    public boolean isUnsat() {
        Preconditions.checkState(env != 0);

        int res = mathsat.api.msat_solve(env);
        assert(res != mathsat.api.MSAT_UNKNOWN);
        
        return res == mathsat.api.MSAT_UNSAT;
    }

    @Override
    public Formula getInterpolant(List<Integer> formulasOfA) {
        Preconditions.checkState(env != 0);

        int[] groupsOfA = new int[formulasOfA.size()];
        int i = 0;
        for (Integer f : formulasOfA) {
          groupsOfA[i++] = f;
        }
        long itp = mathsat.api.msat_get_interpolant(env, groupsOfA);
        assert(!mathsat.api.MSAT_ERROR_TERM(itp));
        if (!useSharedEnv) {
            itp = mathsat.api.msat_make_copy_from(mgr.getMsatEnv(), itp, env);
        }
        return new MathsatFormula(itp);
    }

    @Override
    public void reset() {
        Preconditions.checkState(env != 0);

        mathsat.api.msat_destroy_env(env);
        env = 0;
    }

    @Override
    public Model getModel() {
      Preconditions.checkState(env != 0);
      
      return MathsatModel.createMathsatModel(env);
    }
    
}