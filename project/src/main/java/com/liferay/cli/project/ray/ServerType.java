
package com.liferay.cli.project.ray;

import com.liferay.cli.support.DisplayName;
import com.liferay.cli.support.KeyType;

/**
 * Provides server types for server setup command
 *
 * @author Gregory Amerson
 */
public class ServerType extends KeyType
{
    @DisplayName( "tomcat" )
    public static final ServerType TOMCAT = new ServerType( "TOMCAT" );

    @DisplayName( "jboss" )
    public static final ServerType JBOSS = new ServerType( "JBOSS" );

    @DisplayName( "jetty" )
    public static final ServerType JETTY = new ServerType( "JETTY" );

    @DisplayName( "glassfish" )
    public static final ServerType GLASSFISH = new ServerType( "GLASSFISH" );

    public ServerType( final String key )
    {
        super( key );
    }

}
