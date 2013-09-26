
package com.liferay.cli.project.ray;

import com.liferay.cli.support.KeyType;

/**
 * Provides server types for server setup command
 *
 * @author Gregory Amerson
 */
public class ServerEdition extends KeyType
{
    public static final ServerEdition CE = new ServerEdition( "CE" );
    public static final ServerEdition EE = new ServerEdition( "EE" );

    public ServerEdition( final String key )
    {
        super( key );
    }

}
