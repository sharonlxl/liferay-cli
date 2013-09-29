package com.liferay.cli.shell.osgi;

import org.apache.commons.lang3.SystemUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

@Component
@Service
public class OSCommandServiceImpl implements OSCommandService
{

    @Override
    public OSCommandProvider getOSCommandProvider()
    {
        OSCommandProvider retval = null;

        if ( SystemUtils.IS_OS_WINDOWS )
        {
            retval = new WindowsOSCommandProvider();
        }
        else if ( SystemUtils.IS_OS_UNIX )
        {
            retval = new UnixOSCommandProvider();
        }
        else if ( SystemUtils.IS_OS_MAC_OSX )
        {
            retval = new MacOSCommandProvider();
        }
        return retval;
    }

}
