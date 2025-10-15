@ECHO OFF

SETLOCAL

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the

echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME_DIR=%JAVA_HOME%
if "%JAVA_HOME_DIR:~-1%"=="\" set JAVA_HOME_DIR=%JAVA_HOME_DIR:~0,-1%
set JAVA_EXE=%JAVA_HOME_DIR%\bin\java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the

echo location of your Java installation.

goto fail

:execute
set WRAPPER_JAR=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
set WRAPPER_B64=%WRAPPER_JAR%.base64

if not exist "%WRAPPER_JAR%" if exist "%WRAPPER_B64%" (
    certutil -f -decode "%WRAPPER_B64%" "%WRAPPER_JAR%" >NUL
    if not "%ERRORLEVEL%" == "0" (
        echo.
        echo ERROR: Failed to decode the Gradle wrapper JAR. Ensure certutil is available or manually decode %WRAPPER_B64%.
        goto fail
    )
)

set CLASSPATH=%WRAPPER_JAR%

"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:fail
EXIT /B %ERRORLEVEL%

ENDLOCAL
