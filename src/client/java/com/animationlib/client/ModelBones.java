package com.animationlib.client;

import com.animationlib.mixin.client.ModelPartAccessor;
import net.minecraft.client.model.geom.ModelPart;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Finds a bone by name anywhere in a model's part tree, not just among the root's direct children,
 * so an animation can target a nested part like a goat's {@code left_horn} (under {@code head}).
 * The shallowest part with that name wins, so a direct child of the root is found exactly as
 * before, and a name that is only used further down still resolves.
 */
public final class ModelBones {
    private ModelBones() {
    }

    /** The part named {@code bone}, or {@code null} if the model has no such part. */
    public static ModelPart find(ModelPart root, String bone) {
        if (root.hasChild(bone)) {
            return root.getChild(bone);
        }
        List<ModelPart> path = path(root, bone);
        return path.isEmpty() ? null : path.get(path.size() - 1);
    }

    /**
     * The parts from the root's direct child down to the one named {@code bone}, in order, so a
     * caller can apply each one's transform on the way down. Empty if the model has no such part.
     */
    public static List<ModelPart> path(ModelPart root, String bone) {
        if (root.hasChild(bone)) {
            return List.of(root.getChild(bone));
        }

        // Breadth-first, so the shallowest match wins when a name is reused at different depths.
        Deque<List<ModelPart>> queue = new ArrayDeque<>();
        queue.add(List.of());
        Deque<ModelPart> parents = new ArrayDeque<>();
        parents.add(root);
        while (!queue.isEmpty()) {
            List<ModelPart> pathToParent = queue.poll();
            ModelPart parent = parents.poll();
            for (Map.Entry<String, ModelPart> child : children(parent).entrySet()) {
                List<ModelPart> path = new ArrayList<>(pathToParent);
                path.add(child.getValue());
                if (child.getKey().equals(bone)) {
                    return path;
                }
                queue.add(path);
                parents.add(child.getValue());
            }
        }
        return List.of();
    }

    private static Map<String, ModelPart> children(ModelPart part) {
        return ((ModelPartAccessor) (Object) part).animationlib$children();
    }
}
