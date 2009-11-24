package de.fu_berlin.inf.dpp.activities.business;

import org.eclipse.core.runtime.IPath;

import de.fu_berlin.inf.dpp.User;
import de.fu_berlin.inf.dpp.activities.serializable.FolderActivityDataObject;
import de.fu_berlin.inf.dpp.activities.serializable.IActivityDataObject;

public class FolderActivity extends AbstractActivity implements IResourceObject {

    public static enum Type {
        Created, Removed, Moved
    }

    private final Type type;
    private final IPath path;
    private IPath oldPath;

    public FolderActivity(User source, Type type, IPath path) {
        super(source);
        this.type = type;
        this.path = path;
    }

    public IPath getPath() {
        return this.path;
    }

    /**
     * Returns the folder which was moved to a new destination (given by
     * getPath()) or null if not a move.
     */
    public IPath getOldPath() {
        return this.oldPath;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((oldPath == null) ? 0 : oldPath.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        FolderActivity other = (FolderActivity) obj;
        if (oldPath == null) {
            if (other.oldPath != null)
                return false;
        } else if (!oldPath.equals(other.oldPath))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        if (type == Type.Moved)
            return "FolderActivity(type: Moved, old path: " + this.oldPath
                + ", new path: " + this.path + ")";
        return "FolderActivity(type: " + this.type + ", path: " + this.path
            + ")";
    }

    public boolean dispatch(IActivityConsumer consumer) {
        return consumer.consume(this);
    }

    public void dispatch(IActivityReceiver receiver) {
        receiver.receive(this);
    }

    public IActivityDataObject getActivityDataObject() {
        return new FolderActivityDataObject(source.getJID(), type, oldPath);
    }

}