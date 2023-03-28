;--------------------------------

Name "Boggle Installer"

; The file to write
OutFile "build\BoggleInstaller.exe"

; The default installation directory
InstallDir $PROGRAMFILES\Boggle

; The text to prompt the user to enter a directory
DirText "Choose where you would like to install Boggle"

;--------------------------------

; The stuff to install
Section "" ;No components page, name is not important

; Set output path to the installation directory.
SetOutPath $INSTDIR

; Put file there
File "build\boggle.jar"
File "build\boggle.zip"
File "build\classes\resources\images\icon.ico"

; Now create shortcuts
CreateDirectory "$SMPROGRAMS\Boggle"
CreateShortCut "$SMPROGRAMS\Boggle\Boggle.lnk" "$SYSDIR\javaw.exe" "-jar boggle.jar" "$INSTDIR\icon.ico"
CreateShortCut "$SMPROGRAMS\Boggle\Uninstall.lnk" "$INSTDIR\Uninstall.exe"

; Tell the compiler to write an uninstaller and to look for a "Uninstall" section
WriteUninstaller $INSTDIR\Uninstall.exe

SectionEnd ; end the section

; The uninstall section
Section "Uninstall"

Delete $INSTDIR\boggle.jar
Delete $INSTDIR\boggle.zip
Delete $INSTDIR\icon.ico
Delete $INSTDIR\Uninstall.exe
RMDir $INSTDIR
Delete "$SMPROGRAMS\Boggle\Boggle.lnk"
Delete "$SMPROGRAMS\Boggle\Uninstall.lnk"
RMDir "$SMPROGRAMS\Boggle"

SectionEnd
