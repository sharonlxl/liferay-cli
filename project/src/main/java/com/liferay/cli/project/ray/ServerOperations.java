package com.liferay.cli.project.ray;

import com.liferay.cli.project.ProjectOperations;

/**
 * Provides Liferay project operations.
 *
 * @author Gregory Amerson
 */
public interface ServerOperations extends ProjectOperations
{

    /**
     * Setup server target for current project
     *
     * @param serverEdition
     * @param serverVersion
     * @param serverType
     */
    void serverSetup(ServerType serverType, ServerVersion serverVersion, ServerEdition serverEdition  );

    void serverStart();

    void serverStop();

    boolean isServerSetupAvailable();

    boolean isServerStartAvailable();
}