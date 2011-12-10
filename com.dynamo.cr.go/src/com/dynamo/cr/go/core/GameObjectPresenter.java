package com.dynamo.cr.go.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.dynamo.cr.editor.core.EditorCorePlugin;
import com.dynamo.cr.editor.core.IResourceType;
import com.dynamo.cr.editor.core.IResourceTypeRegistry;
import com.dynamo.cr.go.core.operations.AddComponentOperation;
import com.dynamo.cr.go.core.operations.RemoveComponentOperation;
import com.dynamo.cr.sceneed.Activator;
import com.dynamo.cr.sceneed.core.ILoaderContext;
import com.dynamo.cr.sceneed.core.ISceneView;
import com.dynamo.cr.sceneed.core.ISceneView.IPresenterContext;
import com.dynamo.cr.sceneed.core.Node;

public class GameObjectPresenter implements ISceneView.INodePresenter<GameObjectNode> {

    private GameObjectNode findGameObjectFromSelection(IStructuredSelection selection) {
        Object[] nodes = selection.toArray();
        GameObjectNode parent = null;
        for (Object node : nodes) {
            if (node instanceof GameObjectNode) {
                parent = (GameObjectNode)node;
                break;
            } else if (node instanceof ComponentNode) {
                parent = (GameObjectNode)((Node)node).getParent();
                break;
            }
        }
        return parent;
    }

    public void onAddComponent(IPresenterContext presenterContext, ILoaderContext loaderContext) {
        // Find selected game objects
        // TODO: Support multi selection
        GameObjectNode parent = findGameObjectFromSelection(presenterContext.getSelection());
        if (parent == null) {
            throw new UnsupportedOperationException("No game object in selection.");
        }
        String componentType = selectComponentType(presenterContext);
        if (componentType != null) {
            ComponentTypeNode child = null;
            try {
                child = (ComponentTypeNode)loaderContext.loadNodeFromTemplate(componentType);
            } catch (Exception e) {
                presenterContext.logException(e);
            }
            if (child != null) {
                presenterContext.executeOperation(new AddComponentOperation(parent, new ComponentNode(child), presenterContext));
            } else {
                throw new UnsupportedOperationException("Component type " + componentType + " not registered.");
            }
        }
    }

    public void onAddComponentFromFile(IPresenterContext presenterContext, ILoaderContext loaderContext) {
        // Find selected game objects
        // TODO: Support multi selection
        GameObjectNode parent = findGameObjectFromSelection(presenterContext.getSelection());
        if (parent == null) {
            throw new UnsupportedOperationException("No game object in selection.");
        }
        String path = selectComponentFromFile(presenterContext);
        if (path != null) {
            ComponentTypeNode child = null;
            try {
                child = (ComponentTypeNode)loaderContext.loadNode(path);
            } catch (Exception e) {
                presenterContext.logException(e);
            }
            if (child != null) {
                RefComponentNode component = new RefComponentNode(child);
                component.setComponent(path);
                presenterContext.executeOperation(new AddComponentOperation(parent, component, presenterContext));
            } else {
                throw new UnsupportedOperationException("Component " + path + " has unknown type.");
            }
        }
    }

    public void onRemoveComponent(IPresenterContext context) {
        // Find selected components
        // TODO: Support multi selection
        IStructuredSelection structuredSelection = context.getSelection();
        Object[] nodes = structuredSelection.toArray();
        ComponentNode component = null;
        for (Object node : nodes) {
            if (node instanceof ComponentNode) {
                component = (ComponentNode)node;
                break;
            }
        }
        context.executeOperation(new RemoveComponentOperation(component, context));
    }

    private String selectComponentType(IPresenterContext context) {
        IResourceTypeRegistry registry = EditorCorePlugin.getDefault().getResourceTypeRegistry();
        IResourceType[] resourceTypes = registry.getResourceTypes();
        List<IResourceType> embeddableTypes = new ArrayList<IResourceType>();
        for (IResourceType t : resourceTypes) {
            if (t.isEmbeddable()) {
                embeddableTypes.add(t);
            }
        }

        LabelProvider labelProvider = new LabelProvider() {
            @Override
            public Image getImage(Object element) {
                return Activator.getDefault().getImage(((IResourceType)element).getFileExtension());
            }

            @Override
            public String getText(Object element) {
                return ((IResourceType)element).getName();
            }
        };

        IResourceType resourceType = (IResourceType) context.selectFromArray(Messages.GameObjectPresenter_ADD_COMPONENT, Messages.GameObjectPresenter_SELECT_COMPONENT_TYPE, embeddableTypes.toArray(), labelProvider);
        if (resourceType != null) {
            return resourceType.getFileExtension();
        }
        return null;
    }

    private String selectComponentFromFile(IPresenterContext context) {
        return context.selectFile(Messages.GameObjectPresenter_ADD_COMPONENT_FROM_FILE);
    }

}
