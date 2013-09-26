
package com.liferay.cli.project.ray;

import com.liferay.cli.support.DisplayName;
import com.liferay.cli.support.KeyType;

/**
 * Provides server types for server setup command
 *
 * @author Gregory Amerson
 */
public class ServerVersion extends KeyType
{
    @DisplayName( "6.0" )
    public static final ServerVersion v60 = new ServerVersion( "6.0" );

    @DisplayName( "6.1" )
    public static final ServerVersion v61 = new ServerVersion( "6.1" );

    @DisplayName( "6.2" )
    public static final ServerVersion v62 = new ServerVersion( "6.2" );

    public ServerVersion( final String key )
    {
        super( key );
    }

}
