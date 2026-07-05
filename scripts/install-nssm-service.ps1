# Install Persistent Agent Platform as Windows Service (NSSM)
# Author: SIVARAMAN R <sivaram311@gmail.com>
# Run as Administrator

$NssmDir = "C:\nssm"
$NssmExe = Join-Path $NssmDir "nssm.exe"
$ProjectRoot = "E:\MyWorkspace\persistent-agent-platform"
$JarPath = Join-Path $ProjectRoot "target\persistent-agent-platform-0.1.0-SNAPSHOT.jar"
$JavaHome = (Get-Command java).Source

Write-Host "Building JAR..."
Set-Location $ProjectRoot
mvn -q package -DskipTests

if (-not (Test-Path $NssmExe)) {
    Write-Host "Downloading NSSM..."
    $zip = "$env:TEMP\nssm.zip"
    Invoke-WebRequest -Uri "https://nssm.cc/release/nssm-2.24.zip" -OutFile $zip
    Expand-Archive $zip -DestinationPath $NssmDir -Force
    $NssmExe = Get-ChildItem -Path $NssmDir -Recurse -Filter "nssm.exe" | Select-Object -First 1 -ExpandProperty FullName
}

$ServiceName = "PersistentAgentPlatform"

Write-Host "Installing service: $ServiceName"
& $NssmExe install $ServiceName $JavaHome "-jar" $JarPath
& $NssmExe set $ServiceName AppDirectory $ProjectRoot
& $NssmExe set $ServiceName AppEnvironmentExtra "SPRING_PROFILES_ACTIVE=local"
& $NssmExe set $ServiceName DisplayName "Persistent Agent Platform"
& $NssmExe set $ServiceName Description "Self-hosted AI agent platform by SIVARAMAN R"
& $NssmExe set $ServiceName Start SERVICE_AUTO_START

Write-Host "Starting service..."
net start $ServiceName
Write-Host "Done. Service $ServiceName installed."
