package com.liferay.cli.shell.osgi;

import com.liferay.cli.shell.Shell;

import org.apache.commons.lang3.SystemUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * @author Sharon Li
 * @author Gregory Amerson
 */
@Component
@Service
public class OSCommandServiceImpl implements OSCommandService
{
    @Reference
    Shell shell;

    private OSCommandProvider commandProvider = null;

    @Override
    public OSCommandProvider getOSCommandProvider()
    {
        if( commandProvider == null )
        {
            if ( SystemUtils.IS_OS_WINDOWS )
            {
                commandProvider = new WindowsOSCommandProvider( shell );
            }
            else if ( SystemUtils.IS_OS_UNIX )
            {
                commandProvider = new UnixOSCommandProvider( shell );
            }
            else if ( SystemUtils.IS_OS_MAC_OSX )
            {
                commandProvider = new MacOSCommandProvider( shell );
            }
        }

        return commandProvider;
    }

}
