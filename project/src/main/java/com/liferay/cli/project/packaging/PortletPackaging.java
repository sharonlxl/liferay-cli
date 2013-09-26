
package com.liferay.cli.project.packaging;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

/**
 * @author Gregory Amerson
 */
@Component
@Service
public class PortletPackaging extends PluginPackaging
{

    public static final String NAME = "portlet";

    /**
     * Constructor
     */
    public PortletPackaging()
    {
        super( NAME );
    }

}
