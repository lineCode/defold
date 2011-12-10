package com.dynamo.cr.tileeditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.dynamo.cr.sceneed.core.ISceneView.IPresenterContext;
import com.dynamo.cr.sceneed.core.NodeUtil;
import com.dynamo.cr.sceneed.core.operations.AbstractSelectOperation;
import com.dynamo.cr.tileeditor.scene.CollisionGroupNode;
import com.dynamo.cr.tileeditor.scene.TileSetNode;

public class AddCollisionGroupNodeOperation extends AbstractSelectOperation {

    final private TileSetNode tileSet;
    final private CollisionGroupNode collisionGroup;

    public AddCollisionGroupNodeOperation(TileSetNode tileSet, CollisionGroupNode collisionGroup, IPresenterContext presenterContext) {
        super("Add Collision Group", collisionGroup, presenterContext);
        this.tileSet = tileSet;
        this.collisionGroup = collisionGroup;
        String id = "default";
        id = NodeUtil.getUniqueId(this.tileSet.getCollisionGroups(), id, new NodeUtil.IdFetcher<CollisionGroupNode>() {
            @Override
            public String getId(CollisionGroupNode node) {
                return node.getId();
            }
        });
        this.collisionGroup.setId(id);
    }

    @Override
    protected IStatus doExecute(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        this.tileSet.addCollisionGroup(this.collisionGroup);
        return Status.OK_STATUS;
    }

    @Override
    protected IStatus doRedo(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        this.tileSet.addCollisionGroup(this.collisionGroup);
        return Status.OK_STATUS;
    }

    @Override
    protected IStatus doUndo(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        this.tileSet.removeCollisionGroup(this.collisionGroup);
        return Status.OK_STATUS;
    }

}
