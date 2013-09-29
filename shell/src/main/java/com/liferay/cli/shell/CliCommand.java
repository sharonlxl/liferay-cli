package com.liferay.cli.shell;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CliCommand {

    /**
     * @return a help message for this command (the default is a blank String,
     *         which means there is no help)
     */
    String help() default "";

    /**
     * @return one or more strings which must serve as the start of a particular
     *         command in order to match this method (these must be unique
     *         within the entire application; if not unique, behaviour is not
     *         specified)
     */
    String[] value();

    /**
     * @return Indicates if this command should only be shown when CLI is set to 'advanced' mode.
     */
    boolean advanced() default false;
    
    /**
     * @return Indicates if this command is a system command and is always available but should never
     * be shown in the 'help' command
     */
    boolean system() default false;
}
