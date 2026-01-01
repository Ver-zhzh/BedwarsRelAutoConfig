$ErrorActionPreference = "Continue"

$tempDir = "build/CraftBukkit_Temp"
if (!(Test-Path -Path $tempDir)) {
    New-Item -ItemType Directory -Force -Path $tempDir | Out-Null
}

$dependencies = @(
    @{ 
        Version = "1.8-R0.1-SNAPSHOT"; 
        File = "craftbukkit-1.8-R0.1-SNAPSHOT.jar";
        Urls = @(
            "http://maven.elmakers.com/repository/org/bukkit/craftbukkit/1.8-R0.1-SNAPSHOT/craftbukkit-1.8-R0.1-SNAPSHOT.jar",
            "https://repo.dmulloy2.net/repository/public/org/bukkit/craftbukkit/1.8-R0.1-SNAPSHOT/craftbukkit-1.8-R0.1-SNAPSHOT.jar"
        )
    },
    @{ 
        Version = "1.8.3-R0.1-SNAPSHOT"; 
        File = "craftbukkit-1.8.3-R0.1-SNAPSHOT.jar";
        Urls = @(
            "http://maven.elmakers.com/repository/org/bukkit/craftbukkit/1.8.3-R0.1-SNAPSHOT/craftbukkit-1.8.3-R0.1-SNAPSHOT.jar",
            "https://repo.dmulloy2.net/repository/public/org/bukkit/craftbukkit/1.8.3-R0.1-SNAPSHOT/craftbukkit-1.8.3-R0.1-SNAPSHOT.jar"
        )
    },
    @{ 
        Version = "1.8.8-R0.1-SNAPSHOT"; 
        File = "craftbukkit-1.8.8-R0.1-SNAPSHOT.jar";
        Urls = @(
            "https://repo.codemc.io/repository/nms/org/bukkit/craftbukkit/1.8.8-R0.1-SNAPSHOT/craftbukkit-1.8.8-R0.1-SNAPSHOT.jar",
            "http://maven.elmakers.com/repository/org/bukkit/craftbukkit/1.8.8-R0.1-SNAPSHOT/craftbukkit-1.8.8-R0.1-SNAPSHOT.jar",
            "https://repo.dmulloy2.net/repository/public/org/bukkit/craftbukkit/1.8.8-R0.1-SNAPSHOT/craftbukkit-1.8.8-R0.1-SNAPSHOT.jar",
            "https://repo.yumemc.com/repository/maven-public/org/bukkit/craftbukkit/1.8.8-R0.1-SNAPSHOT/craftbukkit-1.8.8-R0.1-SNAPSHOT.jar"
        )
    }
)

foreach ($dep in $dependencies) {
    $outputPath = Join-Path $tempDir $dep.File
    $downloadSuccess = $false
    
    if (Test-Path $outputPath) {
        $downloadSuccess = $true
    } else {
        foreach ($url in $dep.Urls) {
            Write-Host "Trying $($dep.Version) from $url ..."
            try {
                [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
                Invoke-WebRequest -Uri $url -OutFile $outputPath -TimeoutSec 30
                $downloadSuccess = $true
                Write-Host "Success!"
                break
            } catch {
                # Silently try next URL
            }
        }
    }
    
    if ($downloadSuccess) {
        Write-Host "Installing $($dep.Version)..."
        $mvnArgs = @(
            "install:install-file",
            "-Dfile=$outputPath",
            "-DgroupId=org.bukkit",
            "-DartifactId=craftbukkit",
            "-Dversion=$($dep.Version)",
            "-Dpackaging=jar",
            "-DgeneratePom=true"
        )
        & mvn $mvnArgs
    } else {
        Write-Warning "Failed to obtain $($dep.Version)"
    }
}
