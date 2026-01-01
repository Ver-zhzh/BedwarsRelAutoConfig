$ErrorActionPreference = "Continue"

# Create directory
$tempDir = "build/CraftBukkit_Temp"
if (!(Test-Path -Path $tempDir)) {
    New-Item -ItemType Directory -Force -Path $tempDir | Out-Null
}

# Define dependencies with potential mirrors
# Note: Finding public downloads for old CraftBukkit jars is hard due to DMCA. 
# Using some known public repositories that might host them for dev purposes.
$dependencies = @(
    @{ 
        Version = "1.8-R0.1-SNAPSHOT"; 
        File = "craftbukkit-1.8-R0.1-SNAPSHOT.jar";
        Urls = @(
            "https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.8-R0.1-SNAPSHOT-latest.jar",
            "https://hub.spigotmc.org/nexus/content/repositories/snapshots/org/bukkit/craftbukkit/1.8-R0.1-SNAPSHOT/craftbukkit-1.8-R0.1-SNAPSHOT.jar"
        )
    },
    @{ 
        Version = "1.8.3-R0.1-SNAPSHOT"; 
        File = "craftbukkit-1.8.3-R0.1-SNAPSHOT.jar";
        Urls = @(
            "https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.8.3-R0.1-SNAPSHOT-latest.jar"
        )
    },
    @{ 
        Version = "1.8.8-R0.1-SNAPSHOT"; 
        File = "craftbukkit-1.8.8-R0.1-SNAPSHOT.jar";
        Urls = @(
            "https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.8.8-R0.1-SNAPSHOT-latest.jar",
            "https://repo.codemc.io/repository/nms/org/bukkit/craftbukkit/1.8.8-R0.1-SNAPSHOT/craftbukkit-1.8.8-R0.1-SNAPSHOT.jar"
        )
    },
    @{ 
        Version = "1.9.2-R0.1-SNAPSHOT"; 
        File = "craftbukkit-1.9.2-R0.1-SNAPSHOT.jar";
        Urls = @(
            "http://maven.elmakers.com/repository/org/bukkit/craftbukkit/1.9.2-R0.1-SNAPSHOT/craftbukkit-1.9.2-R0.1-SNAPSHOT.jar",
            "https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.9.2-R0.1-SNAPSHOT-latest.jar"
        )
    },
    @{ 
        Version = "1.9.4-R0.1-SNAPSHOT"; 
        File = "craftbukkit-1.9.4-R0.1-SNAPSHOT.jar";
        Urls = @(
            "http://maven.elmakers.com/repository/org/bukkit/craftbukkit/1.9.4-R0.1-SNAPSHOT/craftbukkit-1.9.4-R0.1-SNAPSHOT.jar",
            "https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.9.4-R0.1-SNAPSHOT-latest.jar"
        )
    },
    @{ 
        Version = "1.10.2-R0.1-SNAPSHOT"; 
        File = "craftbukkit-1.10.2-R0.1-SNAPSHOT.jar";
        Urls = @(
            "http://maven.elmakers.com/repository/org/bukkit/craftbukkit/1.10.2-R0.1-SNAPSHOT/craftbukkit-1.10.2-R0.1-SNAPSHOT.jar",
            "https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.10.2-R0.1-SNAPSHOT-latest.jar"
        )
    },
    @{ 
        Version = "1.11.2-R0.1-SNAPSHOT"; 
        File = "craftbukkit-1.11.2-R0.1-SNAPSHOT.jar";
        Urls = @(
            "https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.11.2.jar",
            "http://maven.elmakers.com/repository/org/bukkit/craftbukkit/1.11.2-R0.1-SNAPSHOT/craftbukkit-1.11.2-R0.1-SNAPSHOT.jar"
        )
    },
    @{ 
        Version = "1.12-R0.1-SNAPSHOT"; 
        File = "craftbukkit-1.12-R0.1-SNAPSHOT.jar";
        Urls = @(
            "https://cdn.getbukkit.org/craftbukkit/craftbukkit-1.12.jar",
            "http://maven.elmakers.com/repository/org/bukkit/craftbukkit/1.12-R0.1-SNAPSHOT/craftbukkit-1.12-R0.1-SNAPSHOT.jar"
        )
    }
)

$failed = @()

foreach ($dep in $dependencies) {
    $outputPath = Join-Path $tempDir $dep.File
    $downloadSuccess = $false
    
    if (Test-Path $outputPath) {
        Write-Host "File for $($dep.Version) already exists ($outputPath)."
        $downloadSuccess = $true
    } else {
        foreach ($url in $dep.Urls) {
            Write-Host "Attempting to download $($dep.Version) from $url ..."
            try {
                [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
                Invoke-WebRequest -Uri $url -OutFile $outputPath
                $downloadSuccess = $true
                Write-Host "Download successful!"
                break # Stop trying other URLs
            } catch {
                Write-Warning "Failed to download from $url"
            }
        }
    }
    
    if ($downloadSuccess) {
        Write-Host "Installing $($dep.Version) to local Maven repo..."
        # Using & operator and quoting arguments properly for PowerShell
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
        if ($LASTEXITCODE -eq 0) {
             Write-Host "Successfully installed $($dep.Version)" -ForegroundColor Green
        } else {
             Write-Error "Maven install failed for $($dep.Version)"
             $failed += $dep.Version
        }
    } else {
        Write-Error "Could not download $($dep.Version) from any source."
        $failed += $dep.Version
    }
}

if ($failed.Count -gt 0) {
    Write-Warning "The following versions failed to process: $($failed -join ', ')"
} else {
    Write-Host "All dependencies processed successfully!" -ForegroundColor Green
}