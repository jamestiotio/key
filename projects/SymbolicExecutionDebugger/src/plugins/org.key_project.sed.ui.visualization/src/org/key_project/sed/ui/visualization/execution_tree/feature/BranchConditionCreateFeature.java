package org.key_project.sed.ui.visualization.execution_tree.feature;

import org.eclipse.debug.core.DebugException;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.key_project.sed.core.model.ISEDBranchCondition;
import org.key_project.sed.core.model.ISEDDebugNode;
import org.key_project.sed.core.model.ISEDDebugTarget;
import org.key_project.sed.core.model.ISEDThread;
import org.key_project.sed.core.model.memory.SEDMemoryBranchCondition;
import org.key_project.sed.ui.visualization.execution_tree.provider.IExecutionTreeImageConstants;

/**
 * Implementation of {@link ICreateFeature} for {@link ISEDBranchCondition}s.
 * @author Martin Hentschel
 */
public class BranchConditionCreateFeature extends AbstractDebugNodeCreateFeature {
   /**
    * Constructor.
    * @param fp The {@link IFeatureProvider} which provides this {@link IAddFeature}.
    */
   public BranchConditionCreateFeature(IFeatureProvider fp) {
       super(fp, "Branch Condition", "Create a new Branch Conditon");
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public String getCreateImageId() {
      return IExecutionTreeImageConstants.IMG_BRANCH_CONDITION;
   }

   /**
    * {@inheritDoc}
    */   
   @Override
   public String getNodeType() {
      return "Branch Condition";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected ISEDDebugNode createNewDebugNode(ISEDDebugTarget target,
                                              ISEDDebugNode parent,
                                              ISEDThread thread,
                                              String name) throws DebugException {
      SEDMemoryBranchCondition result = new SEDMemoryBranchCondition(target, parent, thread);
      result.setName(name);
      return result;
   }
}