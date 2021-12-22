package org.jivesoftware.openfire.exceptions;

import org.jivesoftware.openfire.cluster.NodeID;

import java.util.Map;

/**
 * Exception to communicate that something went wrong (probably) because of plugin version inconsistencies in the cluster.
 */
public class PluginVersionException extends Exception {
    private String localPluginVersion;
    private Map<NodeID, String> inconsistentRemotePluginVersions;

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     * @param localPluginVersion The version of the plugin that is installed on this local node.
     * @param inconsistentRemotePluginVersions A map containing all nodes that have a different version of this plugin.
     */
    public PluginVersionException(String message, String localPluginVersion, Map<NodeID, String> inconsistentRemotePluginVersions) {
        super(message);
        this.localPluginVersion = localPluginVersion;
        this.inconsistentRemotePluginVersions = inconsistentRemotePluginVersions;
    }

    public String getLocalPluginVersion() {
        return localPluginVersion;
    }

    public Map<NodeID, String> getInconsistentRemotePluginVersions() {
        return inconsistentRemotePluginVersions;
    }
}
