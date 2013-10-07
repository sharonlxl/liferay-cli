package com.liferay.cli.shell.osgi;

import com.liferay.cli.metadata.MetadataService;
import com.liferay.cli.shell.Shell;

/**
 * @author Sharon Li
 */
public class MacOSCommandProvider extends UnixOSCommandProvider
{
    public MacOSCommandProvider( Shell shell, MetadataService metadataService )
    {
        super( shell, metadataService );
    }
}
