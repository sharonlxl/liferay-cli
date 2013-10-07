@echo off
setlocal enabledelayedexpansion

for %%? in ("%~dp0..") do set RAY_HOME=%%~f?
rem echo Resolved RAY_HOME: "%RAY_HOME%"

if not exist "%RAY_HOME%\bootstrap\target\osgi\bin"    mkdir "%RAY_HOME%\bootstrap\target\osgi\bin" 
if not exist "%RAY_HOME%\bootstrap\target\osgi\bundle" mkdir "%RAY_HOME%\bootstrap\target\osgi\bundle" 
if not exist "%RAY_HOME%\bootstrap\target\osgi\conf"   mkdir "%RAY_HOME%\bootstrap\target\osgi\conf" 

copy "%RAY_HOME%\bootstrap\src\main\bin\*.*"  "%RAY_HOME%\bootstrap\target\osgi\bin"  > NUL
copy "%RAY_HOME%\bootstrap\src\main\conf\*.*" "%RAY_HOME%\bootstrap\target\osgi\conf" > NUL

rem Most ray bundles are not special and belong in "bundle"
copy "%RAY_HOME%\target\all\*.jar" "%RAY_HOME%\bootstrap\target\osgi\bundle" > NUL

rem Move the startup-related JAR from the "bundle" directory to the "bin" directory
move "%RAY_HOME%\bootstrap\target\osgi\bundle\com.liferay.cli.bootstrap-*.jar" "%RAY_HOME%\bootstrap\target\osgi\bin" > NUL 2>&1
move "%RAY_HOME%\bootstrap\target\osgi\bundle\org.apache.felix.framework-*.jar" "%RAY_HOME%\bootstrap\target\osgi\bin" > NUL 2>&1

rem Build a classpath containing our two magical startup JARs
for %%a in ("%RAY_HOME%\bootstrap\target\osgi\bin\*.jar") do set RAY_CP=!RAY_CP!%%a;

rem Hop, hop, hop...
rem java -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -Djline.nobell=true %RAY_OPTS% -Dray.args="%*" -DdevelopmentMode=true -Dorg.osgi.framework.storage="%RAY_HOME%\bootstrap\target\osgi\cache" -Dfelix.auto.deploy.dir="%RAY_HOME%\bootstrap\target\osgi\bundle" -Dfelix.config.properties="file:%RAY_HOME%\bootstrap\target\osgi\conf\config.properties" -Dray.console.ansi=true -cp "%RAY_CP%" com.liferay.cli.bootstrap.Main
java -Djline.nobell=true %RAY_OPTS% -Dray.args="%*" -DdevelopmentMode=true -Dorg.osgi.framework.storage="%RAY_HOME%\bootstrap\target\osgi\cache" -Dfelix.auto.deploy.dir="%RAY_HOME%\bootstrap\target\osgi\bundle" -Dfelix.config.properties="file:%RAY_HOME%\bootstrap\target\osgi\conf\config.properties" -Dray.console.ansi=true -cp "%RAY_CP%" com.liferay.cli.bootstrap.Main
echo ray exited with code %errorlevel%
